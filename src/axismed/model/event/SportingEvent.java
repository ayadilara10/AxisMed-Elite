package axismed.model.event;

import axismed.enums.Sport;
import axismed.model.clinic.Clinic;
import axismed.model.patient.Patient;
import axismed.model.patient.TeamDelegate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SportingEvent {

    private String eventId;
    private String eventName;
    private Sport sport;
    private String hostTeam;
    private String visitingTeam;
    private Clinic hostClinic;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Patient> registeredAthletes;
    private TeamDelegate delegate;

    public SportingEvent(String eventId, String eventName, Sport sport, String hostTeam,
                         String visitingTeam, Clinic hostClinic, LocalDate startDate,
                         LocalDate endDate, TeamDelegate delegate) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.sport = sport;
        this.hostTeam = hostTeam;
        this.visitingTeam = visitingTeam;
        this.hostClinic = hostClinic;
        this.startDate = startDate;
        this.endDate = endDate;
        this.delegate = delegate;
        this.registeredAthletes = new ArrayList<>();
    }

    // --- Event-specific methods ---

    public void registerAthlete(Patient athlete) {
        registeredAthletes.add(athlete);
    }

    public int getAthleteCount() {
        return registeredAthletes.size();
    }

    // --- Getters and setters ---

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public Sport getSport() { return sport; }
    public void setSport(Sport sport) { this.sport = sport; }

    public String getHostTeam() { return hostTeam; }
    public void setHostTeam(String hostTeam) { this.hostTeam = hostTeam; }

    public String getVisitingTeam() { return visitingTeam; }
    public void setVisitingTeam(String visitingTeam) { this.visitingTeam = visitingTeam; }

    public Clinic getHostClinic() { return hostClinic; }
    public void setHostClinic(Clinic hostClinic) { this.hostClinic = hostClinic; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<Patient> getRegisteredAthletes() { return registeredAthletes; }
    public void setRegisteredAthletes(List<Patient> registeredAthletes) { this.registeredAthletes = registeredAthletes; }

    public TeamDelegate getDelegate() { return delegate; }
    public void setDelegate(TeamDelegate delegate) { this.delegate = delegate; }

    @Override
    public String toString() {
        return "SportingEvent{id='" + eventId + "', name='" + eventName + "', sport=" + sport +
               ", hostTeam='" + hostTeam + "', visitingTeam='" + visitingTeam +
               "', hostClinic='" + (hostClinic != null ? hostClinic.getCity() : "none") +
               "', start=" + startDate + ", end=" + endDate +
               ", athletes=" + registeredAthletes.size() + "}";
    }
}
