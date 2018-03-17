package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;


import android.app.Activity;
import android.app.Application;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.BaseAdapter;

import org.jivesoftware.smackx.mam.MamManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.ChatMessage;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.MessageChangeListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

/**
 * Created by Elias on 27.01.2018.
 */

public class DataStore extends Application {
    private static DataStore instance;

    private Activity ctx;
    private HashMap<String, ArrayList<ChatMessage>> chats;
    private BaseAdapter chatChangedListener;

    private ObservableArrayList<Contact> roster;
    private String msgRoster;
    private MessageChangeListener rosterMsgListener;
    private MamManager.MamQueryResult lastQueryResult;

    private DataStore() {
        super();
        this.chats = new HashMap<>();
        this.roster = new ObservableArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public void addOnChatsChangedObserver(BaseAdapter listener) {
        this.chatChangedListener = listener;
    }

    public void addOnRosterChangedCallback(ObservableList.OnListChangedCallback cb) {
        this.roster.addOnListChangedCallback(cb);
    }

    public void addOnMsgRosterChangedCallback(MessageChangeListener listener) {
        this.rosterMsgListener = listener;
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

    public Contact getContactByUsername(String username) {
        synchronized (this) {
            return this.roster.get(this.roster.indexOf(new Contact(username, "")));
        }
    }

    public int getContactCount() {
        return this.roster.size();
    }

    public boolean isChatLoaded(String username) {
        return this.chats.get(username) != null;
    }

    public int getChatMessageCount(String username) {
        return (this.chats.get(username) != null) ? this.chats.get(username).size() : 0;
    }

    public ChatMessage getChatMessageAt(int index, String username) {
        synchronized (this) {
            if (this.chats.get(username) == null) {
                this.chats.put(username, new ArrayList<ChatMessage>());
            }

            Collections.sort(this.chats.get(username));
            return this.chats.get(username).get(index);
        }
    }

    public Contact getContactAt(int index) {
        synchronized (this) {
            Collections.sort(this.roster);
            return this.roster.get(index);
        }
    }

    private void notifyChatListener() {
        if (chatChangedListener != null) {
            chatChangedListener.notifyDataSetChanged();
        }
    }

    public void clearChatMessages(final String username) {
        synchronized (this) {
            ctx.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if (instance.chats.get(username) != null && !instance.chats.get(username).isEmpty()) {
                          instance.chats.get(username).clear();
                          notifyChatListener();
                      }
                  }
              }
            );
        }
    }

    public void clearRoster() {
        synchronized (this) {
            this.roster.clear();
        }
    }

    public void addContact(Contact c) {
        synchronized (this) {
            if (roster.contains(c)) {
                removeContact(c);
            }
            this.roster.add(c);
        }
    }

    public boolean removeContact(Contact c) {
        synchronized (this) {
            return this.roster.remove(c);
        }
    }

    public void addChatMessage(final ChatMessage msg, final String username) {
        synchronized (this) {
            ctx.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      synchronized (roster) {
                          if (instance.chats.get(username) == null) {
                              instance.chats.put(username, new ArrayList<ChatMessage>());
                          }
                          instance.chats.get(username).add(msg);
                          notifyChatListener();

                          Contact c = getContactByUsername(username);
                          removeContact(c);
                          c.newMessage();
                          addContact(c);
                      }
                  }
              }
            );
        }
    }

    private ArrayList<ChatMessage> getChats(String username) {
        if (this.chats.get(username) == null) {
            this.chats.put(username, new ArrayList<ChatMessage>());
        }

        return this.chats.get(username);
    }

    private ArrayList<Contact> getRoster() {
        return new ArrayList<>(Arrays.asList(this.roster.toArray(new Contact[0])));
    }

    public void setMsgRoster(String msgRoster) {
        this.msgRoster = msgRoster;
        if (this.rosterMsgListener != null) {
            this.rosterMsgListener.onMessageUpdated(msgRoster);
        }
    }

    public String getMsgRoster() {
        return msgRoster;
    }

    public MamManager.MamQueryResult getLastQueryResult() {
        return lastQueryResult;
    }

    public void setLastQueryResult(MamManager.MamQueryResult lastQueryResult) {
        this.lastQueryResult = lastQueryResult;
    }

    public Activity getCtx() {
        return ctx;
    }

    public void setCtx(Activity ctx) {
        this.ctx = ctx;
    }
}
