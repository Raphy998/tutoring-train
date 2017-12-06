package at.tutoringtrain.adminclient.data;

import at.tutoringtrain.adminclient.datamapper.JsonUserViews;
import at.tutoringtrain.adminclient.main.ApplicationManager;
import at.tutoringtrain.adminclient.security.PasswordHashGenerator;
import com.fasterxml.jackson.annotation.JsonView;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public final class User extends Entity implements Cloneable {
    private static final long serialVersionUID = 1830655504546896238L;
    
    @JsonView ({
        JsonUserViews.Out.Register.class, 
        JsonUserViews.In.Register.class, 
        JsonUserViews.In.Get.class, 
        JsonUserViews.Out.Update.class,
        JsonUserViews.Out.UpdatePassowrd.class
    })
    private String username;
    
    @JsonView ({
        JsonUserViews.Out.Register.class,
        JsonUserViews.In.Register.class,
        JsonUserViews.In.Get.class,
        JsonUserViews.Out.Update.class,
        JsonUserViews.Out.UpdateOwn.class
    })
    private String name;
    
    @JsonView ({
        JsonUserViews.Out.Register.class,
        JsonUserViews.In.Register.class,
        JsonUserViews.In.Get.class,
        JsonUserViews.Out.Update.class,
        JsonUserViews.Out.UpdateOwn.class
    })
    private Character gender;
    
    @JsonView ({
        JsonUserViews.Out.UpdateOwn.class
    })
    private String password;
    
    @JsonView ({
        JsonUserViews.Out.Register.class,
        JsonUserViews.In.Register.class,
        JsonUserViews.In.Get.class,
        JsonUserViews.Out.Update.class,
        JsonUserViews.Out.UpdateOwn.class
    })
    private String email;
    
    @JsonView ({
        JsonUserViews.In.Register.class,
        JsonUserViews.In.Get.class,
        JsonUserViews.Out.UpdateRole.class
    })
    private Character role;
    
    @JsonView ({
       JsonUserViews.Out.Register.class,
       JsonUserViews.In.Register.class,
       JsonUserViews.In.Get.class,
       JsonUserViews.Out.Update.class,
       JsonUserViews.Out.UpdateOwn.class
    })
    private String education;
    
    @JsonView ({
        JsonUserViews.In.Get.class
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

    @Override
    public String toString() {
        return super.toString() + ": " + username + ", " + name + ", " + gender + ", " + education + ", " + email;
    }    
    
    public boolean isCurrentUser() {
        return ApplicationManager.getInstance().isCurrentUser(username);
    }
}
