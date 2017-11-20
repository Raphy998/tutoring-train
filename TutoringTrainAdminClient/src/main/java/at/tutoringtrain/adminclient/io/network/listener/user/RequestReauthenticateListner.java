package at.tutoringtrain.adminclient.io.network.listener.user;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestReauthenticateListner extends RequestListener {
    public void requestReauthenticateFinished(RequestResult result);
}
