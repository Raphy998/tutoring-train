package at.bsd.tutoringtrain.io.network.listener.subject;

import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestDeleteSubjectListener extends RequestListener {
    public void requestDeleteSubjectFinished(RequestResult result);
}
