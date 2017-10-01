/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import edu.tutoringtrain.entities.Subject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Elias
 */
public class OfferResponse implements Serializable {
    private String description;
    private BigDecimal id;
    private Character isactive;
    private Date postedon;
    private Date duedate;
    private Subject subject;
    private String username;

    public OfferResponse() {
    }

    public OfferResponse(String description, BigDecimal id, Character isactive, Date postedon, Date duedate, Subject subject, String username) {
        this.description = description;
        this.id = id;
        this.isactive = isactive;
        this.postedon = postedon;
        this.subject = subject;
        this.username = username;
        this.duedate = duedate;
    }

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }
    
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Character getIsactive() {
        return isactive;
    }

    public void setIsactive(Character isactive) {
        this.isactive = isactive;
    }

    public Date getPostedon() {
        return postedon;
    }

    public void setPostedon(Date postedon) {
        this.postedon = postedon;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    
}
