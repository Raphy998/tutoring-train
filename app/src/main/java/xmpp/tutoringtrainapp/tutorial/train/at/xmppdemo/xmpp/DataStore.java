package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;


import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.ChatMessage;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

/**
 * Created by Elias on 27.01.2018.
 */

public class DataStore extends Application {
    private static DataStore instance;

    private ObservableArrayList<ChatMessage> chats;
    private ObservableArrayList<Contact> roster;

    private DataStore() {
        super();
        this.chats = new ObservableArrayList<>();
        this.roster = new ObservableArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void addOnChatsChangedCallback(ObservableList.OnListChangedCallback cb) {
        this.chats.addOnListChangedCallback(cb);
    }

    public void addOnRosterChangedCallback(ObservableList.OnListChangedCallback cb) {
        this.roster.addOnListChangedCallback(cb);
    }

    public void updateContact(Contact c) {
        synchronized (this) {
            int i = this.roster.indexOf(c);

            if (i == -1) {
                throw new IllegalArgumentException("contact " + c.getUsername() + " not found");
            } else {
                this.roster.set(i, c);
            }
        }
    }

    public int getContactCount() {
        return this.roster.size();
    }

    public int getChatMessageCount() {
        return this.chats.size();
    }

    public ChatMessage getChatMessageAt(int index) {
        return this.chats.get(index);
    }

    public Contact getContactAt(int index) {
        return this.roster.get(index);
    }

    public void clearChatMessages() {
        synchronized (this) {
            this.chats.clear();
        }
    }

    public void clearRoster() {
        synchronized (this) {
            this.roster.clear();
        }
    }

    public void addContact(Contact c) {
        synchronized (this) {
            this.roster.add(c);
        }
    }

    public boolean removeContact(Contact c) {
        synchronized (this) {
            return this.roster.remove(c);
        }
    }

    public void addChatMessage(ChatMessage msg) {
        synchronized (this) {
            this.chats.add(msg);
        }
    }

    private ArrayList<ChatMessage> getChats() {
        return new ArrayList<>(Arrays.asList(this.chats.toArray(new ChatMessage[0])));
    }

    private ArrayList<Contact> getRoster() {
        return new ArrayList<>(Arrays.asList(this.roster.toArray(new Contact[0])));
    }
}
