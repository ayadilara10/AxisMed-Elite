package axismed.model.medical;

import axismed.model.appointment.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MedicalRecord {

    private String recordId;
    private String patientId;
    private LocalDate createdDate;
    private TreeMap<LocalDateTime, Appointment> appointmentHistory;
    private List<Diagnosis> diagnosisHistory;
    private List<TreatmentPlan> treatmentHistory;
    private List<FunctionalTest> testHistory;
    private List<Medication> medicationHistory;
    private List<String> generalNotes;
    private String lastUpdatedByClinic;

    public MedicalRecord(String recordId, String patientId, LocalDate createdDate) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.createdDate = createdDate;
        this.appointmentHistory = new TreeMap<>();
        this.diagnosisHistory = new ArrayList<>();
        this.treatmentHistory = new ArrayList<>();
        this.testHistory = new ArrayList<>();
        this.medicationHistory = new ArrayList<>();
        this.generalNotes = new ArrayList<>();
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public List<Appointment> getAppointmentHistory() {
        return new ArrayList<>(appointmentHistory.values());
    }

    public List<Diagnosis> getDiagnosisHistory() { return diagnosisHistory; }
    public void setDiagnosisHistory(List<Diagnosis> diagnosisHistory) { this.diagnosisHistory = diagnosisHistory; }

    public List<TreatmentPlan> getTreatmentHistory() { return treatmentHistory; }
    public void setTreatmentHistory(List<TreatmentPlan> treatmentHistory) { this.treatmentHistory = treatmentHistory; }

    public List<FunctionalTest> getTestHistory() { return testHistory; }
    public void setTestHistory(List<FunctionalTest> testHistory) { this.testHistory = testHistory; }

    public List<Medication> getMedicationHistory() { return medicationHistory; }
    public void setMedicationHistory(List<Medication> medicationHistory) { this.medicationHistory = medicationHistory; }

    public List<String> getGeneralNotes() { return generalNotes; }
    public void setGeneralNotes(List<String> generalNotes) { this.generalNotes = generalNotes; }

    public String getLastUpdatedByClinic() { return lastUpdatedByClinic; }
    public void setLastUpdatedByClinic(String lastUpdatedByClinic) { this.lastUpdatedByClinic = lastUpdatedByClinic; }

    public void addAppointmentEntry(Appointment appointment) {
        appointmentHistory.put(appointment.getDateTime(), appointment);
    }

    public void addDiagnosisEntry(Diagnosis diagnosis) {
        diagnosisHistory.add(diagnosis);
    }

    public void addTreatmentEntry(TreatmentPlan plan) {
        treatmentHistory.add(plan);
    }

    public void addTestEntry(FunctionalTest test) {
        testHistory.add(test);
    }

    public void addMedicationEntry(Medication medication) {
        medicationHistory.add(medication);
    }

    public void addNote(String note) {
        generalNotes.add(note);
    }

    public String getFullHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Medical Record: ").append(recordId).append(" ===\n");
        sb.append("Patient ID: ").append(patientId).append("\n");
        sb.append("Created: ").append(createdDate).append("\n");
        sb.append("Last Updated By: ").append(lastUpdatedByClinic).append("\n\n");

        sb.append("--- Appointments (").append(appointmentHistory.size()).append(") ---\n");
        for (Appointment a : appointmentHistory.values()) {
            sb.append("  ").append(a).append("\n");
        }

        sb.append("--- Diagnoses (").append(diagnosisHistory.size()).append(") ---\n");
        for (Diagnosis d : diagnosisHistory) {
            sb.append("  ").append(d).append("\n");
        }

        sb.append("--- Treatment Plans (").append(treatmentHistory.size()).append(") ---\n");
        for (TreatmentPlan t : treatmentHistory) {
            sb.append("  ").append(t).append("\n");
        }

        sb.append("--- Functional Tests (").append(testHistory.size()).append(") ---\n");
        for (FunctionalTest f : testHistory) {
            sb.append("  ").append(f).append("\n");
        }

        sb.append("--- Medications (").append(medicationHistory.size()).append(") ---\n");
        for (Medication m : medicationHistory) {
            sb.append("  ").append(m).append("\n");
        }

        sb.append("--- Notes (").append(generalNotes.size()).append(") ---\n");
        for (String note : generalNotes) {
            sb.append("  ").append(note).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "MedicalRecord{id='" + recordId + "', patientId='" + patientId +
               "', createdDate=" + createdDate + ", appointments=" + appointmentHistory.size() +
               ", diagnoses=" + diagnosisHistory.size() + ", lastUpdatedBy='" + lastUpdatedByClinic + "'}";
    }
}
