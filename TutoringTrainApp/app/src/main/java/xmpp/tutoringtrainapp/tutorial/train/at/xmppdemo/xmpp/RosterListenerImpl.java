package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.Jid;

import java.util.Collection;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

/**
 * Created by Elias on 02.03.2018.
 */

public class RosterListenerImpl implements RosterListener {
    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        for (Jid jid: addresses) {
            addEntry(jid);
        }
    }

    private void addEntry(Jid jid) {
        try {
            VCard vCardOfUser = XmppHandler.getInstance().getVCard(jid);
            String fullName = (vCardOfUser != null && vCardOfUser.getFirstName() != null) ?
                    vCardOfUser.getFirstName() :
                    jid.getLocalpartOrNull().toString();
            byte[] avatar = (vCardOfUser != null ? vCardOfUser.getAvatar() : null);

            RosterEntry rosterEntry = XmppHandler.getInstance().getRosterEntry(jid.asBareJid());
            if (rosterEntry.getType().equals(RosterPacket.ItemType.to) || rosterEntry.getType().equals(RosterPacket.ItemType.both)) {
                Contact contactToAdd = new Contact(
                        jid.getLocalpartOrNull().toString(),
                        fullName,
                        Contact.Type.APPROVED);
                contactToAdd.setAvatar(avatar);
                DataStore.getInstance().addContact(contactToAdd);
            }
            else if (rosterEntry.getType().equals(RosterPacket.ItemType.from)) {
                Contact contactToAdd = new Contact(
                        jid.getLocalpartOrNull().toString(),
                        fullName,
                        Contact.Type.REQUESTED_BY_OTHER);
                contactToAdd.setAvatar(avatar);
                XmppHandler.getInstance().addToRoster(contactToAdd);
            }
            else if (rosterEntry.getType().equals(RosterPacket.ItemType.none)) {
                Contact contactToAdd = new Contact(
                        jid.getLocalpartOrNull().toString(),
                        fullName,
                        Contact.Type.REQUESTED_BY_ME);
                contactToAdd.setAvatar(avatar);
                DataStore.getInstance().addContact(contactToAdd);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        for (Jid jid: addresses) {
            RosterEntry rosterEntry = XmppHandler.getInstance().getRosterEntry(jid.asBareJid());

            //if not subscribed anymore, remove from roster display
            if (rosterEntry.getType().equals(RosterPacket.ItemType.none)) {
                deleteEntry(jid);
            }
            //else use logic implemented in addEntry(...) to handle
            else {
                addEntry(jid);
            }
        }
        entriesAdded(addresses);
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        for (Jid jid: addresses) {
            deleteEntry(jid);
        }
    }

    private void deleteEntry(Jid jid) {
        try {
            Contact contactToRemove = new Contact(
                    jid.getLocalpartOrNull().toString(),
                    null,
                    Contact.Type.NONE);

            DataStore.getInstance().removeContact(contactToRemove);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void presenceChanged(Presence presence) {
        System.out.println("---------------- PRESENCE CHANGED: " + presence);
        //loadRoster();
    }
}
