package axismed.service;

import axismed.enums.AppointmentStatus;
import axismed.enums.AppointmentType;
import axismed.enums.ClearanceResult;
import axismed.enums.ClearanceType;
import axismed.enums.ResultType;
import axismed.enums.Sport;
import axismed.enums.TreatmentStatus;
import axismed.enums.UserRole;
import axismed.exception.DuplicateRecordException;
import axismed.exception.InvalidClearanceException;
import axismed.exception.PatientNotFoundException;
import axismed.exception.SchedulingConflictException;
import axismed.exception.StaffNotFoundException;
import axismed.exception.UnauthorizedActionException;
import axismed.model.appointment.Appointment;
import axismed.model.appointment.ClearanceTest;
import axismed.model.clinic.Clinic;
import axismed.model.event.SportingEvent;
import axismed.model.medical.Diagnosis;
import axismed.model.medical.FunctionalTest;
import axismed.model.medical.MedicalRecord;
import axismed.model.medical.Medication;
import axismed.model.medical.RehabilitationProtocol;
import axismed.model.medical.TreatmentPlan;
import axismed.model.patient.Patient;
import axismed.model.patient.TeamDelegate;
import axismed.model.staff.Doctor;
import axismed.model.staff.Staff;
import axismed.repository.AppointmentRepository;
import axismed.repository.DiagnosisRepository;
import axismed.repository.MedicalRecordRepository;
import axismed.repository.PatientRepository;
import axismed.repository.StaffRepository;
import axismed.repository.TreatmentPlanRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClinicService {

    private final PatientRepository      patientRepository;
    private final StaffRepository        staffRepository;
    private final AppointmentRepository  appointmentRepository;
    private final DiagnosisRepository    diagnosisRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AuditService           auditService;
    private final AuthService            authService;

    // In-memory store for sporting events (no CSV repository in spec)
    private final HashMap<String, SportingEvent> sportingEvents = new HashMap<>();

    // Clinic lookup by city name — populated via addClinic() called from Main
    private final HashMap<String, Clinic> clinics = new HashMap<>();

    public ClinicService(PatientRepository patientRepository,
                         StaffRepository staffRepository,
                         AppointmentRepository appointmentRepository,
                         DiagnosisRepository diagnosisRepository,
                         TreatmentPlanRepository treatmentPlanRepository,
                         MedicalRecordRepository medicalRecordRepository) {
        this.patientRepository       = patientRepository;
        this.staffRepository         = staffRepository;
        this.appointmentRepository   = appointmentRepository;
        this.diagnosisRepository     = diagnosisRepository;
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.auditService            = AuditService.getInstance();
        this.authService             = AuthService.getInstance();
        linkLoadedData();
    }

    // -------------------------------------------------------------------------
    // Startup linking — wire loaded CSV data into in-memory object graph
    // -------------------------------------------------------------------------

    private void linkLoadedData() {
        // Link medical records to patients
        for (MedicalRecord record : medicalRecordRepository.findAll()) {
            Patient patient = patientRepository.findById(record.getPatientId());
            if (patient != null) {
                patient.setMedicalRecord(record);
            }
        }

        // Link diagnoses to patients and their medical records
        for (Diagnosis diagnosis : diagnosisRepository.findAll()) {
            Patient patient = patientRepository.findById(diagnosis.getPatientId());
            if (patient != null) {
                patient.addDiagnosis(diagnosis);
                if (patient.getMedicalRecord() != null) {
                    patient.getMedicalRecord().addDiagnosisEntry(diagnosis);
                }
            }
        }

        // Link treatment plans to patients and their medical records
        for (TreatmentPlan plan : treatmentPlanRepository.findAll()) {
            Patient patient = patientRepository.findById(plan.getPatientId());
            if (patient != null) {
                patient.addTreatmentPlan(plan);
                if (patient.getMedicalRecord() != null) {
                    patient.getMedicalRecord().addTreatmentEntry(plan);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Clinic registry
    // -------------------------------------------------------------------------

    public void addClinic(Clinic clinic) {
        clinics.put(clinic.getCity().toLowerCase(), clinic);
    }

    public Clinic findClinicByCity(String city) {
        return clinics.getOrDefault(city.toLowerCase(),
               new Clinic("", city, "", "", ""));
    }

    // -------------------------------------------------------------------------
    // Helper utilities
    // -------------------------------------------------------------------------

    private String newId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String currentEmail() {
        return authService.getCurrentUserEmail();
    }

    private void requireRole(UserRole role, String action) throws UnauthorizedActionException {
        if (!authService.hasPermission(role)) {
            throw new UnauthorizedActionException(authService.getCurrentRole(), action);
        }
    }

    private void requireAnyRole(String action, UserRole... roles) throws UnauthorizedActionException {
        UserRole current = authService.getCurrentRole();
        for (UserRole r : roles) {
            if (current == r) return;
        }
        throw new UnauthorizedActionException(current, action);
    }

    // -------------------------------------------------------------------------
    // 1. Patient Management
    // -------------------------------------------------------------------------

    public void registerPatient(Patient patient) throws DuplicateRecordException, UnauthorizedActionException {
        requireRole(UserRole.ADMIN, "registerPatient");

        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new DuplicateRecordException("Patient with email " + patient.getEmail() + " already exists");
        }

        // Create and attach a fresh medical record
        MedicalRecord record = new MedicalRecord("REC-" + newId(), patient.getPatientId(), LocalDate.now());
        record.setLastUpdatedByClinic(
            authService.getLoggedInStaff() != null
                ? authService.getLoggedInStaff().getHomeClinic() != null
                    ? authService.getLoggedInStaff().getHomeClinic().getCity()
                    : "Unknown"
                : "Unknown"
        );
        patient.setMedicalRecord(record);

        patientRepository.save(patient);
        medicalRecordRepository.save(record);

        auditService.log("REGISTER_PATIENT", currentEmail());
    }

    public void assignStaffToPatient(String patientId, String staffId)
            throws PatientNotFoundException, StaffNotFoundException, UnauthorizedActionException {
        requireRole(UserRole.ADMIN, "assignStaffToPatient");

        Patient patient = patientRepository.findById(patientId);
        if (patient == null) throw new PatientNotFoundException(patientId);

        Staff staff = staffRepository.findById(staffId);
        if (staff == null) throw new StaffNotFoundException(staffId);

        if (staff instanceof Doctor) {
            ((Doctor) staff).addPatient(patient);
        }

        auditService.log("ASSIGN_STAFF_TO_PATIENT", currentEmail());
    }

    // -------------------------------------------------------------------------
    // 2. Appointments
    // -------------------------------------------------------------------------

    public Appointment scheduleAppointment(String patientId, String staffId, String clinicCity,
            LocalDateTime dateTime, AppointmentType type)
            throws SchedulingConflictException, PatientNotFoundException, UnauthorizedActionException {
        requireAnyRole("scheduleAppointment", UserRole.ADMIN, UserRole.DOCTOR);

        Patient patient = patientRepository.findById(patientId);
        if (patient == null) throw new PatientNotFoundException(patientId);

        Staff staff = staffRepository.findById(staffId);
        if (staff == null) {
            auditService.log("SCHEDULE_APPOINTMENT_FAILED", currentEmail());
            throw new SchedulingConflictException("Staff " + staffId + " not found");
        }

        // Conflict check — same staff, overlapping time (within 1 hour)
        for (Appointment existing : staff.getAppointments()) {
            long minutesDiff = Math.abs(
                existing.getDateTime().toLocalTime().toSecondOfDay()
                - dateTime.toLocalTime().toSecondOfDay()) / 60;
            if (existing.getDateTime().toLocalDate().equals(dateTime.toLocalDate())
                    && minutesDiff < 60
                    && existing.getStatus() == AppointmentStatus.SCHEDULED) {
                throw new SchedulingConflictException(
                    "Staff " + staffId + " already has an appointment within 1 hour of " + dateTime);
            }
        }

        if (!clinics.containsKey(clinicCity.toLowerCase())) {
            throw new IllegalArgumentException("Unknown clinic city: " + clinicCity);
        }
        Clinic clinic = findClinicByCity(clinicCity);
        String appointmentId = "APT-" + newId();
        Appointment appointment = new Appointment(appointmentId, patient, staff, clinic,
                                                  dateTime, type, AppointmentStatus.SCHEDULED, "");

        appointmentRepository.save(appointment);
        patient.getAppointments().add(appointment);
        staff.scheduleAppointment(appointment);
        clinic.addAppointment(appointment);

        if (patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().addAppointmentEntry(appointment);
            patient.getMedicalRecord().setLastUpdatedByClinic(clinicCity);
            medicalRecordRepository.update(patient.getMedicalRecord());
        }

        auditService.log("SCHEDULE_APPOINTMENT", currentEmail());
        return appointment;
    }

    public void cancelAppointment(String appointmentId) throws UnauthorizedActionException {
        requireAnyRole("cancelAppointment", UserRole.ADMIN, UserRole.DOCTOR);

        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment != null) {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.update(appointment);
        }

        auditService.log("CANCEL_APPOINTMENT", currentEmail());
    }

    public void rescheduleAppointment(String appointmentId, LocalDateTime newDateTime)
            throws SchedulingConflictException {
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            auditService.log("RESCHEDULE_APPOINTMENT_FAILED", currentEmail());
            return;
        }

        Staff staff = appointment.getStaff();
        if (staff != null) {
            for (Appointment existing : staff.getAppointments()) {
                if (existing.getAppointmentId().equals(appointmentId)) continue;
                long minutesDiff = Math.abs(
                    existing.getDateTime().toLocalTime().toSecondOfDay()
                    - newDateTime.toLocalTime().toSecondOfDay()) / 60;
                if (existing.getDateTime().toLocalDate().equals(newDateTime.toLocalDate())
                        && minutesDiff < 60
                        && existing.getStatus() == AppointmentStatus.SCHEDULED) {
                    throw new SchedulingConflictException(
                        "New time conflicts with an existing appointment for staff " + staff.getStaffId());
                }
            }
        }

        appointment.setDateTime(newDateTime);
        appointmentRepository.update(appointment);
        auditService.log("RESCHEDULE_APPOINTMENT", currentEmail());
    }

    // -------------------------------------------------------------------------
    // 3. Medical
    // -------------------------------------------------------------------------

    public Diagnosis recordDiagnosis(String appointmentId, String doctorId,
            String diagnosisName, String description, String icdCode)
            throws UnauthorizedActionException, PatientNotFoundException {
        requireRole(UserRole.DOCTOR, "recordDiagnosis");

        Staff staff = staffRepository.findById(doctorId);
        if (staff == null || !staff.canDiagnose()) {
            throw new UnauthorizedActionException(authService.getCurrentRole(),
                "recordDiagnosis — staff is not qualified to diagnose");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            throw new PatientNotFoundException("Appointment not found: " + appointmentId);
        }
        String patientId  = appointment.getPatient() != null
                            ? appointment.getPatient().getPatientId() : "UNKNOWN";
        String clinicCity = appointment.getClinic() != null
                            ? appointment.getClinic().getCity() : "Unknown";

        String diagnosisId = "DGN-" + newId();
        Diagnosis diagnosis = new Diagnosis(diagnosisId, patientId, doctorId, appointmentId,
                                            clinicCity, diagnosisName, description, icdCode,
                                            LocalDate.now(), false);

        diagnosisRepository.save(diagnosis);

        Patient patient = patientRepository.findById(patientId);
        if (patient != null) {
            patient.addDiagnosis(diagnosis);
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().addDiagnosisEntry(diagnosis);
                patient.getMedicalRecord().setLastUpdatedByClinic(clinicCity);
                medicalRecordRepository.update(patient.getMedicalRecord());
            }
        }

        auditService.log("RECORD_DIAGNOSIS", currentEmail());
        return diagnosis;
    }

    public TreatmentPlan createTreatmentPlan(String patientId, String staffId,
            String diagnosisId, String recommendations)
            throws UnauthorizedActionException {
        requireRole(UserRole.DOCTOR, "createTreatmentPlan");

        Staff staff = staffRepository.findById(staffId);
        if (staff == null || !staff.canWriteTreatmentPlan()) {
            throw new UnauthorizedActionException(authService.getCurrentRole(),
                "createTreatmentPlan — staff is not qualified to write treatment plans");
        }

        Patient patient = patientRepository.findById(patientId);
        String planId = "PLN-" + newId();
        TreatmentPlan plan = new TreatmentPlan(planId, patientId, staffId, diagnosisId,
                                               LocalDate.now(), null,
                                               TreatmentStatus.ACTIVE, recommendations);

        treatmentPlanRepository.save(plan);

        if (patient != null) {
            patient.addTreatmentPlan(plan);
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().addTreatmentEntry(plan);
                medicalRecordRepository.update(patient.getMedicalRecord());
            }
        }

        auditService.log("CREATE_TREATMENT_PLAN", currentEmail());
        return plan;
    }

    public void addRehabProtocol(String planId, RehabilitationProtocol protocol) {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId);
        if (plan != null) {
            plan.setRehabProtocol(protocol);
            treatmentPlanRepository.update(plan);
        }
        auditService.log("ADD_REHAB_PROTOCOL", currentEmail());
    }

    public FunctionalTest recordFunctionalTest(String patientId, String staffId,
            String appointmentId, String testName, ResultType resultType,
            String resultValue, String unit, String notes) {
        String testId = "TST-" + newId();
        FunctionalTest test = new FunctionalTest(testId, patientId, staffId, appointmentId,
                                                 testName, resultType, resultValue, unit,
                                                 LocalDate.now(), resolveClinicCity(staffId),
                                                 notes != null ? notes : "", null);

        Patient patient = patientRepository.findById(patientId);
        if (patient != null) {
            patient.addFunctionalTest(test);
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().addTestEntry(test);
                medicalRecordRepository.update(patient.getMedicalRecord());
            }
        }

        auditService.log("RECORD_FUNCTIONAL_TEST", currentEmail());
        return test;
    }

    public void prescribeMedication(String patientId, String staffId, Medication medication)
            throws UnauthorizedActionException {
        requireRole(UserRole.DOCTOR, "prescribeMedication");

        Staff staff = staffRepository.findById(staffId);
        if (staff == null || !staff.canDiagnose()) {
            throw new UnauthorizedActionException(authService.getCurrentRole(),
                "prescribeMedication — only doctors can prescribe medication");
        }

        Patient patient = patientRepository.findById(patientId);
        if (patient != null) {
            patient.addMedication(medication);
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().addMedicationEntry(medication);
                medicalRecordRepository.update(patient.getMedicalRecord());
            }
        }

        auditService.log("PRESCRIBE_MEDICATION", currentEmail());
    }

    public void updateMedicalRecord(String patientId, String note) {
        Patient patient = patientRepository.findById(patientId);
        if (patient != null && patient.getMedicalRecord() != null) {
            patient.getMedicalRecord().addNote(note);
            medicalRecordRepository.update(patient.getMedicalRecord());
        }
        auditService.log("UPDATE_MEDICAL_RECORD", currentEmail());
    }

    // -------------------------------------------------------------------------
    // 4. Queries
    // -------------------------------------------------------------------------

    public MedicalRecord viewFullPatientHistory(String patientId)
            throws PatientNotFoundException, UnauthorizedActionException {
        UserRole role = authService.getCurrentRole();

        if (role == UserRole.TEAM_DELEGATE) {
            throw new UnauthorizedActionException(role, "viewFullPatientHistory");
        }
        if (role == UserRole.PATIENT) {
            // Patients may only view their own record
            if (!patientId.equals(authService.getCurrentUserId())) {
                throw new UnauthorizedActionException(role,
                    "viewFullPatientHistory — patients can only view their own record");
            }
        }

        Patient patient = patientRepository.findById(patientId);
        if (patient == null) throw new PatientNotFoundException(patientId);

        auditService.log("VIEW_PATIENT_HISTORY", currentEmail());
        return patient.getMedicalRecord();
    }

    public List<Appointment> viewStaffUpcomingAppointments(String staffId)
            throws StaffNotFoundException {
        Staff staff = staffRepository.findById(staffId);
        if (staff == null) throw new StaffNotFoundException(staffId);

        auditService.log("VIEW_STAFF_APPOINTMENTS", currentEmail());
        return staff.getUpcomingAppointments();
    }

    public void trackTreatmentPlanProgress(String planId, String progressNote) {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId);
        if (plan != null) {
            plan.addProgressNote(progressNote);
            treatmentPlanRepository.update(plan);
        }
        auditService.log("TRACK_TREATMENT_PROGRESS", currentEmail());
    }

    public List<Patient> searchPatients(String query) {
        List<Patient> results = patientRepository.search(query);
        String q = query.toLowerCase();
        Set<String> found = results.stream()
            .map(Patient::getPatientId)
            .collect(Collectors.toCollection(HashSet::new));
        for (Diagnosis d : diagnosisRepository.findAll()) {
            if (d.getDiagnosisName().toLowerCase().contains(q) && !found.contains(d.getPatientId())) {
                Patient p = patientRepository.findById(d.getPatientId());
                if (p != null) {
                    results.add(p);
                    found.add(d.getPatientId());
                }
            }
        }
        auditService.log("SEARCH_PATIENTS", currentEmail());
        return results;
    }

    // -------------------------------------------------------------------------
    // 5. Events
    // -------------------------------------------------------------------------

    public SportingEvent registerSportingEvent(String eventName, Sport sport,
            String hostTeam, String visitingTeam, String clinicCity,
            LocalDate start, LocalDate end, TeamDelegate delegate) {
        if (!clinics.containsKey(clinicCity.toLowerCase())) {
            throw new IllegalArgumentException("Unknown clinic city: " + clinicCity);
        }
        Clinic clinic = findClinicByCity(clinicCity);
        String eventId = "EVT-" + newId();
        SportingEvent event = new SportingEvent(eventId, eventName, sport, hostTeam,
                                                visitingTeam, clinic, start, end, delegate);
        sportingEvents.put(eventId, event);

        if (delegate != null) {
            delegate.setCurrentEvent(event);
        }

        auditService.log("REGISTER_SPORTING_EVENT", currentEmail());
        return event;
    }

    public void linkAthleteToEvent(String patientId, String eventId) {
        SportingEvent event = sportingEvents.get(eventId);
        Patient patient = patientRepository.findById(patientId);
        if (event != null && patient != null) {
            event.registerAthlete(patient);
            if (event.getDelegate() != null) {
                event.getDelegate().addAthlete(patient);
                event.getDelegate().setCurrentEvent(event);
            }
        }
        auditService.log("LINK_ATHLETE_TO_EVENT", currentEmail());
    }

    public SportingEvent findEventById(String eventId) {
        return sportingEvents.get(eventId);
    }

    public List<SportingEvent> getAllEvents() {
        return new ArrayList<>(sportingEvents.values());
    }

    // -------------------------------------------------------------------------
    // 6. Clearance
    // -------------------------------------------------------------------------

    public ClearanceTest recordClearanceTest(String patientId, String staffId,
            ClearanceType type, ClearanceResult result, LocalDate validUntil, String officialNotes)
            throws InvalidClearanceException {
        if (result == ClearanceResult.CLEARED && validUntil == null) {
            throw new InvalidClearanceException(
                "A CLEARED result must have a validUntil date");
        }

        Patient patient = patientRepository.findById(patientId);
        Staff staff     = staffRepository.findById(staffId);
        Clinic clinic   = (staff != null && staff.getHomeClinic() != null)
                          ? staff.getHomeClinic() : new Clinic("", "Unknown", "", "", "");

        String appointmentId = "APT-" + newId();
        AppointmentType apptType = (type == ClearanceType.DOPING_SCREEN)
                                   ? AppointmentType.DOPING_SCREEN
                                   : AppointmentType.MEDICAL_CLEARANCE;

        ClearanceTest clearanceTest = new ClearanceTest(
            appointmentId, patient, staff, clinic,
            LocalDateTime.now(), AppointmentStatus.COMPLETED,
            type, result,
            staff != null ? staff.getFullName() : staffId,
            validUntil,
            officialNotes != null && !officialNotes.isEmpty()
                ? officialNotes : type.name() + " for " + patientId
        );

        appointmentRepository.save(clearanceTest);

        if (patient != null) {
            patient.getAppointments().add(clearanceTest);
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().addAppointmentEntry(clearanceTest);
                medicalRecordRepository.update(patient.getMedicalRecord());
            }
        }

        auditService.log("RECORD_CLEARANCE_TEST", currentEmail());
        return clearanceTest;
    }

    // -------------------------------------------------------------------------
    // 7. Report
    // -------------------------------------------------------------------------

    public String generatePerformanceReport(String patientId) throws PatientNotFoundException {
        Patient patient = patientRepository.findById(patientId);
        if (patient == null) throw new PatientNotFoundException(patientId);

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("   AXISMED ELITE — PERFORMANCE REPORT\n");
        sb.append("====================================================\n");
        sb.append("Patient : ").append(patient.getFullName()).append("\n");
        sb.append("ID      : ").append(patient.getPatientId()).append("\n");
        sb.append("Age     : ").append(patient.getAge()).append("\n");
        sb.append("Sport   : ").append(patient.getPrimarySport() != null
                                      ? patient.getPrimarySport() : "N/A").append("\n");
        sb.append("Type    : ").append(patient.getPatientType()).append("\n");
        sb.append("Member  : ").append(patient.getMembershipType()).append("\n");

        if (!patient.getChronicConditions().isEmpty()) {
            sb.append("Chronic : ").append(String.join(", ", patient.getChronicConditions())).append("\n");
        }

        sb.append("\n--- Diagnoses (").append(patient.getDiagnoses().size()).append(") ---\n");
        for (Diagnosis d : patient.getDiagnoses()) {
            sb.append("  [").append(d.getDiagnosisDate()).append("] ")
              .append(d.getDiagnosisName());
            if (d.getIcdCode() != null && !d.getIcdCode().isEmpty()) {
                sb.append(" (").append(d.getIcdCode()).append(")");
            }
            sb.append("\n");
        }

        sb.append("\n--- Treatment Plans (").append(patient.getTreatmentPlans().size()).append(") ---\n");
        for (TreatmentPlan p : patient.getTreatmentPlans()) {
            sb.append("  [").append(p.getPlanId()).append("] Status: ").append(p.getStatus())
              .append(" | Start: ").append(p.getStartDate()).append("\n");
            if (p.getFreeTextRecommendations() != null && !p.getFreeTextRecommendations().isEmpty()) {
                sb.append("    Recommendations: ").append(p.getFreeTextRecommendations()).append("\n");
            }
        }

        sb.append("\n--- Functional Tests (").append(patient.getFunctionalTests().size()).append(") ---\n");
        for (FunctionalTest t : patient.getFunctionalTests()) {
            sb.append("  [").append(t.getTestDate()).append("] ")
              .append(t.getTestName()).append(": ").append(t.getResultValue());
            if (t.getUnit() != null && !t.getUnit().isEmpty()) {
                sb.append(" ").append(t.getUnit());
            }
            sb.append("\n");
        }

        sb.append("\n--- Medications (").append(patient.getMedications().size()).append(") ---\n");
        for (Medication m : patient.getMedications()) {
            sb.append("  ").append(m.getName()).append(" — ").append(m.getDosage())
              .append(", ").append(m.getFrequency())
              .append(m.isSupplement() ? " [supplement]" : " [pharmaceutical]").append("\n");
        }

        sb.append("\n--- Appointments (").append(patient.getAppointments().size()).append(") ---\n");
        for (Appointment a : patient.getAppointments()) {
            sb.append("  [").append(a.getDateTime().toLocalDate()).append("] ")
              .append(a.getType()).append(" — ").append(a.getStatus()).append("\n");
        }

        sb.append("====================================================\n");

        auditService.log("GENERATE_PERFORMANCE_REPORT", currentEmail());
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Staff registration (called from Admin menu)
    // -------------------------------------------------------------------------

    public void registerStaff(Staff staff) throws DuplicateRecordException, UnauthorizedActionException {
        requireRole(UserRole.ADMIN, "registerStaff");
        if (staffRepository.existsByEmail(staff.getEmail())) {
            throw new DuplicateRecordException("Staff with email " + staff.getEmail() + " already exists");
        }
        staffRepository.save(staff);
        auditService.log("REGISTER_STAFF", currentEmail());
    }

    // Athlete registration by a TeamDelegate
    public void registerAthlete(Patient athlete, TeamDelegate delegate)
            throws DuplicateRecordException {
        if (patientRepository.existsByEmail(athlete.getEmail())) {
            throw new DuplicateRecordException("Athlete with email " + athlete.getEmail() + " already exists");
        }
        MedicalRecord record = new MedicalRecord("REC-" + newId(), athlete.getPatientId(), LocalDate.now());
        record.setLastUpdatedByClinic("Visiting");
        athlete.setMedicalRecord(record);
        patientRepository.save(athlete);
        medicalRecordRepository.save(record);
        if (delegate != null) {
            delegate.addAthlete(athlete);
        }
        auditService.log("REGISTER_ATHLETE", currentEmail());
    }

    // -------------------------------------------------------------------------
    // Convenience accessors for menus
    // -------------------------------------------------------------------------

    public Patient findPatientById(String patientId) {
        return patientRepository.findById(patientId);
    }

    public Staff findStaffById(String staffId) {
        return staffRepository.findById(staffId);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<Appointment> getAppointmentsForClinic(String clinicCity) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointmentRepository.findAll()) {
            if (a.getClinic() != null && a.getClinic().getCity().equalsIgnoreCase(clinicCity)) {
                result.add(a);
            }
        }
        return result;
    }

    public TreatmentPlan findTreatmentPlanById(String planId) {
        return treatmentPlanRepository.findById(planId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String resolveClinicCity(String staffId) {
        Staff staff = staffRepository.findById(staffId);
        if (staff != null && staff.getHomeClinic() != null) {
            return staff.getHomeClinic().getCity();
        }
        return "Unknown";
    }
}
