/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import edu.tutoringtrain.data.error.Language;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Elias
 */
public class Gender {
    public static final Character MALE = 'M';
    public static final Character FEMALE = 'F';
    public static final Character OTHER = 'N';
    
    private Character code;
    private String name;
    
    public Gender(Character code, String name) {
        this.code = code;
        this.name = name;
    }

    public Character getCode() {
        return code;
    }

    public void setCode(Character code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static Gender get(Character code, Language lang) {
        Gender g;
        
        if (lang.equals(Language.EN)) {
            if (code.equals(MALE)) {
                g = new Gender(MALE, "Male");
            }
            else if (code.equals(FEMALE)) {
                g = new Gender(FEMALE, "Female");
            }
            else {
                g = new Gender(OTHER, "Other");
            }
        }
        else {
            if (code.equals(MALE)) {
                g = new Gender(MALE, "Männlich");
            }
            else if (code.equals(FEMALE)) {
                g = new Gender(FEMALE, "Weiblich");
            }
            else {
                g = new Gender(OTHER, "Sonstiges");
            }
        }
        
        return g;
    }
    
    public static List<Gender> getAll(Language lang) {
        List<Gender> genders = new ArrayList<>();
        
        if (lang == Language.EN) {
            genders.add(new Gender(MALE, "Male"));
            genders.add(new Gender(FEMALE, "Female"));
            genders.add(new Gender(OTHER, "Other"));
        }
        else {
            genders.add(new Gender(MALE, "Männlich"));
            genders.add(new Gender(FEMALE, "Weiblich"));
            genders.add(new Gender(OTHER, "Sonstiges"));
        }
        
        return genders;
    }
    
    public static boolean isValidGender(Character code) {
        return code.equals(MALE) || code.equals(FEMALE) || code.equals(OTHER);
    }
}
