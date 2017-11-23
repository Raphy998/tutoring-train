package at.tutoringtrain.adminclient.exception;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RequiredParameterException extends Exception {

    /**
     * Creates a new instance of <code>RequiredParameterException</code> without
     * detail message.
     */
    public RequiredParameterException() {
    }

    /**
     * Constructs an instance of <code>RequiredParameterException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RequiredParameterException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>RequiredParameterException</code> with
     * the specified detail message.
     *
     * @param parameter
     */
    public RequiredParameterException(Object parameter, String message) {
        super(parameter.getClass().getSimpleName() + ": " + message);
    }
}
