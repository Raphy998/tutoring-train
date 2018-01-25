package at.tutoringtrain.adminclient.io.network;

import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RequestResult {
    private final static Logger LOGGER = LogManager.getLogger(RequestResult.class);
    
    private final int statusCode;
    private final String data;
    
    /**
     * 
     * @param code 
     * @param data 
     */
    public RequestResult(int code, String data) {
        this.statusCode = code;
        this.data = data;
    }

    /**
     * 
     * @return 
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 
     * @return 
     */
    public String getData() {
        return data;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isSuccessful() {
        return statusCode == 200;
    }
    
    /**
     * 
     * @return 
     */
    public MessageContainer getMessageContainer() {
        MessageContainer container = null;
        if (!isSuccessful()) {
            container = ApplicationManager.getInstance().getDataMapper().toMessageContainer(getData());
        }
        return container;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String toString() {
        StringBuilder builder;
        MessageContainer container;
        builder = new StringBuilder();
        builder.append(statusCode);
        builder.append(": ");
        if (!isSuccessful()) {
            container = getMessageContainer();
            if (container == null) {    
                builder.append("'");
                builder.append(data);
                builder.append("'");
            } else {
                builder.append(container.toString());
            } 
        }
        return builder.toString();
    }
}
