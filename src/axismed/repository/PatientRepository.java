package axismed.repository;

import axismed.enums.Gender;
import axismed.enums.MembershipType;
import axismed.enums.PatientType;
import axismed.enums.Sport;
import axismed.model.patient.Patient;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PatientRepository {

    private static final String FILE_PATH = "data/patients.csv";
    private static final String[] HEADER = {
        "patientId", "firstName", "lastName", "email", "passwordHash",
        "dob", "gender", "phone", "nationality", "patientType",
        "membershipType", "chronicConditions", "sport", "isActive"
    };

    private final HashMap<String, Patient> patients = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();

    public PatientRepository() {
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                Patient p = fromCSV(row);
                patients.put(p.getPatientId(), p);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load patients.csv — " + e.getMessage());
        }
    }

    private Patient fromCSV(String[] row) {
        String patientId  = row[0];
        String firstName  = row[1];
        String lastName   = row[2];
        String email      = row[3];
        String passHash   = row[4];
        LocalDate dob     = LocalDate.parse(row[5]);
        Gender gender     = Gender.valueOf(row[6]);
        String phone      = row[7];
        String nationality = row[8];
        PatientType type  = PatientType.valueOf(row[9]);
        MembershipType mem = MembershipType.valueOf(row[10]);
        Sport sport       = (row[12] == null || row[12].isEmpty()) ? null : Sport.valueOf(row[12]);

        Patient p = new Patient(patientId, firstName, lastName, email, passHash,
                                dob, gender, phone, nationality, type, mem, sport);

        if (row[11] != null && !row[11].isEmpty()) {
            for (String condition : row[11].split(";")) {
                p.addChronicCondition(condition.trim());
            }
        }

        p.setActive(Boolean.parseBoolean(row[13]));
        return p;
    }

    private String[] toCSV(Patient p) {
        return new String[]{
            p.getPatientId(),
            p.getFirstName(),
            p.getLastName(),
            p.getEmail(),
            p.getPasswordHash(),
            p.getDateOfBirth().toString(),
            p.getGender().name(),
            p.getPhone(),
            p.getNationality(),
            p.getPatientType().name(),
            p.getMembershipType().name(),
            String.join(";", p.getChronicConditions()),
            p.getPrimarySport() != null ? p.getPrimarySport().name() : "",
            String.valueOf(p.isActive())
        };
    }

    public void save(Patient patient) {
        patients.put(patient.getPatientId(), patient);
        persist();
    }

    public Patient findById(String patientId) {
        return patients.get(patientId);
    }

    public Patient findByEmail(String email) {
        return patients.values().stream()
            .filter(p -> p.getEmail().equalsIgnoreCase(email))
            .findFirst().orElse(null);
    }

    public List<Patient> findAll() {
        return new ArrayList<>(patients.values());
    }

    public List<Patient> search(String query) {
        String q = query.toLowerCase();
        return patients.values().stream()
            .filter(p -> p.getFullName().toLowerCase().contains(q) ||
                         p.getChronicConditions().stream()
                             .anyMatch(c -> c.toLowerCase().contains(q)))
            .collect(Collectors.toList());
    }

    public boolean existsByEmail(String email) {
        return patients.values().stream()
            .anyMatch(p -> p.getEmail().equalsIgnoreCase(email));
    }

    public void update(Patient patient) {
        patients.put(patient.getPatientId(), patient);
        persist();
    }

    private void persist() {
        List<String[]> rows = patients.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save patients.csv — " + e.getMessage());
        }
    }
}
