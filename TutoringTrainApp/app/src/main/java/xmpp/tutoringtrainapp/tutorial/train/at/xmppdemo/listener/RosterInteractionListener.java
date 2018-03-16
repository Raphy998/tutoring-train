package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

/**
 * Created by Elias on 31.01.2018.
 */

public interface RosterInteractionListener {
    void removeFromRoster(Contact c);
    void addToRoster(Contact c);
}
