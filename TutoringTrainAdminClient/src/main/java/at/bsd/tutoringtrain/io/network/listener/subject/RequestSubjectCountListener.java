package at.bsd.tutoringtrain.io.network.listener.subject;

import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestSubjectCountListener extends RequestListener {
    public void requestSubjectCountFinished(RequestResult result);
}
