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
public class RegisterUserRequest implements Serializable {
    private String username;
    private String password;
    private String email;
    private String name;
    private BigDecimal gender;

    public RegisterUserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getGender() {
        return gender;
    }

    public void setGender(BigDecimal gender) {
        this.gender = gender;
    }
    
    
}
