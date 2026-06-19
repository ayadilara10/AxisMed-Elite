package axismed.util;

import axismed.enums.AppointmentType;
import axismed.enums.ClearanceResult;
import axismed.enums.ClearanceType;
import axismed.enums.Gender;
import axismed.enums.MedicalSpecialization;
import axismed.enums.MembershipType;
import axismed.enums.PatientType;
import axismed.enums.ResultType;
import axismed.enums.Sport;
import axismed.enums.StaffRole;
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
import axismed.model.medical.Diagnosis;
import axismed.model.medical.Exercise;
import axismed.model.medical.FunctionalTest;
import axismed.model.medical.MedicalRecord;
import axismed.model.medical.Medication;
import axismed.model.medical.RehabilitationProtocol;
import axismed.model.medical.TreatmentPlan;
import axismed.model.patient.Patient;
import axismed.model.patient.TeamDelegate;
import axismed.model.staff.Doctor;
import axismed.model.staff.Nutritionist;
import axismed.model.staff.Physiotherapist;
import axismed.model.staff.Staff;
import axismed.model.staff.Trainer;
import axismed.repository.CSVReaderService;
import axismed.repository.CSVWriterService;
import axismed.service.AuthService;
import axismed.service.ClinicService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleMenu {

    private static final String USERS_FILE = "data/users.csv";

    private final ClinicService clinicService;
    private final AuthService   authService;
    private final Scanner       scanner;

    public ConsoleMenu(ClinicService clinicService) {
        this.clinicService = clinicService;
        this.authService   = AuthService.getInstance();
        this.scanner       = new Scanner(System.in);
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    public void start() {
        printBanner();
        while (true) {
            if (showLoginScreen()) {
                routeToMenu();
            }
        }
    }

    private void printBanner() {
        System.out.println("=====================================================");
        System.out.println("       AXISMED ELITE — Sports Medicine System        ");
        System.out.println("=====================================================");
    }

    private boolean showLoginScreen() {
        System.out.println("\n--- LOGIN ---");
        String email    = prompt("Email   : ");
        String password = prompt("Password: ");

        if (authService.login(email, password)) {
            System.out.println("\nWelcome! Logged in as " + authService.getCurrentRole());
            return true;
        }
        System.out.println("Invalid credentials. Please try again.");
        return false;
    }

    private void routeToMenu() {
        UserRole role = authService.getCurrentRole();
        switch (role) {
            case ADMIN:         showAdminMenu();    break;
            case DOCTOR:        showDoctorMenu();   break;
            case PATIENT:       showPatientMenu();  break;
            case TEAM_DELEGATE: showDelegateMenu(); break;
        }
    }

    // -------------------------------------------------------------------------
    // ADMIN menu
    // -------------------------------------------------------------------------

    private void showAdminMenu() {
        while (true) {
            System.out.println("\n=== AXISMED ELITE | ADMIN ===");
            System.out.println("1.  Register new patient");
            System.out.println("2.  Register new staff member");
            System.out.println("3.  View all patients");
            System.out.println("4.  View all staff");
            System.out.println("5.  Schedule appointment");
            System.out.println("6.  View clinic schedule");
            System.out.println("7.  Register sporting event");
            System.out.println("8.  View all sporting events");
            System.out.println("9.  Search patients");
            System.out.println("10. Generate performance report");
            System.out.println("11. View audit log");
            System.out.println("0.  Logout");

            int choice = promptInt("Select: ");
            if (choice == 0) { authService.logout(); return; }
            handleAdminChoice(choice);
        }
    }

    private void handleAdminChoice(int choice) {
        switch (choice) {
            case 1:  adminRegisterPatient();          break;
            case 2:  adminRegisterStaff();            break;
            case 3:  adminViewAllPatients();          break;
            case 4:  adminViewAllStaff();             break;
            case 5:  adminScheduleAppointment();      break;
            case 6:  adminViewClinicSchedule();       break;
            case 7:  adminRegisterSportingEvent();    break;
            case 8:  adminViewAllEvents();            break;
            case 9:  adminSearchPatients();           break;
            case 10: adminGenerateReport();           break;
            case 11: adminViewAuditLog();             break;
            default: System.out.println("Invalid option.");
        }
    }

    private void adminRegisterPatient() {
        System.out.println("\n-- Register New Patient --");
        String id          = "PAT-" + newId();
        String firstName   = prompt("First name  : ");
        String lastName    = prompt("Last name   : ");
        String email       = prompt("Email       : ");
        String password    = prompt("Password    : ");
        LocalDate dob      = promptDate("Date of birth");
        Gender gender      = promptEnum("gender", Gender.class);
        String phone       = prompt("Phone       : ");
        String nationality = prompt("Nationality : ");
        PatientType pType  = promptEnum("patient type", PatientType.class);
        MembershipType mem = promptEnum("membership", MembershipType.class);
        System.out.print("Primary sport (leave blank if none): ");
        String sportInput  = scanner.nextLine().trim();
        Sport sport = sportInput.isEmpty() ? null : parseEnumSafe(Sport.class, sportInput.toUpperCase());

        Patient patient = new Patient(id, firstName, lastName, email, password,
                                      dob, gender, phone, nationality, pType, mem, sport);

        System.out.print("Chronic conditions (semicolon-separated, or blank): ");
        String conditions = scanner.nextLine().trim();
        if (!conditions.isEmpty()) {
            for (String c : conditions.split(";")) patient.addChronicCondition(c.trim());
        }

        try {
            clinicService.registerPatient(patient);
            appendUserRecord(email, password, UserRole.PATIENT, id);
            System.out.println("Patient registered successfully. ID: " + id);
        } catch (DuplicateRecordException | UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminRegisterStaff() {
        System.out.println("\n-- Register New Staff Member --");
        String id        = "STF-" + newId();
        String firstName = prompt("First name   : ");
        String lastName  = prompt("Last name    : ");
        String email     = prompt("Email        : ");
        String password  = prompt("Password     : ");
        String homeCity  = prompt("Home clinic  : ");
        axismed.model.clinic.Clinic homeClinic =
            new axismed.model.clinic.Clinic("", homeCity, "", "", "");

        StaffRole role = promptEnum("role", StaffRole.class);
        Staff staff;

        switch (role) {
            case DOCTOR: {
                MedicalSpecialization spec = promptEnum("specialization", MedicalSpecialization.class);
                String license = prompt("License number: ");
                staff = new Doctor(id, firstName, lastName, email, password, homeClinic, spec, license);
                break;
            }
            case PHYSIOTHERAPIST: {
                String cert = prompt("Certification number: ");
                staff = new Physiotherapist(id, firstName, lastName, email, password, homeClinic, cert);
                break;
            }
            case NUTRITIONIST:
                staff = new Nutritionist(id, firstName, lastName, email, password, homeClinic);
                break;
            case TRAINER:
                staff = new Trainer(id, firstName, lastName, email, password, homeClinic);
                break;
            default:
                staff = new Doctor(id, firstName, lastName, email, password,
                                   homeClinic, MedicalSpecialization.GENERAL, "");
        }

        try {
            clinicService.registerStaff(staff);
            UserRole userRole = (role == StaffRole.ADMIN) ? UserRole.ADMIN : UserRole.DOCTOR;
            appendUserRecord(email, password, userRole, id);
            System.out.println("Staff registered. ID: " + id);
        } catch (DuplicateRecordException | UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminViewAllPatients() {
        List<Patient> patients = clinicService.getAllPatients();
        System.out.println("\n-- All Patients (" + patients.size() + ") --");
        if (patients.isEmpty()) { System.out.println("  None found."); return; }
        for (Patient p : patients) {
            System.out.println("  " + p.getPatientId() + " | " + p.getFullName()
                + " | " + p.getPatientType() + " | " + p.getMembershipType()
                + (p.getPrimarySport() != null ? " | " + p.getPrimarySport() : ""));
        }
    }

    private void adminViewAllStaff() {
        List<Staff> staffList = clinicService.getAllStaff();
        System.out.println("\n-- All Staff (" + staffList.size() + ") --");
        if (staffList.isEmpty()) { System.out.println("  None found."); return; }
        for (Staff s : staffList) {
            System.out.println("  " + s.getStaffId() + " | " + s.getFullName()
                + " | " + s.getRole()
                + " | " + (s.getHomeClinic() != null ? s.getHomeClinic().getCity() : "N/A"));
        }
    }

    private void adminScheduleAppointment() {
        System.out.println("\n-- Schedule Appointment --");
        String patientId = prompt("Patient ID  : ");
        String staffId   = prompt("Staff ID    : ");
        String city      = prompt("Clinic city : ");
        LocalDateTime dt = promptDateTime("Date & time");
        AppointmentType type = promptEnum("appointment type", AppointmentType.class);

        try {
            Appointment a = clinicService.scheduleAppointment(patientId, staffId, city, dt, type);
            System.out.println("Appointment scheduled. ID: " + a.getAppointmentId());
        } catch (SchedulingConflictException | PatientNotFoundException
                 | UnauthorizedActionException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminViewClinicSchedule() {
        String city = prompt("Clinic city: ");
        List<Appointment> appts = clinicService.getAppointmentsForClinic(city);
        System.out.println("\n-- Schedule for " + city + " (" + appts.size() + " appointments) --");
        for (Appointment a : appts) {
            System.out.println("  " + DateUtils.displayDateTime(a.getDateTime())
                + " | " + a.getType() + " | " + a.getStatus()
                + " | Patient: " + (a.getPatient() != null ? a.getPatient().getFullName() : "N/A")
                + " | Staff: " + (a.getStaff() != null ? a.getStaff().getFullName() : "N/A"));
        }
    }

    private void adminRegisterSportingEvent() {
        System.out.println("\n-- Register Sporting Event --");
        String name       = prompt("Event name   : ");
        Sport sport       = promptEnum("sport", Sport.class);
        String hostTeam   = prompt("Host team    : ");
        String visitTeam  = prompt("Visiting team: ");
        String city       = prompt("Clinic city  : ");
        LocalDate start   = promptDate("Start date");
        LocalDate end     = promptDate("End date");
        String delId      = prompt("Delegate ID (or blank): ");
        TeamDelegate del  = null;
        if (!delId.isBlank() && authService.getLoggedInDelegate() != null
                && authService.getLoggedInDelegate().getDelegateId().equals(delId)) {
            del = authService.getLoggedInDelegate();
        }

        try {
            axismed.model.event.SportingEvent ev =
                clinicService.registerSportingEvent(name, sport, hostTeam, visitTeam, city, start, end, del);
            System.out.println("Event registered. ID: " + ev.getEventId());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminViewAllEvents() {
        List<axismed.model.event.SportingEvent> events = clinicService.getAllEvents();
        System.out.println("\n-- All Sporting Events (" + events.size() + ") --");
        if (events.isEmpty()) { System.out.println("  None found."); return; }
        for (axismed.model.event.SportingEvent e : events) {
            System.out.println("  " + e.getEventId() + " | " + e.getEventName()
                + " | " + e.getSport() + " | " + e.getStartDate() + " — " + e.getEndDate()
                + " | Athletes: " + e.getAthleteCount());
        }
    }

    private void adminSearchPatients() {
        String query = prompt("Search query: ");
        List<Patient> results = clinicService.searchPatients(query);
        System.out.println("\n-- Search Results (" + results.size() + ") --");
        for (Patient p : results) {
            System.out.println("  " + p.getPatientId() + " | " + p.getFullName()
                + " | " + p.getPatientType());
        }
    }

    private void adminGenerateReport() {
        String patientId = prompt("Patient ID: ");
        try {
            String report = clinicService.generatePerformanceReport(patientId);
            System.out.println(report);
        } catch (PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void adminViewAuditLog() {
        System.out.println("\n-- Audit Log --");
        try {
            List<String[]> rows = CSVReaderService.getInstance().readAll("data/audit_log.csv");
            if (rows.isEmpty()) { System.out.println("  No entries yet."); return; }
            System.out.printf("  %-35s %-22s %s%n", "Action", "Timestamp", "User");
            System.out.println("  " + "-".repeat(80));
            int limit = Math.min(rows.size(), 50);
            for (int i = rows.size() - limit; i < rows.size(); i++) {
                String[] r = rows.get(i);
                System.out.printf("  %-35s %-22s %s%n",
                    r.length > 0 ? r[0] : "",
                    r.length > 1 ? r[1] : "",
                    r.length > 2 ? r[2] : "");
            }
        } catch (IOException e) {
            System.out.println("  Could not read audit log: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // DOCTOR menu
    // -------------------------------------------------------------------------

    private void showDoctorMenu() {
        while (true) {
            System.out.println("\n=== AXISMED ELITE | DOCTOR ===");
            System.out.println("1.  View my patients");
            System.out.println("2.  View my upcoming appointments");
            System.out.println("3.  Record diagnosis");
            System.out.println("4.  Create treatment plan");
            System.out.println("5.  Add rehab protocol to plan");
            System.out.println("6.  Prescribe medication");
            System.out.println("7.  Record functional test");
            System.out.println("8.  Record clearance test");
            System.out.println("9.  View patient full history");
            System.out.println("10. Track treatment plan progress");
            System.out.println("0.  Logout");

            int choice = promptInt("Select: ");
            if (choice == 0) { authService.logout(); return; }
            handleDoctorChoice(choice);
        }
    }

    private void handleDoctorChoice(int choice) {
        switch (choice) {
            case 1:  doctorViewMyPatients();          break;
            case 2:  doctorViewUpcomingAppointments();break;
            case 3:  doctorRecordDiagnosis();         break;
            case 4:  doctorCreateTreatmentPlan();     break;
            case 5:  doctorAddRehabProtocol();        break;
            case 6:  doctorPrescribeMedication();     break;
            case 7:  doctorRecordFunctionalTest();    break;
            case 8:  doctorRecordClearanceTest();     break;
            case 9:  doctorViewPatientHistory();      break;
            case 10: doctorTrackPlanProgress();       break;
            default: System.out.println("Invalid option.");
        }
    }

    private void doctorViewMyPatients() {
        Staff staff = authService.getLoggedInStaff();
        if (!(staff instanceof Doctor)) {
            System.out.println("  This option is available for Doctors only.");
            return;
        }
        List<Patient> patients = ((Doctor) staff).getPatients();
        System.out.println("\n-- My Patients (" + patients.size() + ") --");
        if (patients.isEmpty()) { System.out.println("  None assigned."); return; }
        for (Patient p : patients) {
            System.out.println("  " + p.getPatientId() + " | " + p.getFullName()
                + " | Age: " + p.getAge()
                + (p.getPrimarySport() != null ? " | " + p.getPrimarySport() : ""));
        }
    }

    private void doctorViewUpcomingAppointments() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        try {
            List<Appointment> appts = clinicService.viewStaffUpcomingAppointments(staff.getStaffId());
            System.out.println("\n-- Upcoming Appointments (" + appts.size() + ") --");
            for (Appointment a : appts) {
                System.out.println("  " + a.getAppointmentId()
                    + " | " + DateUtils.displayDateTime(a.getDateTime())
                    + " | " + a.getType()
                    + " | Patient: " + (a.getPatient() != null ? a.getPatient().getFullName() : "N/A"));
            }
        } catch (StaffNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorRecordDiagnosis() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        System.out.println("\n-- Record Diagnosis --");
        String appointmentId = prompt("Appointment ID : ");
        String diagnosisName = prompt("Diagnosis name : ");
        String description   = prompt("Description    : ");
        String icdCode       = prompt("ICD code (blank if none): ");

        try {
            Diagnosis d = clinicService.recordDiagnosis(appointmentId, staff.getStaffId(),
                                                        diagnosisName, description,
                                                        icdCode.isEmpty() ? null : icdCode);
            System.out.println("Diagnosis recorded. ID: " + d.getDiagnosisId());
        } catch (UnauthorizedActionException | PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorCreateTreatmentPlan() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        System.out.println("\n-- Create Treatment Plan --");
        String patientId     = prompt("Patient ID           : ");
        String diagnosisId   = prompt("Diagnosis ID (blank if none): ");
        String recommendations = prompt("Recommendations      : ");

        try {
            TreatmentPlan plan = clinicService.createTreatmentPlan(patientId, staff.getStaffId(),
                diagnosisId.isEmpty() ? null : diagnosisId, recommendations);
            System.out.println("Treatment plan created. ID: " + plan.getPlanId());
        } catch (UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorAddRehabProtocol() {
        System.out.println("\n-- Add Rehab Protocol --");
        String planId       = prompt("Plan ID          : ");
        String description  = prompt("Description      : ");
        int durationWeeks   = promptInt("Duration (weeks) : ");
        int sessionsPerWeek = promptInt("Sessions/week    : ");

        String protocolId = "PRO-" + newId();
        RehabilitationProtocol protocol =
            new RehabilitationProtocol(protocolId, planId, description, durationWeeks, sessionsPerWeek);

        System.out.println("Add exercises? (y/n): ");
        String addEx = scanner.nextLine().trim();
        while (addEx.equalsIgnoreCase("y")) {
            String exName  = prompt("  Exercise name    : ");
            String exDesc  = prompt("  Description      : ");
            int sets       = promptInt("  Sets             : ");
            int reps       = promptInt("  Reps             : ");
            String duration = prompt("  Duration (blank) : ");
            String notes   = prompt("  Notes            : ");
            protocol.addExercise(new Exercise(exName, exDesc, sets, reps,
                                              duration.isEmpty() ? null : duration, notes));
            System.out.print("Add another exercise? (y/n): ");
            addEx = scanner.nextLine().trim();
        }

        clinicService.addRehabProtocol(planId, protocol);
        System.out.println("Rehab protocol added. Exercises: " + protocol.getExercises().size());
    }

    private void doctorPrescribeMedication() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        System.out.println("\n-- Prescribe Medication --");
        String patientId  = prompt("Patient ID  : ");
        String medId      = "MED-" + newId();
        String name       = prompt("Name        : ");
        String dosage     = prompt("Dosage      : ");
        String frequency  = prompt("Frequency   : ");
        LocalDate start   = promptDate("Start date");
        LocalDate end     = promptDate("End date");
        String suppInput  = prompt("Supplement? (y/n): ");
        boolean isSupp    = suppInput.equalsIgnoreCase("y");
        String notes      = prompt("Notes (blank): ");

        Medication med = new Medication(medId, patientId, staff.getStaffId(),
                                        name, dosage, frequency, start, end, isSupp,
                                        notes.isEmpty() ? null : notes);
        try {
            clinicService.prescribeMedication(patientId, staff.getStaffId(), med);
            System.out.println("Medication prescribed. ID: " + medId);
        } catch (UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorRecordFunctionalTest() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        System.out.println("\n-- Record Functional Test --");
        String patientId     = prompt("Patient ID     : ");
        String appointmentId = prompt("Appointment ID : ");
        String testName      = prompt("Test name      : ");
        ResultType rType     = promptEnum("result type", ResultType.class);
        String resultValue   = prompt("Result value   : ");
        String unit          = prompt("Unit (blank)   : ");
        String notes         = prompt("Notes (blank)  : ");

        FunctionalTest test = clinicService.recordFunctionalTest(patientId, staff.getStaffId(),
            appointmentId, testName, rType, resultValue,
            unit.isEmpty() ? null : unit,
            notes.isEmpty() ? null : notes);
        System.out.println("Functional test recorded. ID: " + test.getTestId());
    }

    private void doctorRecordClearanceTest() {
        Staff staff = authService.getLoggedInStaff();
        if (staff == null) return;
        System.out.println("\n-- Record Clearance Test --");
        String patientId       = prompt("Patient ID    : ");
        ClearanceType cType    = promptEnum("clearance type", ClearanceType.class);
        ClearanceResult result = promptEnum("result", ClearanceResult.class);
        LocalDate validUntil   = null;
        if (result == ClearanceResult.CLEARED) {
            validUntil = promptDate("Valid until");
        }
        String notes = prompt("Official notes (blank): ");

        try {
            ClearanceTest ct = clinicService.recordClearanceTest(patientId, staff.getStaffId(),
                cType, result, validUntil, notes.isEmpty() ? null : notes);
            System.out.println("Clearance test recorded. ID: " + ct.getAppointmentId()
                + " | Result: " + result);
        } catch (InvalidClearanceException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorViewPatientHistory() {
        String patientId = prompt("Patient ID: ");
        try {
            MedicalRecord record = clinicService.viewFullPatientHistory(patientId);
            if (record != null) System.out.println(record.getFullHistory());
            else System.out.println("No medical record found for this patient.");
        } catch (PatientNotFoundException | UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doctorTrackPlanProgress() {
        System.out.println("\n-- Track Treatment Plan Progress --");
        String planId = prompt("Plan ID       : ");
        String note   = prompt("Progress note : ");
        clinicService.trackTreatmentPlanProgress(planId, note);
        System.out.println("Progress note added.");
    }

    // -------------------------------------------------------------------------
    // PATIENT menu
    // -------------------------------------------------------------------------

    private void showPatientMenu() {
        while (true) {
            System.out.println("\n=== AXISMED ELITE | PATIENT ===");
            System.out.println("1.  View my appointments");
            System.out.println("2.  View my medical record");
            System.out.println("3.  View my diagnoses");
            System.out.println("4.  View my treatment plans");
            System.out.println("5.  View my prescriptions");
            System.out.println("6.  View my functional test results");
            System.out.println("0.  Logout");

            int choice = promptInt("Select: ");
            if (choice == 0) { authService.logout(); return; }
            handlePatientChoice(choice);
        }
    }

    private void handlePatientChoice(int choice) {
        Patient patient = authService.getLoggedInPatient();
        if (patient == null) { authService.logout(); return; }
        switch (choice) {
            case 1: patientViewAppointments(patient);   break;
            case 2: patientViewMedicalRecord(patient);  break;
            case 3: patientViewDiagnoses(patient);      break;
            case 4: patientViewTreatmentPlans(patient); break;
            case 5: patientViewPrescriptions(patient);  break;
            case 6: patientViewFunctionalTests(patient);break;
            default: System.out.println("Invalid option.");
        }
    }

    private void patientViewAppointments(Patient patient) {
        System.out.println("\n-- My Appointments --");
        List<Appointment> appts = patient.getAppointments();
        if (appts.isEmpty()) { System.out.println("  None found."); return; }
        for (Appointment a : appts) {
            System.out.println("  " + DateUtils.displayDateTime(a.getDateTime())
                + " | " + a.getType() + " | " + a.getStatus()
                + " | " + (a.getClinic() != null ? a.getClinic().getCity() : "N/A"));
        }
    }

    private void patientViewMedicalRecord(Patient patient) {
        try {
            MedicalRecord record = clinicService.viewFullPatientHistory(patient.getPatientId());
            if (record != null) System.out.println(record.getFullHistory());
            else System.out.println("  No medical record found.");
        } catch (PatientNotFoundException | UnauthorizedActionException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void patientViewDiagnoses(Patient patient) {
        System.out.println("\n-- My Diagnoses --");
        if (patient.getDiagnoses().isEmpty()) { System.out.println("  None found."); return; }
        for (var d : patient.getDiagnoses()) {
            System.out.println("  [" + DateUtils.displayDate(d.getDiagnosisDate()) + "] "
                + d.getDiagnosisName()
                + (d.getIcdCode() != null && !d.getIcdCode().isEmpty()
                    ? " (" + d.getIcdCode() + ")" : "")
                + " — " + d.getClinicCity());
        }
    }

    private void patientViewTreatmentPlans(Patient patient) {
        System.out.println("\n-- My Treatment Plans --");
        if (patient.getTreatmentPlans().isEmpty()) { System.out.println("  None found."); return; }
        for (TreatmentPlan p : patient.getTreatmentPlans()) {
            System.out.println("  " + p.getPlanId() + " | Status: " + p.getStatus()
                + " | Start: " + DateUtils.displayDate(p.getStartDate()));
            if (p.getFreeTextRecommendations() != null && !p.getFreeTextRecommendations().isEmpty()) {
                System.out.println("    Recommendations: " + p.getFreeTextRecommendations());
            }
        }
    }

    private void patientViewPrescriptions(Patient patient) {
        System.out.println("\n-- My Prescriptions --");
        if (patient.getMedications().isEmpty()) { System.out.println("  None found."); return; }
        for (Medication m : patient.getMedications()) {
            System.out.println("  " + m.getName() + " | " + m.getDosage()
                + " | " + m.getFrequency()
                + (m.isSupplement() ? " [supplement]" : " [pharmaceutical]")
                + " | Until: " + DateUtils.displayDate(m.getEndDate()));
        }
    }

    private void patientViewFunctionalTests(Patient patient) {
        System.out.println("\n-- My Functional Test Results --");
        if (patient.getFunctionalTests().isEmpty()) { System.out.println("  None found."); return; }
        for (FunctionalTest t : patient.getFunctionalTests()) {
            System.out.println("  [" + DateUtils.displayDate(t.getTestDate()) + "] "
                + t.getTestName() + ": " + t.getResultValue()
                + (t.getUnit() != null && !t.getUnit().isEmpty() ? " " + t.getUnit() : "")
                + " — " + t.getResultType());
        }
    }

    // -------------------------------------------------------------------------
    // TEAM DELEGATE menu
    // -------------------------------------------------------------------------

    private void showDelegateMenu() {
        while (true) {
            System.out.println("\n=== AXISMED ELITE | TEAM DELEGATE ===");
            System.out.println("1.  Register athlete");
            System.out.println("2.  View my athletes");
            System.out.println("3.  View athlete clearance status");
            System.out.println("4.  Link athlete to event");
            System.out.println("5.  View current event details");
            System.out.println("0.  Logout");

            int choice = promptInt("Select: ");
            if (choice == 0) { authService.logout(); return; }
            handleDelegateChoice(choice);
        }
    }

    private void handleDelegateChoice(int choice) {
        TeamDelegate delegate = authService.getLoggedInDelegate();
        if (delegate == null) { authService.logout(); return; }
        switch (choice) {
            case 1: delegateRegisterAthlete(delegate);     break;
            case 2: delegateViewAthletes(delegate);        break;
            case 3: delegateViewClearanceStatus(delegate); break;
            case 4: delegateLinkAthleteToEvent(delegate);  break;
            case 5: delegateViewEvent(delegate);           break;
            default: System.out.println("Invalid option.");
        }
    }

    private void delegateRegisterAthlete(TeamDelegate delegate) {
        System.out.println("\n-- Register Athlete --");
        String id          = "PAT-" + newId();
        String firstName   = prompt("First name  : ");
        String lastName    = prompt("Last name   : ");
        String email       = prompt("Email       : ");
        String password    = prompt("Password    : ");
        LocalDate dob      = promptDate("Date of birth");
        Gender gender      = promptEnum("gender", Gender.class);
        String phone       = prompt("Phone       : ");
        String nationality = prompt("Nationality : ");
        Sport sport        = promptEnum("sport", Sport.class);
        PatientType pType  = promptEnum("patient type", PatientType.class);
        MembershipType mem = promptEnum("membership type", MembershipType.class);

        Patient athlete = new Patient(id, firstName, lastName, email, password,
                                      dob, gender, phone, nationality, pType, mem, sport);
        try {
            clinicService.registerAthlete(athlete, delegate);
            appendUserRecord(email, password, UserRole.PATIENT, id);
            System.out.println("Athlete registered. ID: " + id);
        } catch (DuplicateRecordException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void delegateViewAthletes(TeamDelegate delegate) {
        System.out.println("\n-- My Athletes (" + delegate.getManagedAthletes().size() + ") --");
        if (delegate.getManagedAthletes().isEmpty()) {
            System.out.println("  No athletes registered.");
            return;
        }
        for (Patient p : delegate.getManagedAthletes()) {
            System.out.println("  " + p.getPatientId() + " | " + p.getFullName()
                + " | " + p.getNationality()
                + (p.getPrimarySport() != null ? " | " + p.getPrimarySport() : ""));
        }
    }

    private void delegateViewClearanceStatus(TeamDelegate delegate) {
        System.out.println("\n-- Athlete Clearance Status --");
        if (delegate.getManagedAthletes().isEmpty()) {
            System.out.println("  No athletes registered.");
            return;
        }
        for (Patient p : delegate.getManagedAthletes()) {
            String clearance = "PENDING";
            for (Appointment a : p.getAppointments()) {
                if (a instanceof ClearanceTest) {
                    ClearanceTest ct = (ClearanceTest) a;
                    clearance = ct.getClearanceResult().name();
                    if (ct.getValidUntil() != null) {
                        clearance += " (valid until " + DateUtils.displayDate(ct.getValidUntil()) + ")";
                    }
                }
            }
            System.out.println("  " + p.getFullName() + " [" + p.getPatientId() + "]: " + clearance);
        }
    }

    private void delegateLinkAthleteToEvent(TeamDelegate delegate) {
        String patientId = prompt("Athlete ID : ");
        String eventId   = prompt("Event ID   : ");
        clinicService.linkAthleteToEvent(patientId, eventId);
        System.out.println("Athlete linked to event.");
    }

    private void delegateViewEvent(TeamDelegate delegate) {
        axismed.model.event.SportingEvent event = delegate.getCurrentEvent();
        if (event == null) {
            System.out.println("  No current event assigned.");
            return;
        }
        System.out.println("\n-- Current Event --");
        System.out.println("  ID       : " + event.getEventId());
        System.out.println("  Name     : " + event.getEventName());
        System.out.println("  Sport    : " + event.getSport());
        System.out.println("  Host     : " + event.getHostTeam());
        System.out.println("  Visiting : " + event.getVisitingTeam());
        System.out.println("  Clinic   : " + (event.getHostClinic() != null
                           ? event.getHostClinic().getCity() : "N/A"));
        System.out.println("  Dates    : " + DateUtils.displayDate(event.getStartDate())
                           + " — " + DateUtils.displayDate(event.getEndDate()));
        System.out.println("  Athletes : " + event.getAthleteCount());
    }

    // -------------------------------------------------------------------------
    // Input helpers
    // -------------------------------------------------------------------------

    private String prompt(String message) {
        System.out.print(message);
        System.out.flush();
        return scanner.nextLine().trim();
    }

    private int promptInt(String message) {
        while (true) {
            System.out.print(message);
            System.out.flush();
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number.");
            }
        }
    }

    private LocalDate promptDate(String message) {
        while (true) {
            System.out.print(message + " (yyyy-MM-dd): ");
            System.out.flush();
            LocalDate date = DateUtils.parseDate(scanner.nextLine().trim());
            if (date != null) return date;
            System.out.println("  Invalid format. Use yyyy-MM-dd.");
        }
    }

    private LocalDateTime promptDateTime(String message) {
        while (true) {
            System.out.print(message + " (yyyy-MM-dd HH:mm): ");
            System.out.flush();
            LocalDateTime dt = DateUtils.parseDateTime(scanner.nextLine().trim());
            if (dt != null) return dt;
            System.out.println("  Invalid format. Use yyyy-MM-dd HH:mm.");
        }
    }

    private <T extends Enum<T>> T promptEnum(String label, Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        System.out.println("  Select " + label + ":");
        for (int i = 0; i < values.length; i++) {
            System.out.println("    " + (i + 1) + ". " + values[i]);
        }
        while (true) {
            int choice = promptInt("  Choice: ");
            if (choice >= 1 && choice <= values.length) return values[choice - 1];
            System.out.println("  Invalid choice. Enter 1–" + values.length + ".");
        }
    }

    private <T extends Enum<T>> T parseEnumSafe(Class<T> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String newId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private void appendUserRecord(String email, String password, UserRole role, String linkedId) {
        String[] row = { email, password, role.name(), linkedId };
        try {
            CSVWriterService.getInstance().appendRow(USERS_FILE, row);
        } catch (IOException e) {
            System.out.println("Warning: Could not update users.csv — " + e.getMessage());
        }
    }
}
