package axismed.repository;

import axismed.enums.AppointmentStatus;
import axismed.enums.AppointmentType;
import axismed.model.appointment.Appointment;
import axismed.model.clinic.Clinic;
import axismed.model.patient.Patient;
import axismed.model.staff.Staff;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentRepository {

    private static final String FILE_PATH = "data/appointments.csv";
    private static final String[] HEADER = {
        "appointmentId", "patientId", "staffId", "clinicCity",
        "dateTime", "type", "status", "notes"
    };

    private final HashMap<String, Appointment> appointments = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;

    public AppointmentRepository(PatientRepository patientRepository,
                                  StaffRepository staffRepository) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                Appointment a = fromCSV(row);
                if (a != null) {
                    appointments.put(a.getAppointmentId(), a);
                    // Link back to patient and staff
                    if (a.getPatient() != null) {
                        a.getPatient().getAppointments().add(a);
                    }
                    if (a.getStaff() != null) {
                        a.getStaff().scheduleAppointment(a);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load appointments.csv — " + e.getMessage());
        }
    }

    private Appointment fromCSV(String[] row) {
        String appointmentId = row[0];
        String patientId     = row[1];
        String staffId       = row[2];
        String clinicCity    = row[3];
        LocalDateTime dt     = LocalDateTime.parse(row[4]);
        AppointmentType type = AppointmentType.valueOf(row[5]);
        AppointmentStatus status = AppointmentStatus.valueOf(row[6]);
        String notes         = row[7];

        Patient patient = patientRepository.findById(patientId);
        Staff staff     = staffRepository.findById(staffId);
        Clinic clinic   = new Clinic("", clinicCity, "", "", "");

        return new Appointment(appointmentId, patient, staff, clinic, dt, type, status, notes);
    }

    private String[] toCSV(Appointment a) {
        return new String[]{
            a.getAppointmentId(),
            a.getPatient() != null ? a.getPatient().getPatientId() : "",
            a.getStaff()   != null ? a.getStaff().getStaffId()     : "",
            a.getClinic()  != null ? a.getClinic().getCity()        : "",
            a.getDateTime().toString(),
            a.getType().name(),
            a.getStatus().name(),
            a.getNotes() != null ? a.getNotes() : ""
        };
    }

    public void save(Appointment appointment) {
        appointments.put(appointment.getAppointmentId(), appointment);
        persist();
    }

    public Appointment findById(String appointmentId) {
        return appointments.get(appointmentId);
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    public List<Appointment> findByPatientId(String patientId) {
        return appointments.values().stream()
            .filter(a -> a.getPatient() != null &&
                         a.getPatient().getPatientId().equals(patientId))
            .collect(Collectors.toList());
    }

    public List<Appointment> findByStaffId(String staffId) {
        return appointments.values().stream()
            .filter(a -> a.getStaff() != null &&
                         a.getStaff().getStaffId().equals(staffId))
            .collect(Collectors.toList());
    }

    public void update(Appointment appointment) {
        appointments.put(appointment.getAppointmentId(), appointment);
        persist();
    }

    private void persist() {
        List<String[]> rows = appointments.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save appointments.csv — " + e.getMessage());
        }
    }
}
