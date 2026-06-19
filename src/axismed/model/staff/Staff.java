package axismed.model.staff;

import axismed.enums.StaffRole;
import axismed.model.Schedulable;
import axismed.model.appointment.Appointment;
import axismed.model.clinic.Clinic;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Staff implements Schedulable {

    private String staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private StaffRole role;
    private Clinic homeClinic;
    private List<Clinic> assignedClinics;
    private boolean isActive;
    private List<Appointment> appointments;

    public Staff(String staffId, String firstName, String lastName, String email,
                 String passwordHash, StaffRole role, Clinic homeClinic) {
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.homeClinic = homeClinic;
        this.assignedClinics = new ArrayList<>();
        this.isActive = true;
        this.appointments = new ArrayList<>();
    }

    // --- Abstract methods ---

    public abstract String getTitle();
    public abstract boolean canDiagnose();
    public abstract boolean canWriteTreatmentPlan();

    // --- Schedulable implementation ---

    @Override
    public void scheduleAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    @Override
    public void cancelAppointment(String appointmentId) {
        appointments.removeIf(a -> a.getAppointmentId().equals(appointmentId));
    }

    @Override
    public List<Appointment> getUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        return appointments.stream()
            .filter(a -> !a.getDate().isBefore(today))
            .collect(Collectors.toList());
    }

    // --- Getters and setters ---

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public StaffRole getRole() { return role; }
    public void setRole(StaffRole role) { this.role = role; }

    public Clinic getHomeClinic() { return homeClinic; }
    public void setHomeClinic(Clinic homeClinic) { this.homeClinic = homeClinic; }

    public List<Clinic> getAssignedClinics() { return assignedClinics; }
    public void setAssignedClinics(List<Clinic> assignedClinics) { this.assignedClinics = assignedClinics; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }

    public void addAssignedClinic(Clinic clinic) {
        assignedClinics.add(clinic);
    }

    public void removeAssignedClinic(Clinic clinic) {
        assignedClinics.remove(clinic);
    }

    public String getFullName() {
        return getTitle() + " " + firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Staff{id='" + staffId + "', name='" + getFullName() + "', role=" + role +
               ", homeClinic='" + (homeClinic != null ? homeClinic.getCity() : "none") +
               "', active=" + isActive + "}";
    }
}
