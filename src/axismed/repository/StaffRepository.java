package axismed.repository;

import axismed.enums.MedicalSpecialization;
import axismed.enums.StaffRole;
import axismed.model.clinic.Clinic;
import axismed.model.staff.Doctor;
import axismed.model.staff.Nutritionist;
import axismed.model.staff.Physiotherapist;
import axismed.model.staff.Staff;
import axismed.model.staff.Trainer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StaffRepository {

    private static final String FILE_PATH = "data/staff.csv";
    private static final String[] HEADER = {
        "staffId", "firstName", "lastName", "email", "passwordHash",
        "role", "specialization", "homeClinic", "assignedClinics",
        "licenseNumber", "isActive"
    };

    private final HashMap<String, Staff> staffMap = new HashMap<>();
    private final CSVReaderService reader = CSVReaderService.getInstance();
    private final CSVWriterService writer = CSVWriterService.getInstance();

    public StaffRepository() {
        load();
    }

    private void load() {
        try {
            List<String[]> rows = reader.readAll(FILE_PATH);
            for (String[] row : rows) {
                Staff s = fromCSV(row);
                staffMap.put(s.getStaffId(), s);
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load staff.csv — " + e.getMessage());
        }
    }

    private Staff fromCSV(String[] row) {
        String staffId    = row[0];
        String firstName  = row[1];
        String lastName   = row[2];
        String email      = row[3];
        String passHash   = row[4];
        StaffRole role    = StaffRole.valueOf(row[5]);
        String specStr    = row[6];
        String homeCity   = row[7];
        String assignedStr = row[8];
        String licenseNum = row[9];
        boolean isActive  = Boolean.parseBoolean(row[10]);

        Clinic homeClinic = new Clinic("", homeCity, "", "", "");

        Staff staff;
        switch (role) {
            case DOCTOR:
                MedicalSpecialization spec = (specStr == null || specStr.isEmpty())
                    ? MedicalSpecialization.GENERAL
                    : MedicalSpecialization.valueOf(specStr);
                staff = new Doctor(staffId, firstName, lastName, email, passHash,
                                   homeClinic, spec, licenseNum);
                break;
            case PHYSIOTHERAPIST:
                staff = new Physiotherapist(staffId, firstName, lastName, email, passHash,
                                            homeClinic, licenseNum);
                break;
            case NUTRITIONIST:
                staff = new Nutritionist(staffId, firstName, lastName, email, passHash, homeClinic);
                break;
            case TRAINER:
                staff = new Trainer(staffId, firstName, lastName, email, passHash, homeClinic);
                break;
            default:
                staff = new Doctor(staffId, firstName, lastName, email, passHash,
                                   homeClinic, MedicalSpecialization.GENERAL, licenseNum);
                break;
        }

        if (assignedStr != null && !assignedStr.isEmpty()) {
            for (String city : assignedStr.split(";")) {
                staff.addAssignedClinic(new Clinic("", city.trim(), "", "", ""));
            }
        }

        staff.setActive(isActive);
        return staff;
    }

    private String[] toCSV(Staff s) {
        String spec = "";
        String license = "";
        if (s instanceof Doctor) {
            spec = ((Doctor) s).getSpecialization().name();
            license = ((Doctor) s).getLicenseNumber();
        } else if (s instanceof Physiotherapist) {
            license = ((Physiotherapist) s).getCertificationNumber();
        }

        String assignedClinics = s.getAssignedClinics().stream()
            .map(Clinic::getCity)
            .collect(Collectors.joining(";"));

        return new String[]{
            s.getStaffId(),
            s.getFirstName(),
            s.getLastName(),
            s.getEmail(),
            s.getPasswordHash(),
            s.getRole().name(),
            spec,
            s.getHomeClinic() != null ? s.getHomeClinic().getCity() : "",
            assignedClinics,
            license,
            String.valueOf(s.isActive())
        };
    }

    public void save(Staff staff) {
        staffMap.put(staff.getStaffId(), staff);
        persist();
    }

    public Staff findById(String staffId) {
        return staffMap.get(staffId);
    }

    public Staff findByEmail(String email) {
        return staffMap.values().stream()
            .filter(s -> s.getEmail().equalsIgnoreCase(email))
            .findFirst().orElse(null);
    }

    public List<Staff> findAll() {
        return new ArrayList<>(staffMap.values());
    }

    public boolean existsByEmail(String email) {
        return staffMap.values().stream()
            .anyMatch(s -> s.getEmail().equalsIgnoreCase(email));
    }

    public void update(Staff staff) {
        staffMap.put(staff.getStaffId(), staff);
        persist();
    }

    private void persist() {
        List<String[]> rows = staffMap.values().stream()
            .map(this::toCSV)
            .collect(Collectors.toList());
        try {
            writer.writeAll(FILE_PATH, rows, HEADER);
        } catch (IOException e) {
            System.out.println("Warning: Could not save staff.csv — " + e.getMessage());
        }
    }
}
