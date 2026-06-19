package axismed.model.appointment;

import axismed.enums.AppointmentStatus;
import axismed.enums.AppointmentType;
import axismed.model.clinic.Clinic;
import axismed.model.patient.Patient;
import axismed.model.staff.Staff;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {

    private String appointmentId;
    private Patient patient;
    private Staff staff;
    private Clinic clinic;
    private LocalDateTime dateTime;
    private AppointmentType type;
    private AppointmentStatus status;
    private String notes;

    public Appointment(String appointmentId, Patient patient, Staff staff, Clinic clinic,
                       LocalDateTime dateTime, AppointmentType type, AppointmentStatus status,
                       String notes) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.staff = staff;
        this.clinic = clinic;
        this.dateTime = dateTime;
        this.type = type;
        this.status = status;
        this.notes = notes;
    }

    // --- Convenience method used by Schedulable.hasAppointmentOn() ---

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    // --- Getters and setters ---

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public Clinic getClinic() { return clinic; }
    public void setClinic(Clinic clinic) { this.clinic = clinic; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public AppointmentType getType() { return type; }
    public void setType(AppointmentType type) { this.type = type; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Appointment{id='" + appointmentId + "', patient='" +
               (patient != null ? patient.getFullName() : "none") +
               "', staff='" + (staff != null ? staff.getFullName() : "none") +
               "', clinic='" + (clinic != null ? clinic.getCity() : "none") +
               "', dateTime=" + dateTime + ", type=" + type + ", status=" + status + "}";
    }
}
