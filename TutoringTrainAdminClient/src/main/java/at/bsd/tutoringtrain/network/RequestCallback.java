package at.bsd.tutoringtrain.network;

import at.bsd.tutoringtrain.network.enumeration.ResultType;
import at.bsd.tutoringtrain.network.listener.RequestListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import okhttp3.Call;
import okhttp3.Callback;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
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
        listener.requestFailed(new Result(0, ResultType.ERROR, null, e.getMessage()));
    }
}