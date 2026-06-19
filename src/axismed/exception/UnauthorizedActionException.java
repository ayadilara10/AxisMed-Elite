package axismed.exception;

import axismed.enums.UserRole;

public class UnauthorizedActionException extends Exception {
    public UnauthorizedActionException(UserRole role, String action) {
        super("Role " + role + " is not authorized to perform: " + action);
    }
}
