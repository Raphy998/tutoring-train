package at.tutoringtrain.adminclient.io.network.listener.user.newsletter;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestNewsletterTemplateListener extends RequestListener {
    public void requestNewsletterTemplateFinished(RequestResult result);
}
