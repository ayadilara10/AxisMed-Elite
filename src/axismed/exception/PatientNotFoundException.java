package axismed.exception;

public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String patientId) {
        super("Patient not found: " + patientId);
    }
}
