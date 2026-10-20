package service.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException(String message) { super(("Error: " + message)); }
    public BadRequestException(String message, Throwable ex) { super(("Error: " + message), ex); }
}
