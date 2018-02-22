package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster;

/**
 * Created by Elias on 28.01.2018.
 */

public class Contact {
    private String username;
    private String fullName;
    private Type type;

    public enum Type {APPROVED, REQUESTED}

    public Contact(String username, String fullName, Type type) {
        this.username = username;
        this.fullName = fullName;
        this.type = type;
    }

    public Contact(String username, String fullName) {
        this.username = username;
        this.fullName = fullName;
        this.type = Type.APPROVED;
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

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        return username.equals(contact.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
