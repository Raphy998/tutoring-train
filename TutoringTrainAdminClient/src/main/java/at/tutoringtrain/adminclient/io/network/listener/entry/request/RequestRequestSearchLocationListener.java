package at.tutoringtrain.adminclient.io.network.listener.entry.request;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestRequestSearchLocationListener extends RequestListener {
    public void requestRequestSearchLocationFinished(RequestResult result);
}
