/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

/**
 *
 * @author Elias
 */
public enum EntryType {
    OFFER('O'),
    REQUEST('R');
    
    private final Character abbrev;

    private EntryType(Character abbrev) {
        this.abbrev = abbrev;
    }
    
    public char getChar() {
        return this.abbrev;
    }
    
    public static boolean isValidType(Character type) {
        boolean isValid = false;
        if (type.equals(OFFER.getChar()) || type.equals(REQUEST.getChar())) {
            isValid = true;
        }
        return isValid;
    }
    
    public static EntryType toEntryType(Character type) throws Exception {
        EntryType et;
        if (type.equals(OFFER.getChar())) {
            et = OFFER;
        }
        else if (type.equals(REQUEST.getChar())) {
            et = REQUEST;
        }
        else {
            throw new Exception("invalid entry type '" + type + "'");
        }
        
        return et;
    }

    @Override
    public String toString() {
        return (this == OFFER) ? "Offer" : "Request";
    }
    
    
}
