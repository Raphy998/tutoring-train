/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.utils.Views;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "TUSER", catalog = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"EMAIL"})})
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
    , @NamedQuery(name = "User.findByGender", query = "SELECT u FROM User u WHERE u.gender = :gender")
    , @NamedQuery(name = "User.findByUsernameAndPassword", query = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    , @NamedQuery(name = "User.countAll", query = "SELECT count(u) FROM User u")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "USERNAME", nullable = false, length = 20)
    @JsonView({Views.Offer.Out.Public.class, Views.User.In.Register.class, Views.User.Out.Public.class})
    private String username;
    @Size(max = 64)
    @NotNull(groups = ConstraintGroups.Create.class)
    @Column(name = "PASSWORD", length = 64)
    @JsonView({Views.User.In.Register.class})
    private String password;
    @Column(name = "ROLE")
    @JsonView({Views.User.Out.Public.class})
    private Character role;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 50)
    @Column(name = "EMAIL", length = 50)
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
    @Column(name = "GENDER")
    @JsonView({Views.User.In.Register.class, Views.User.Out.Public.class})
    private Character gender;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Rating> ratingCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user1")
    private Collection<Rating> ratingCollection1;
    @OneToMany(mappedBy = "user")
    private Collection<Session> sessionCollection;
    @OneToMany(mappedBy = "user")
    private Collection<Entry> entryCollection;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonView({Views.User.Out.Private.class})
    @JsonProperty("block")
    private Blocked blocked;

    public User() {
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

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    @XmlTransient
    public Collection<Rating> getRatingCollection() {
        return ratingCollection;
    }

    public void setRatingCollection(Collection<Rating> ratingCollection) {
        this.ratingCollection = ratingCollection;
    }

    @XmlTransient
    public Collection<Rating> getRatingCollection1() {
        return ratingCollection1;
    }

    public void setRatingCollection1(Collection<Rating> ratingCollection1) {
        this.ratingCollection1 = ratingCollection1;
    }

    @XmlTransient
    public Collection<Session> getSessionCollection() {
        return sessionCollection;
    }

    public void setSessionCollection(Collection<Session> sessionCollection) {
        this.sessionCollection = sessionCollection;
    }

    @XmlTransient
    public Collection<Entry> getEntryCollection() {
        return entryCollection;
    }

    public void setEntryCollection(Collection<Entry> entryCollection) {
        this.entryCollection = entryCollection;
    }

    public Blocked getBlock() {
        return blocked;
    }

    public void setBlock(Blocked blocked) {
        this.blocked = blocked;
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
    
}
