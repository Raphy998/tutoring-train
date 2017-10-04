/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import java.io.Serializable;

/**
 *
 * @author Elias
 */
public class Credentials implements Serializable {
    private String username;
    private String password;
    private String calledFrom;

    public Credentials() {}
    
    public Credentials(String username, String password, String calledFrom) {
        this.username = username;
        this.password = password;
        this.calledFrom = calledFrom;
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

    public String getCalledFrom() {
        return calledFrom;
    }

    public void setCalledFrom(String calledFrom) {
        this.calledFrom = calledFrom;
    }

    @Override
    public String toString() {
        return "Credentials{" + "username=" + username + ", password=" + password + ", calledFrom=" + calledFrom + '}';
    }
}