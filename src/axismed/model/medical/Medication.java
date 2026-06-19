package axismed.model.medical;

import java.time.LocalDate;

public class Medication {

    private String medicationId;
    private String patientId;
    private String prescribedByStaffId;
    private String name;
    private String dosage;
    private String frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isSupplement;
    private String notes;

    public Medication(String medicationId, String patientId, String prescribedByStaffId,
                      String name, String dosage, String frequency,
                      LocalDate startDate, LocalDate endDate, boolean isSupplement, String notes) {
        this.medicationId = medicationId;
        this.patientId = patientId;
        this.prescribedByStaffId = prescribedByStaffId;
        this.name = name;
        this.dosage = dosage;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isSupplement = isSupplement;
        this.notes = notes;
    }

    public String getMedicationId() { return medicationId; }
    public void setMedicationId(String medicationId) { this.medicationId = medicationId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPrescribedByStaffId() { return prescribedByStaffId; }
    public void setPrescribedByStaffId(String prescribedByStaffId) { this.prescribedByStaffId = prescribedByStaffId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isSupplement() { return isSupplement; }
    public void setSupplement(boolean isSupplement) { this.isSupplement = isSupplement; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Medication{id='" + medicationId + "', name='" + name + "', dosage='" + dosage +
               "', frequency='" + frequency + "', supplement=" + isSupplement +
               ", start=" + startDate + ", end=" + endDate + "}";
    }
}
