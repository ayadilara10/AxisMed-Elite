package axismed.model.staff;

import axismed.enums.StaffRole;
import axismed.model.clinic.Clinic;
import java.util.ArrayList;
import java.util.List;

public class Trainer extends Staff {

    private List<String> certifications;

    public Trainer(String staffId, String firstName, String lastName, String email,
                   String passwordHash, Clinic homeClinic) {
        super(staffId, firstName, lastName, email, passwordHash, StaffRole.TRAINER, homeClinic);
        this.certifications = new ArrayList<>();
    }

    // --- Abstract method implementations ---

    @Override
    public String getTitle() { return "Trainer"; }

    @Override
    public boolean canDiagnose() { return false; }

    @Override
    public boolean canWriteTreatmentPlan() { return true; }

    // --- Getters and setters ---

    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }

    public void addCertification(String certification) {
        certifications.add(certification);
    }

    @Override
    public String toString() {
        return "Trainer{id='" + getStaffId() + "', name='" + getFullName() +
               "', certifications=" + certifications + ", active=" + isActive() + "}";
    }
}
