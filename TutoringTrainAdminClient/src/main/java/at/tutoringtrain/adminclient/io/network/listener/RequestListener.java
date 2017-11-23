package at.tutoringtrain.adminclient.io.network.listener;

import at.tutoringtrain.adminclient.io.network.RequestResult;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public abstract interface RequestListener {
    public void requestFailed(RequestResult result);
}
