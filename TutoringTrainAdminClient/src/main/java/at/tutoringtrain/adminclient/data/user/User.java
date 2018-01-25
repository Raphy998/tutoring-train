package at.tutoringtrain.adminclient.data.user;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.security.PasswordHashGenerator;
import com.fasterxml.jackson.annotation.JsonView;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public final class User {
    @JsonView ({
        DataMappingViews.User.Out.Register.class, 
        DataMappingViews.User.In.Register.class, 
        DataMappingViews.User.In.Get.class, 
        DataMappingViews.User.Out.Update.class,
        DataMappingViews.User.Out.UpdatePassowrd.class,
        DataMappingViews.Entry.In.Get.class
    })
    private String username;
    
    @JsonView ({
        DataMappingViews.User.Out.Register.class,
        DataMappingViews.User.In.Register.class,
        DataMappingViews.User.In.Get.class,
        DataMappingViews.User.Out.Update.class,
        DataMappingViews.User.Out.UpdateOwn.class,
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class
    })
    private String name;
    
    @JsonView ({
        DataMappingViews.User.Out.Register.class,
        DataMappingViews.User.In.Register.class,
        DataMappingViews.User.In.Get.class,
        DataMappingViews.User.Out.Update.class,
        DataMappingViews.User.Out.UpdateOwn.class
    })
    private Character gender;
    
    @JsonView ({
        DataMappingViews.User.Out.UpdateOwn.class
    })
    private String password;
    
    @JsonView ({
        DataMappingViews.User.Out.Register.class,
        DataMappingViews.User.In.Register.class,
        DataMappingViews.User.In.Get.class,
        DataMappingViews.User.Out.Update.class,
        DataMappingViews.User.Out.UpdateOwn.class
    })
    private String email;
    
    @JsonView ({
        DataMappingViews.User.In.Register.class,
        DataMappingViews.User.In.Get.class,
        DataMappingViews.User.Out.UpdateRole.class
    })
    private Character role;
    
    @JsonView ({
       DataMappingViews.User.Out.Register.class,
       DataMappingViews.User.In.Register.class,
       DataMappingViews.User.In.Get.class,
       DataMappingViews.User.Out.Update.class,
       DataMappingViews.User.Out.UpdateOwn.class
    })
    private String education;
    
    @JsonView ({
        DataMappingViews.User.In.Get.class
    })
    private Blocked block;
    
    private BigDecimal averagerating;
    private BufferedImage avatar;
    
    
    public User() {
        
    }

    public User(String username, String name, Character gender, String password, String email, String education) {
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.education = education;
        setPassword(password);
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
        this.password = PasswordHashGenerator.generate(password);
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

    public BufferedImage getAvatar() {
        return avatar;
    }

    public void setAvatar(BufferedImage avatar) {
        this.avatar = avatar;
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

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Blocked getBlock() {
        return block;
    }

    public void setBlock(Blocked block) {
        this.block = block;
    }  
    
    public boolean isCurrentUser() {
        return ApplicationManager.getInstance().isCurrentUser(username);
    }

    @Override
    public String toString() {
        return "User {" + "username=" + username + ", name=" + name + ", gender=" + gender + ", email=" + email + ", role=" + role + ", education=" + education + ", averagerating=" + averagerating + '}';
    }
}
