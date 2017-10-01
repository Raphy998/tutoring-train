/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Elias
 */
public class CreateOfferRequest implements Serializable {
    private String description;
    private BigDecimal subject;

    public CreateOfferRequest() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSubject() {
        return subject;
    }

    public void setSubject(BigDecimal subject) {
        this.subject = subject;
    }
    
    
}
