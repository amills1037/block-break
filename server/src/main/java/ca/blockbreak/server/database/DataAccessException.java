package ca.blockbreak.server.database;

/**
 * DataAccessException implementation
 * Used to throw a data access exception.  Extends RuntimeException so you do not have to declare the throw.
 */
public class DataAccessException extends RuntimeException {

    /**
     * Constructor
     *
     * @param message message to throw
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param message message to throw
     * @param cause root cause throwable
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
