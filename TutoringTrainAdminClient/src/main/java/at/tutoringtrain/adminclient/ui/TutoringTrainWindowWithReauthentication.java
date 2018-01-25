package at.tutoringtrain.adminclient.ui;

import at.tutoringtrain.adminclient.io.network.WebserviceOperation;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface TutoringTrainWindowWithReauthentication extends TutoringTrainWindow {
    void reauthenticateUser(WebserviceOperation webserviceOperation) throws Exception;
}
