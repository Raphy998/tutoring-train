package at.bsd.tutoringtrain.io.network;

import at.bsd.tutoringtrain.data.mapper.DataMapper;
import at.bsd.tutoringtrain.debugging.MessageLogger;
import at.bsd.tutoringtrain.io.network.listener.RequestListener;
import at.bsd.tutoringtrain.messages.MessageCode;
import at.bsd.tutoringtrain.messages.MessageContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 * @param <T>
 */
public abstract class RequestCallback<T extends RequestListener> implements Callback {
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
            listener.requestFailed(new RequestResult(0, DataMapper.toJSON(new MessageContainer(MessageCode.REQUEST_FAILURE, e.getMessage()))));
        } catch (JsonProcessingException jpex) {
            MessageLogger.getINSTANCE().error(jpex.getMessage(), getClass());
        }
    }
}