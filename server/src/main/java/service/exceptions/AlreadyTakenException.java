package service.exceptions;

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) { super(("Error: " + message)); }
    public AlreadyTakenException(String message, Throwable ex) {
        super(("Error: " + message), ex);
    }
}