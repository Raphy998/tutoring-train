/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.annotations;

import edu.tutoringtrain.data.UserRole;
import java.security.Principal;

/**
 *
 * @author Elias
 */
public class PrincipalInRole implements Principal {

    private String username;
    private UserRole role;
    
    public PrincipalInRole(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }
    
    @Override
    public String getName() {
        return username;
    }
    
    public UserRole getRole() {
        return role;
    }
}
