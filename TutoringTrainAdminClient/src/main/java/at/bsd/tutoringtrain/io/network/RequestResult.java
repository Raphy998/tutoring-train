package at.bsd.tutoringtrain.io.network;

import at.bsd.tutoringtrain.messages.MessageContainer;
import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import java.io.IOException;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class RequestResult {
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
            try {
                container = DataMapper.toMessageContainer(getData());
            } catch (IOException ioex) {
                MessageLogger.getINSTANCE().exception(ioex, getClass());
            }
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
