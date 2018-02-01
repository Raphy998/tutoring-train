package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener;

import android.support.v4.app.Fragment;

/**
 * Created by Elias on 31.01.2018.
 */

public interface FragmentInteractionListener {
    void openChatWithUser(Object sender, String username);
    void showRoster(Fragment sender);
}
