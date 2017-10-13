/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

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
}
