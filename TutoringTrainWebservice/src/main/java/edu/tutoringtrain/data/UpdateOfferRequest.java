/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import edu.tutoringtrain.utils.DateUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Elias
 */
public class UpdateOfferRequest implements Serializable {
    private String description;
    private BigDecimal id;
    private Character isactive;
    private String postedon;
    private String duedate;
    private BigDecimal subject;

    public UpdateOfferRequest() {
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

    public Date getPostedonDate() {
        return (postedon != null ? DateUtils.toDate(postedon) : null);
    }

    public void setPostedon(String date) {
        this.postedon = date;
    }

    public Date getDuedateDate() {
        return (duedate != null) ? DateUtils.toDate(duedate) : null;
    }

    public void setDuedate(String date) {
        this.duedate = date;
    }

    public BigDecimal getSubject() {
        return subject;
    }

    public void setSubject(BigDecimal subject) {
        this.subject = subject;
    }

    public String getPostedon() {
        return postedon;
    }

    public String getDuedate() {
        return duedate;
    }
    
    
    
}
