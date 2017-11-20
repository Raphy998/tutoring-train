package at.tutoringtrain.adminclient.io.network;

import java.io.Serializable;

/**
 *
 * @author Elias
 */
public class Credentials implements Serializable {
    private String username;
    private String password;
    private Character requiredRole;

    public Credentials() {}
    
    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
        this.requiredRole = 'U';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Character getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(Character requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public String toString() {
        return "Credentials{" + "username=" + username + ", password=" + password + ", requiredRole=" + requiredRole + '}';
    }
}