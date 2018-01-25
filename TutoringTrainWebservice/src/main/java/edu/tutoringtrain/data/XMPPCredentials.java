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
public class XMPPCredentials implements Serializable {
    private String jid;
    private String password;

    public XMPPCredentials() {}
    
    public XMPPCredentials(String jid, String password) {
        this.jid = jid;
        this.password = password;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}