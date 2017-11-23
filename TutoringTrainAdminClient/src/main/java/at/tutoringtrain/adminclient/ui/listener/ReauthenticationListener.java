package at.tutoringtrain.adminclient.ui.listener;

import at.tutoringtrain.adminclient.io.network.WebserviceOperation;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface ReauthenticationListener {
    void reauthenticationCanceled();
    void reauthenticationSuccessful(WebserviceOperation webserviceOperation);
}
