package axismed.exception;

public class SchedulingConflictException extends Exception {
    public SchedulingConflictException(String message) {
        super("Scheduling conflict: " + message);
    }
}
