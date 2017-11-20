package at.tutoringtrain.adminclient.io.network.listener.user;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestUpdateAvatarListener extends RequestListener {
    public void requestUpdateAvatarFinished(RequestResult result, BufferedImage avatar);
}
