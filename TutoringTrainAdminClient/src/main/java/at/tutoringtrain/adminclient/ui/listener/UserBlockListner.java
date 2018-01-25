package at.tutoringtrain.adminclient.ui.listener;

import at.tutoringtrain.adminclient.data.user.User;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public interface UserBlockListner {
    void userBlocked(User user);
    void userUnblocked();
}
