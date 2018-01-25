package at.tutoringtrain.adminclient.io.network.listener.subject;

import at.tutoringtrain.adminclient.io.network.RequestResult;
import at.tutoringtrain.adminclient.io.network.listener.RequestListener;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface RequestRegisterSubjectListener extends RequestListener {
    public void requestRegisterSubjectFinished(RequestResult result);
}
