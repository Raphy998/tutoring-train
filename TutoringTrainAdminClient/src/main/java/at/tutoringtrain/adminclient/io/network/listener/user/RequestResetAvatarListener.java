package at.tutoringtrain.adminclient.io.network.listener.user;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestResetAvatarListener extends RequestListener {
    public void requestResetAvatarFinished(RequestResult result);
}