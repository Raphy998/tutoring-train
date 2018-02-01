package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener;

import android.support.v4.app.Fragment;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

/**
 * Created by Elias on 31.01.2018.
 */

public interface FragmentInteractionListener {
    void openChatWithUser(Object sender, Contact c);
    void showRoster(Fragment sender);
}
