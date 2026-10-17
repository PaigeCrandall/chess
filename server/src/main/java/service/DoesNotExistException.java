package service;

public class DoesNotExistException extends Exception {
    public DoesNotExistException(String message) {
        super(("Error: " + message));
    }
    public DoesNotExistException(String message, Throwable ex) {
        super(("Error: " + message), ex);
    }
}
