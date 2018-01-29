package at.tutoringtrain.adminclient.io.network.listener.entry.request;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestDeleteRequestListener extends RequestListener {
    public void requestDeleteRequestFinished(RequestResult result);
}
