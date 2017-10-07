/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import com.fasterxml.jackson.annotation.JsonView;
import edu.tutoringtrain.utils.Views;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "TUSER", catalog = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u ORDER BY u.username ASC")
    , @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
    , @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password")
    , @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role = :role")
    , @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
    , @NamedQuery(name = "User.findByName", query = "SELECT u FROM User u WHERE u.name = :name")
    , @NamedQuery(name = "User.findByAveragerating", query = "SELECT u FROM User u WHERE u.averagerating = :averagerating")
    , @NamedQuery(name = "User.findByEducation", query = "SELECT u FROM User u WHERE u.education = :education")
    , @NamedQuery(name = "User.findByUsernameAndPassword", query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password")})
public class User implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Session> sessionCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "USERNAME", nullable = false, length = 20)
    @JsonView({Views.Offer.Out.Public.class, Views.User.In.Register.class, Views.User.Out.Public.class})
    private String username;
    @Size(max = 32)
    @Column(name = "PASSWORD", length = 32)
    @JsonView({Views.User.In.Register.class})
    private String password;
    @Column(name = "ROLE")
    @JsonView({Views.User.Out.Public.class})
    private Character role;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 30)
    @Column(name = "EMAIL", length = 30)
    @JsonView({Views.User.In.Register.class, Views.User.Out.Private.class})
    private String email;
    @Size(max = 30)
    @Column(name = "NAME", length = 30)
    @JsonView({Views.User.In.Register.class, Views.User.Out.Public.class})
    private String name;
    @Lob
    @Column(name = "AVATAR")
    private Serializable avatar;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "AVERAGERATING", precision = 2, scale = 1)
    @JsonView({Views.User.Out.Public.class})
    private BigDecimal averagerating;
    @Size(max = 20)
    @Column(name = "EDUCATION", length = 20)
    @JsonView({Views.User.In.Register.class, Views.User.Out.Public.class})
    private String education;
    @JoinColumn(name = "GENDER", referencedColumnName = "ID")
    @ManyToOne
    @JsonView({Views.User.In.Register.class, Views.User.Out.Public.class})
    private Gender gender;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    @JsonView({Views.User.Out.Private.class})
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
        this.password = password;
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
        return "edu.tutoringtrain.entities.User[ username=" + username + " ]";
    }

    @XmlTransient
    public Collection<Session> getSessionCollection() {
        return sessionCollection;
    }

    public void setSessionCollection(Collection<Session> sessionCollection) {
        this.sessionCollection = sessionCollection;
    }
    
}
