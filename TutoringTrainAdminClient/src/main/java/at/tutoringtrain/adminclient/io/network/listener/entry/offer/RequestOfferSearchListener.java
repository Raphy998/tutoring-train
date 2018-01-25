package at.tutoringtrain.adminclient.io.network.listener.entry.offer;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestOfferSearchListener extends RequestListener {
    public void requestOfferSearchFinished(RequestResult result);
}
