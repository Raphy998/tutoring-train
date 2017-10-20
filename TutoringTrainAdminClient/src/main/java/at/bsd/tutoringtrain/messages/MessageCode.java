package at.bsd.tutoringtrain.messages;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum MessageCode {
    OK(0),
    LOGIN_REQUIRED(801),
    REQUEST_FAILURE(901),
    EXCEPTION(999),
    UNDEFINED(-1);
    
    private final int code;

    private MessageCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
