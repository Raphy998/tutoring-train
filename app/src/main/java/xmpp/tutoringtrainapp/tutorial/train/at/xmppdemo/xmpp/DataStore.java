package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;


import android.app.Application;
import android.databinding.ObservableArrayList;

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

    public ObservableArrayList<ChatMessage> getChats() {
        return chats;
    }

    public ObservableArrayList<Contact> getRoster() {
        return roster;
    }
}
