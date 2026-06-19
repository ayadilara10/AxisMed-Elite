package axismed.model.appointment;

import axismed.enums.AppointmentStatus;
import axismed.enums.AppointmentType;
import axismed.enums.ClearanceResult;
import axismed.enums.ClearanceType;
import axismed.model.clinic.Clinic;
import axismed.model.patient.Patient;
import axismed.model.staff.Staff;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClearanceTest extends Appointment {

    private ClearanceType clearanceType;
    private ClearanceResult clearanceResult;
    private String issuedBy;
    private LocalDate validUntil;
    private String officialNotes;

    public ClearanceTest(String appointmentId, Patient patient, Staff staff, Clinic clinic,
                         LocalDateTime dateTime, AppointmentStatus status,
                         ClearanceType clearanceType, ClearanceResult clearanceResult,
                         String issuedBy, LocalDate validUntil, String officialNotes) {
        super(appointmentId, patient, staff, clinic, dateTime,
              clearanceType == ClearanceType.DOPING_SCREEN
                  ? AppointmentType.DOPING_SCREEN
                  : AppointmentType.MEDICAL_CLEARANCE,
              status, officialNotes);
        this.clearanceType = clearanceType;
        this.clearanceResult = clearanceResult;
        this.issuedBy = issuedBy;
        this.validUntil = validUntil;
        this.officialNotes = officialNotes;
    }

    // --- ClearanceTest-specific methods ---

    public boolean isCleared() {
        return clearanceResult == ClearanceResult.CLEARED;
    }

    // --- Getters and setters ---

    public ClearanceType getClearanceType() { return clearanceType; }
    public void setClearanceType(ClearanceType clearanceType) { this.clearanceType = clearanceType; }

    public ClearanceResult getClearanceResult() { return clearanceResult; }
    public void setClearanceResult(ClearanceResult clearanceResult) { this.clearanceResult = clearanceResult; }

    public String getIssuedBy() { return issuedBy; }
    public void setIssuedBy(String issuedBy) { this.issuedBy = issuedBy; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public String getOfficialNotes() { return officialNotes; }
    public void setOfficialNotes(String officialNotes) { this.officialNotes = officialNotes; }

    @Override
    public String toString() {
        return "ClearanceTest{id='" + getAppointmentId() +
               "', patient='" + (getPatient() != null ? getPatient().getFullName() : "none") +
               "', clearanceType=" + clearanceType + ", result=" + clearanceResult +
               "', issuedBy='" + issuedBy + "', validUntil=" + validUntil + "}";
    }
}
