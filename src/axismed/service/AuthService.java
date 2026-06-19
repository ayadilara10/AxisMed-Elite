package axismed.service;

import axismed.enums.UserRole;
import axismed.model.patient.Patient;
import axismed.model.patient.TeamDelegate;
import axismed.model.staff.Staff;
import axismed.repository.CSVReaderService;
import axismed.repository.PatientRepository;
import axismed.repository.StaffRepository;
import java.io.IOException;
import java.util.List;

public class AuthService {

    private static final String USERS_FILE = "data/users.csv";

    private static AuthService instance;

    private Staff loggedInStaff;
    private Patient loggedInPatient;
    private TeamDelegate loggedInDelegate;
    private UserRole currentRole;

    private PatientRepository patientRepository;
    private StaffRepository staffRepository;

    private final CSVReaderService reader = CSVReaderService.getInstance();

    private AuthService() {}

    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public void setRepositories(PatientRepository patientRepository,
                                 StaffRepository staffRepository) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
    }

    /**
     * Reads users.csv, validates credentials, and sets session state.
     * Returns true on success, false on invalid credentials.
     */
    public boolean login(String email, String password) {
        try {
            List<String[]> rows = reader.readAll(USERS_FILE);
            for (String[] row : rows) {
                // columns: email,passwordHash,userRole,linkedId
                if (row[0].equalsIgnoreCase(email) && row[1].equals(password)) {
                    UserRole role = UserRole.valueOf(row[2]);
                    String linkedId = row[3];
                    return establishSession(role, linkedId, email);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not read users.csv — " + e.getMessage());
        }
        return false;
    }

    private boolean establishSession(UserRole role, String linkedId, String email) {
        loggedInStaff    = null;
        loggedInPatient  = null;
        loggedInDelegate = null;
        currentRole      = null;

        switch (role) {
            case ADMIN:
            case DOCTOR:
                Staff staff = (staffRepository != null) ? staffRepository.findById(linkedId) : null;
                if (staff == null) return false;
                loggedInStaff = staff;
                break;

            case PATIENT:
                Patient patient = (patientRepository != null) ? patientRepository.findById(linkedId) : null;
                if (patient == null) return false;
                loggedInPatient = patient;
                break;

            case TEAM_DELEGATE:
                // No dedicated repository for delegates; build a stub from users.csv data
                TeamDelegate delegate = new TeamDelegate(linkedId, "", "", email, "", "", "");
                loggedInDelegate = delegate;
                break;

            default:
                return false;
        }

        currentRole = role;
        AuditService.getInstance().log("LOGIN", email);
        return true;
    }

    public void logout() {
        if (currentRole != null) {
            String email = getCurrentUserEmail();
            AuditService.getInstance().log("LOGOUT", email);
        }
        loggedInStaff    = null;
        loggedInPatient  = null;
        loggedInDelegate = null;
        currentRole      = null;
    }

    public UserRole getCurrentRole() {
        return currentRole;
    }

    public String getCurrentUserId() {
        if (loggedInStaff    != null) return loggedInStaff.getStaffId();
        if (loggedInPatient  != null) return loggedInPatient.getPatientId();
        if (loggedInDelegate != null) return loggedInDelegate.getDelegateId();
        return null;
    }

    public String getCurrentUserEmail() {
        if (loggedInStaff    != null) return loggedInStaff.getEmail();
        if (loggedInPatient  != null) return loggedInPatient.getEmail();
        if (loggedInDelegate != null) return loggedInDelegate.getEmail();
        return "unknown";
    }

    public boolean hasPermission(UserRole requiredRole) {
        return currentRole != null && currentRole == requiredRole;
    }

    public boolean isLoggedIn() {
        return currentRole != null;
    }

    public Staff getLoggedInStaff() {
        return loggedInStaff;
    }

    public Patient getLoggedInPatient() {
        return loggedInPatient;
    }

    public TeamDelegate getLoggedInDelegate() {
        return loggedInDelegate;
    }
}
