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
    ADMIN('A');
    
    private final Character abbrev;

    private UserRole(Character abbrev) {
        this.abbrev = abbrev;
    }
    
    public char getChar() {
        return this.abbrev;
    }
    
    public static boolean isValidRole(Character role) {
        boolean isValid = false;
        if (role.equals(USER.getChar()) || role.equals(MODERATOR.getChar())  || role.equals(ADMIN.getChar())) {
            isValid = true;
        }
        return isValid;
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
        else {
            throw new InvalidArgumentException(new ErrorBuilder(Error.ILLEGAL_ROLE).withParams(role));
        }
        
        return ur;
    }
}