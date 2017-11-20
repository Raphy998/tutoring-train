package at.tutoringtrain.adminclient.io.network.listener.user;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;
import java.io.InputStream;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestGetAvatarListener extends RequestListener {
    public void requestAvatarFinished(RequestResult result, InputStream iStream);
}
