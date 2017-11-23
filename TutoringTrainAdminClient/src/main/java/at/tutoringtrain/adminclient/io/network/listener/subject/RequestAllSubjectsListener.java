package at.tutoringtrain.adminclient.io.network.listener.subject;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestAllSubjectsListener extends RequestListener {
    public void requestGetAllSubjectsFinished(RequestResult result);
}
