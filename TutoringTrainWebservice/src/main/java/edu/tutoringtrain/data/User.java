/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import java.io.Serializable;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author Elias
 */
public final class User implements Comparable, Serializable {
    private String name;
    private String username;
    private String email;
    private String password_enc;

    public User(String name, String username, String email, String password_enc) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password_enc = password_enc;
    }
    
    public User() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_enc() {
        return password_enc;
    }

    public void setPassword_enc(String password_enc) {
        this.password_enc = password_enc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    @Override
    public int compareTo(Object o) {
        return getUsername().compareTo(((User)o).getUsername());
    }
    
    public static boolean isEmailValid(String email) {
        boolean isValid = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public String toString() {
        return "User{" + "name=" + name + ", username=" + username + ", email=" + email + ", password_enc=" + password_enc + '}';
    }
    
    
}
