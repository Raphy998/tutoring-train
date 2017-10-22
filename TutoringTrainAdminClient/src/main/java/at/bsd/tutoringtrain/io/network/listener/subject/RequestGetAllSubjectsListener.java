package at.bsd.tutoringtrain.io.network.listener.subject;

import at.bsd.tutoringtrain.io.network.RequestResult;
import at.bsd.tutoringtrain.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestGetAllSubjectsListener extends RequestListener {
    public void requestGetAllSubjectsFinished(RequestResult result);
}
