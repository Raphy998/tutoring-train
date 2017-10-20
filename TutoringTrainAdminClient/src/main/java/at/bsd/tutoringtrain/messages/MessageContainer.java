package at.bsd.tutoringtrain.messages;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MessageContainer {
    private int code;
    private String message;
    
    /**
     * 
     */
    public MessageContainer() {
        this(MessageCode.UNDEFINED, "");
    }
    
    /**
     * 
     * @param code
     * @param message 
     */
    public MessageContainer(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * 
     * @param code
     * @param message 
     */
    public MessageContainer(MessageCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }

    /**
     * 
     * @return 
     */
    public int getCode() {
        return code;
    }
    
    /**
     * 
     * @param code 
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    /**
     * 
     * @param code 
     */
    public void setCode(MessageCode code) {
        this.code = code.getCode();
    }
    
    /**
     * 
     * @return 
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message 
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        return (getCode() != 0 ? "[" + getCode() + "] " : "") + getMessage();
    }
}
