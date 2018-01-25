/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;

/**
 *
 * @author Elias
 */
public enum UserRole {
    USER('U'),
    MODERATOR('M'),
    ADMIN('A'),
    ROOT('R');
    
    private final Character abbrev;

    private UserRole(Character abbrev) {
        this.abbrev = abbrev;
    }
    
    public char getChar() {
        return this.abbrev;
    }
    
    public static boolean isValidRole(Character role) {
        boolean isValid = false;
        if (role.equals(USER.getChar()) || role.equals(MODERATOR.getChar())  || role.equals(ADMIN.getChar()) || role.equals(ROOT.getChar())) {
            isValid = true;
        }
        return isValid;
    }
    
    public boolean isAdmin() {
        return this == ADMIN || this == ROOT;
    }
    
    public boolean hasPermission(UserRole requiredRole) {
        boolean hasP = false;
        
        switch (requiredRole) {
            case ROOT:
                if (this == ROOT) hasP = true;
                break;
            case ADMIN:
                if (this == ROOT || this == ADMIN) hasP = true;
                break;
            case MODERATOR:
                if (this == ROOT || this == ADMIN || this == MODERATOR) hasP = true;
                break;
            case USER:
                if (this == ROOT || this == ADMIN || this == MODERATOR || this == USER) hasP = true;
                break;
        }
        
        return hasP;
    }
    
    public static UserRole toUserRole(Character role) throws InvalidArgumentException {
        UserRole ur;
        if (role.equals(USER.getChar())) {
            ur = USER;
        }
        else if (role.equals(MODERATOR.getChar())) {
            ur = MODERATOR;
        }
        else if (role.equals(ADMIN.getChar())) {
            ur = ADMIN;
        }
        else if (role.equals(ROOT.getChar())) {
            ur = ROOT;
        }
        else {
            throw new InvalidArgumentException(new ErrorBuilder(Error.ILLEGAL_ROLE).withParams(role));
        }
        
        return ur;
    }
}