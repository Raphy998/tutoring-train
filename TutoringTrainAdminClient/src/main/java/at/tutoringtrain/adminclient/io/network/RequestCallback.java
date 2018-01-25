package at.tutoringtrain.adminclient.io.network;

import at.tutoringtrain.adminclient.io.network.listener.RequestListener;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 * @param <T>
 */
public abstract class RequestCallback<T extends RequestListener> implements Callback {
    private final static Logger LOGGER = LogManager.getLogger(RequestCallback.class);
    
    private final T listener;
    
    /**
     * 
     * @param listener 
     */
    public RequestCallback(T listener) {
        this.listener = listener;
    } 

    /**
     * 
     * @return 
     */
    public T getListener() {
        return listener;
    }

    /**
     * 
     * @param call
     * @param e 
     */
    @Override
    public void onFailure(Call call, IOException e) {
        try {
            listener.requestFailed(new RequestResult(0, ApplicationManager.getInstance().getDataMapper().toJSON(new MessageContainer(MessageCodes.REQUEST_FAILED, e.getMessage()))));
        } catch (JsonProcessingException jpex) {
            LOGGER.error("json processing failed", jpex);
        }
    }
}