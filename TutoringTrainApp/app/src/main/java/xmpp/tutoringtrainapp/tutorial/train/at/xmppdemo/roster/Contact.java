package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

import android.support.annotation.NonNull;

/**
 * Created by Elias on 28.01.2018.
 */

public class Contact implements Comparable<Contact> {
    private String username;
    private String fullName;
    private Type type;
    private int countNewMsgs;

    public enum Type {APPROVED, REQUESTED_BY_OTHER, REQUESTED_BY_ME, NONE}

    public Contact(String username, String fullName, Type type) {
        this.username = username;
        this.fullName = fullName;
        this.type = type;
        this.countNewMsgs = 0;
    }

    public Contact(String username, String fullName) {
        this(username, fullName, Type.APPROVED);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getCountNewMsgs() {
        return countNewMsgs;
    }

    public void setCountNewMsgs(int countNewMsgs) {
        this.countNewMsgs = countNewMsgs;
    }

    public synchronized void newMessage() {
        countNewMsgs++;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                ", countNewMsgs=" + countNewMsgs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (this.username == null || contact.getUsername() == null) {
            return false;
        }

        return username.equals(contact.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public int compareTo(@NonNull Contact o) {
        int retVal;

        retVal = this.getType().compareTo(o.getType());
        if (retVal == 0) {
            retVal = this.getFullName().compareTo(o.getFullName());
            if (retVal == 0) {
                retVal = this.getUsername().compareTo(o.getUsername());
            }
        }

        return retVal;
    }
}
