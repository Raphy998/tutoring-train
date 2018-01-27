package at.tutoringtrain.adminclient.main;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class MessageContainer {
    private int code;
    private String message;
    
    public MessageContainer() {
        this(MessageCodes.UNDEFINED, null);
    }
    
    public MessageContainer(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public void addPrefix(String prefix) {
        message = prefix.concat(message);
    }
    
    public void addPostfix(String postfix) {
        message = message.concat(postfix);
    }

    @Override
    public String toString() {
        return (getCode() != MessageCodes.OK ? "[" + getCode() + "] " : "") + getMessage();
    }
}
