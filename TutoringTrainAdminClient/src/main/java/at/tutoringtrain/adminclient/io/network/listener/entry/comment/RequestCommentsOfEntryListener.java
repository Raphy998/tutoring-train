package at.tutoringtrain.adminclient.io.network.listener.entry.comment;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestCommentsOfEntryListener extends RequestListener {
    public void requestGetCommentsOfEntryFinished(RequestResult result);
}
