package at.bsd.tutoringtrain.data.entities;

import at.bsd.tutoringtrain.security.PasswordHash;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private Character role;
    private String email;
    private String name;
    private Serializable avatar;
    private BigDecimal averagerating;
    private String education;
    private Gender gender;
    private Blocked block;

    public User() {
    }

    public Blocked getBlock() {
        return block;
    }

    public void setBlock(Blocked block) {
        this.block = block;
    }

    
    
    public User(String username) {
        this.username = username;
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
        this.password = PasswordHash.generate(password);
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

    public Serializable getAvatar() {
        return avatar;
    }

    public void setAvatar(Serializable avatar) {
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return username + " - " + name + " (" + email + ")";
    }
    
    
}
