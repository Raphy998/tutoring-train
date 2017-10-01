/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import edu.tutoringtrain.entities.Gender;
import edu.tutoringtrain.entities.User;
import java.math.BigDecimal;

/**
 *
 * @author Elias
 */
public class UserResponse {
    private String username;
    private Character role;
    private String email;
    private String name;
    private BigDecimal averagerating;
    private String education;
    private Gender gender;
    private Character locked;

    public UserResponse() {
    }
    
    public UserResponse(User user) {
        setUsername(user.getUsername());
        setRole(user.getRole());
        setEmail(user.getEmail());
        setName(user.getName());
        setAveragerating(user.getAveragerating());
        setEducation(user.getEducation());
        setGender(user.getGender());
        //setLocked(user.getLocked());
    }

    public Character getLocked() {
        return locked;
    }

    public void setLocked(Character locked) {
        this.locked = locked;
    }

    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Character getRole() {
        return role;
    }

    public void setRole(Character role) {
        this.role = role;
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

    public BigDecimal getAveragerating() {
        return averagerating;
    }

    public void setAveragerating(BigDecimal averagerating) {
        this.averagerating = averagerating;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    
}
