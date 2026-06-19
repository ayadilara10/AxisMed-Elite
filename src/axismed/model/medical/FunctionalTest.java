package axismed.model.medical;

import axismed.enums.ResultType;
import java.time.LocalDate;

public class FunctionalTest {

    private String testId;
    private String patientId;
    private String staffId;
    private String appointmentId;
    private String testName;
    private ResultType resultType;
    private String resultValue;
    private String unit;
    private LocalDate testDate;
    private String clinicCity;
    private String notes;
    private String linkedTreatmentPlanId;

    public FunctionalTest(String testId, String patientId, String staffId, String appointmentId,
                          String testName, ResultType resultType, String resultValue, String unit,
                          LocalDate testDate, String clinicCity, String notes, String linkedTreatmentPlanId) {
        this.testId = testId;
        this.patientId = patientId;
        this.staffId = staffId;
        this.appointmentId = appointmentId;
        this.testName = testName;
        this.resultType = resultType;
        this.resultValue = resultValue;
        this.unit = unit;
        this.testDate = testDate;
        this.clinicCity = clinicCity;
        this.notes = notes;
        this.linkedTreatmentPlanId = linkedTreatmentPlanId;
    }

    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public ResultType getResultType() { return resultType; }
    public void setResultType(ResultType resultType) { this.resultType = resultType; }

    public String getResultValue() { return resultValue; }
    public void setResultValue(String resultValue) { this.resultValue = resultValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDate getTestDate() { return testDate; }
    public void setTestDate(LocalDate testDate) { this.testDate = testDate; }

    public String getClinicCity() { return clinicCity; }
    public void setClinicCity(String clinicCity) { this.clinicCity = clinicCity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLinkedTreatmentPlanId() { return linkedTreatmentPlanId; }
    public void setLinkedTreatmentPlanId(String linkedTreatmentPlanId) { this.linkedTreatmentPlanId = linkedTreatmentPlanId; }

    @Override
    public String toString() {
        return "FunctionalTest{id='" + testId + "', testName='" + testName +
               "', resultType=" + resultType + ", resultValue='" + resultValue +
               "', unit='" + unit + "', testDate=" + testDate + ", clinicCity='" + clinicCity + "'}";
    }
}
