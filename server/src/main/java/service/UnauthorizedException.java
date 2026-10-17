package service;

public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(("Error: unauthorized"));
    }
    public UnauthorizedException(String message, Throwable ex) {
        super("Error: unauthorized", ex);
    }
}
