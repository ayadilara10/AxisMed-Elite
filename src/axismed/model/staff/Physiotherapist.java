package axismed.model.staff;

import axismed.enums.StaffRole;
import axismed.model.clinic.Clinic;
import axismed.model.medical.TreatmentPlan;
import java.util.ArrayList;
import java.util.List;

public class Physiotherapist extends Staff {

    private String certificationNumber;
    private List<TreatmentPlan> managedPlans;

    public Physiotherapist(String staffId, String firstName, String lastName, String email,
                           String passwordHash, Clinic homeClinic, String certificationNumber) {
        super(staffId, firstName, lastName, email, passwordHash, StaffRole.PHYSIOTHERAPIST, homeClinic);
        this.certificationNumber = certificationNumber;
        this.managedPlans = new ArrayList<>();
    }

    // --- Abstract method implementations ---

    @Override
    public String getTitle() { return "Phys."; }

    @Override
    public boolean canDiagnose() { return false; }

    @Override
    public boolean canWriteTreatmentPlan() { return true; }

    // --- Physiotherapist-specific methods ---

    public void addManagedPlan(TreatmentPlan plan) {
        managedPlans.add(plan);
    }

    // --- Getters and setters ---

    public String getCertificationNumber() { return certificationNumber; }
    public void setCertificationNumber(String certificationNumber) { this.certificationNumber = certificationNumber; }

    public List<TreatmentPlan> getManagedPlans() { return managedPlans; }
    public void setManagedPlans(List<TreatmentPlan> managedPlans) { this.managedPlans = managedPlans; }

    @Override
    public String toString() {
        return "Physiotherapist{id='" + getStaffId() + "', name='" + getFullName() +
               "', certification='" + certificationNumber +
               "', managedPlans=" + managedPlans.size() + ", active=" + isActive() + "}";
    }
}
