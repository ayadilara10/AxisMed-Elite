package axismed.model;

import axismed.model.medical.Diagnosis;
import java.util.List;

public interface Diagnosable {
    void addDiagnosis(Diagnosis diagnosis);
    List<Diagnosis> getDiagnoses();
    default String getDiagnosisSummary() {
        return "Total diagnoses: " + getDiagnoses().size();
    }
}
