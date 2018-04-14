package at.tutoringtrain.adminclient.io.network.listener.user.newsletter;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestNewsletterTemplateDefaultImagesListener extends RequestListener {
    public void requestNewsletterTemplateDefaultImagesFinished(RequestResult result);
}
