package axismed.model.patient;

import axismed.model.event.SportingEvent;
import java.util.ArrayList;
import java.util.List;

public class TeamDelegate {

    private String delegateId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String organization;
    private String phone;
    private List<Patient> managedAthletes;
    private SportingEvent currentEvent;

    public TeamDelegate(String delegateId, String firstName, String lastName, String email,
                        String passwordHash, String organization, String phone) {
        this.delegateId = delegateId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.organization = organization;
        this.phone = phone;
        this.managedAthletes = new ArrayList<>();
        this.currentEvent = null;
    }

    // --- Delegate-specific methods ---

    public void addAthlete(Patient athlete) {
        managedAthletes.add(athlete);
    }

    public void removeAthlete(String patientId) {
        managedAthletes.removeIf(p -> p.getPatientId().equals(patientId));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // --- Getters and setters ---

    public String getDelegateId() { return delegateId; }
    public void setDelegateId(String delegateId) { this.delegateId = delegateId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Patient> getManagedAthletes() { return managedAthletes; }
    public void setManagedAthletes(List<Patient> managedAthletes) { this.managedAthletes = managedAthletes; }

    public SportingEvent getCurrentEvent() { return currentEvent; }
    public void setCurrentEvent(SportingEvent currentEvent) { this.currentEvent = currentEvent; }

    @Override
    public String toString() {
        return "TeamDelegate{id='" + delegateId + "', name='" + getFullName() +
               "', organization='" + organization + "', athletes=" + managedAthletes.size() +
               ", currentEvent=" + (currentEvent != null ? currentEvent.getEventName() : "none") + "}";
    }
}
