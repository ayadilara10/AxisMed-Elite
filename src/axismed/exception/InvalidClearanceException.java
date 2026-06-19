package axismed.exception;

public class InvalidClearanceException extends Exception {
    public InvalidClearanceException(String message) {
        super("Invalid clearance operation: " + message);
    }
}
