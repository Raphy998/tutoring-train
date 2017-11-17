package at.bsd.tutoringtrain.io.network.listener.user;

import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestOwnUserListener extends RequestListener {
    public void requestOwnUserFinished(RequestResult result);
}
