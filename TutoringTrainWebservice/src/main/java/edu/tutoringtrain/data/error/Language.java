/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

import java.util.Locale;

/**
 *
 * @author Elias
 */
public enum Language {
    EN,
    DE,
    STAHL;
    
    public static Language getDefault() {
        return EN;
    }
    
    public Locale getLocale() {
        Locale loc;
        
        switch (this) {
            case EN:
                loc = Locale.ENGLISH;
                break;
            case DE:
                loc = Locale.GERMAN;
                break;
            case STAHL:
                loc = Locale.GERMAN;
                break;
            default:
                loc = Locale.ENGLISH;
                break;
        }
        
        return loc;
    }
}
