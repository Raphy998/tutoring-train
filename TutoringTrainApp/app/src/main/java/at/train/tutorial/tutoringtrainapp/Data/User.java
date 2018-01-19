package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author  Raphael Moser
 * created on 06.01.2018.
 */

public class User {
    @JsonView ({Views.User.Out.Login.class})
    private String username;
    private String email;
    private String name;
    private String education;
    @JsonView ({Views.User.Out.Login.class})
    private String password;
    private UserRole role;
    private Gender gender;

    // TODO: 06.01.2018 avatar
    // TODO: 06.01.2018 Rating

    public User(){
    }

    public User (String username,String email, String name, String education, String password,UserRole role, Gender gender){
        this.username = username;
        this.email = email;
        this.education = education;
        this.name = name;
        this.password = password;
        this.role = role;
        this.gender = gender;
    }

    public User(String username,String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getEducation() {
        return education;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public Gender getGender() {
        return gender;
    }
}
