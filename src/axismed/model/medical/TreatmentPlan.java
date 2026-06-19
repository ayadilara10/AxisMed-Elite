package axismed.model.medical;

import axismed.enums.TreatmentStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TreatmentPlan {

    private String planId;
    private String patientId;
    private String createdByStaffId;
    private String diagnosisId;
    private LocalDate startDate;
    private LocalDate endDate;
    private TreatmentStatus status;
    private String freeTextRecommendations;
    private RehabilitationProtocol rehabProtocol;
    private List<Medication> medications;
    private List<String> progressNotes;

    public TreatmentPlan(String planId, String patientId, String createdByStaffId,
                         String diagnosisId, LocalDate startDate, LocalDate endDate,
                         TreatmentStatus status, String freeTextRecommendations) {
        this.planId = planId;
        this.patientId = patientId;
        this.createdByStaffId = createdByStaffId;
        this.diagnosisId = diagnosisId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.freeTextRecommendations = freeTextRecommendations;
        this.rehabProtocol = null;
        this.medications = new ArrayList<>();
        this.progressNotes = new ArrayList<>();
    }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getCreatedByStaffId() { return createdByStaffId; }
    public void setCreatedByStaffId(String createdByStaffId) { this.createdByStaffId = createdByStaffId; }

    public String getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(String diagnosisId) { this.diagnosisId = diagnosisId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public TreatmentStatus getStatus() { return status; }
    public void setStatus(TreatmentStatus status) { this.status = status; }

    public String getFreeTextRecommendations() { return freeTextRecommendations; }
    public void setFreeTextRecommendations(String freeTextRecommendations) {
        this.freeTextRecommendations = freeTextRecommendations;
    }

    public RehabilitationProtocol getRehabProtocol() { return rehabProtocol; }
    public void setRehabProtocol(RehabilitationProtocol rehabProtocol) { this.rehabProtocol = rehabProtocol; }

    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }

    public List<String> getProgressNotes() { return progressNotes; }
    public void setProgressNotes(List<String> progressNotes) { this.progressNotes = progressNotes; }

    public void addProgressNote(String note) {
        progressNotes.add(note);
    }

    public void markCompleted() {
        this.status = TreatmentStatus.COMPLETED;
    }

    public void putOnHold() {
        this.status = TreatmentStatus.ON_HOLD;
    }

    @Override
    public String toString() {
        return "TreatmentPlan{id='" + planId + "', patientId='" + patientId +
               "', status=" + status + ", start=" + startDate + ", end=" + endDate +
               ", rehabProtocol=" + (rehabProtocol != null ? rehabProtocol.getProtocolId() : "none") +
               ", medications=" + medications.size() + "}";
    }
}
