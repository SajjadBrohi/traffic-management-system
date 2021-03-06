package tms.util;

/**
 * Exception thrown when a saved network file is invalid.
 */
public class InvalidNetworkException extends Exception {
    /**
     * Constructs a normal InvalidNetworkException with no error message
     * or cause.
     */
    public InvalidNetworkException() {
        super();
    }

    /**
     * Constructs a InvalidNetworkException that contains a helpful
     * message detailing why the exception occurred.
     *
     * @param message detail message
     */
    public InvalidNetworkException(String message) {
        super(message);
    }

    /**
     * Constructs a InvalidNetworkException that contains a helpful
     * message detailing why the exception occurred.
     *
     * @param message detail message
     * @param err cause of the exception
     */
    public InvalidNetworkException(String message, Throwable err) {
        super(message, err);
    }
}
