package axismed.exception;

public class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super("Duplicate record: " + message);
    }
}
