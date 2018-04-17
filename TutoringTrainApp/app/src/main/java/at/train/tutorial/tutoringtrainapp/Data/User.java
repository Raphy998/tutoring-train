package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.Serializable;

/**
 * @author  Raphael Moser
 * created on 06.01.2018.
 */

public class User implements Serializable{
    @JsonIgnoreProperties
    @JsonView ({Views.User.Out.Login.class,Views.Comment.In.loadNewest.class,Views.User.Out.Register.class,Views.User.Out.RegisterNoPassword.class})
    private String username;
    @JsonView ({Views.User.Out.Register.class,Views.User.Out.RegisterNoPassword.class})
    private String email;
    @JsonView ({Views.User.Out.Register.class,Views.User.Out.RegisterNoPassword.class})
    private String name;
    @JsonView ({Views.User.Out.Register.class,Views.User.Out.RegisterNoPassword.class})
    private String education;
    @JsonView ({Views.User.Out.Login.class,Views.User.Out.Register.class})
    private String password;
    private UserRole role;
    @JsonView ({Views.User.Out.Register.class,Views.User.Out.RegisterNoPassword.class})
    private Gender gender;
    private float averagerating;

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

    public User(String username,String email, String name,String education,String password,Gender gender){
        this.username = username;
        this.email = email;
        this.education = education;
        this.name = name;
        this.password = password;
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

    public float getAveragerating() {
        return averagerating;
    }

    public void setAveragerating(float averagerating) {
        this.averagerating = averagerating;
    }
}
