package axismed.exception;

public class StaffNotFoundException extends Exception {
    public StaffNotFoundException(String staffId) {
        super("Staff member not found: " + staffId);
    }
}
