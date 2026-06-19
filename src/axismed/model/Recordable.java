package axismed.model;

import axismed.model.medical.MedicalRecord;

public interface Recordable {
    void updateRecord(String note);
    MedicalRecord getRecord();
    default String getRecordSummary() {
        return "Record ID: " + getRecord().getRecordId();
    }
}
