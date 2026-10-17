package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(("Error: " + message));
    }
    public DataAccessException(String message, Throwable ex) {
        super(("Error: " + message), ex);
    }
}
