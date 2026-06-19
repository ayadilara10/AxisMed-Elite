package axismed.model.medical;

import java.time.LocalDate;

public class Diagnosis {

    private String diagnosisId;
    private String patientId;
    private String doctorId;
    private String appointmentId;
    private String clinicCity;
    private String diagnosisName;
    private String description;
    private String icdCode;
    private LocalDate diagnosisDate;
    private boolean isChronicRelated;

    public Diagnosis(String diagnosisId, String patientId, String doctorId, String appointmentId,
                     String clinicCity, String diagnosisName, String description, String icdCode,
                     LocalDate diagnosisDate, boolean isChronicRelated) {
        this.diagnosisId = diagnosisId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentId = appointmentId;
        this.clinicCity = clinicCity;
        this.diagnosisName = diagnosisName;
        this.description = description;
        this.icdCode = icdCode;
        this.diagnosisDate = diagnosisDate;
        this.isChronicRelated = isChronicRelated;
    }

    public String getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(String diagnosisId) { this.diagnosisId = diagnosisId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getClinicCity() { return clinicCity; }
    public void setClinicCity(String clinicCity) { this.clinicCity = clinicCity; }

    public String getDiagnosisName() { return diagnosisName; }
    public void setDiagnosisName(String diagnosisName) { this.diagnosisName = diagnosisName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcdCode() { return icdCode; }
    public void setIcdCode(String icdCode) { this.icdCode = icdCode; }

    public LocalDate getDiagnosisDate() { return diagnosisDate; }
    public void setDiagnosisDate(LocalDate diagnosisDate) { this.diagnosisDate = diagnosisDate; }

    public boolean isChronicRelated() { return isChronicRelated; }
    public void setChronicRelated(boolean isChronicRelated) { this.isChronicRelated = isChronicRelated; }

    @Override
    public String toString() {
        return "Diagnosis{id='" + diagnosisId + "', name='" + diagnosisName +
               "', icdCode='" + icdCode + "', date=" + diagnosisDate +
               ", clinicCity='" + clinicCity + "', chronicRelated=" + isChronicRelated + "}";
    }
}
