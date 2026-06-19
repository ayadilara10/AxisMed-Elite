package axismed.model.staff;

import axismed.enums.StaffRole;
import axismed.model.clinic.Clinic;
import axismed.model.medical.TreatmentPlan;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends Staff {

    private List<TreatmentPlan> managedPlans;

    public Nutritionist(String staffId, String firstName, String lastName, String email,
                        String passwordHash, Clinic homeClinic) {
        super(staffId, firstName, lastName, email, passwordHash, StaffRole.NUTRITIONIST, homeClinic);
        this.managedPlans = new ArrayList<>();
    }

    // --- Abstract method implementations ---

    @Override
    public String getTitle() { return "Nutr."; }

    @Override
    public boolean canDiagnose() { return false; }

    @Override
    public boolean canWriteTreatmentPlan() { return true; }

    // --- Getters and setters ---

    public List<TreatmentPlan> getManagedPlans() { return managedPlans; }
    public void setManagedPlans(List<TreatmentPlan> managedPlans) { this.managedPlans = managedPlans; }

    public void addManagedPlan(TreatmentPlan plan) {
        managedPlans.add(plan);
    }

    @Override
    public String toString() {
        return "Nutritionist{id='" + getStaffId() + "', name='" + getFullName() +
               "', managedPlans=" + managedPlans.size() + ", active=" + isActive() + "}";
    }
}
