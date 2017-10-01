/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "GENDER", catalog = "", schema = "D5B15")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Gender.findAll", query = "SELECT g FROM Gender g ORDER BY g.name ASC")
    , @NamedQuery(name = "Gender.findById", query = "SELECT g FROM Gender g WHERE g.id = :id")
    , @NamedQuery(name = "Gender.findByName", query = "SELECT g FROM Gender g WHERE g.name = :name")})
public class Gender implements Serializable {

    @OneToMany(mappedBy = "gender")
    private Collection<User> tuserCollection;

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false, precision = 0, scale = -127)
    private BigDecimal id;
    @Size(max = 20)
    @Column(name = "NAME", length = 20)
    private String name;
    @OneToMany(mappedBy = "gender")
    private Collection<User> userCollection;

    public Gender() {
    }

    public Gender(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Collection<User> getUserCollection() {
        return userCollection;
    }

    public void setUserCollection(Collection<User> userCollection) {
        this.userCollection = userCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gender)) {
            return false;
        }
        Gender other = (Gender) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Gender[ id=" + id + " ]";
    }

    @XmlTransient
    public Collection<User> getTuserCollection() {
        return tuserCollection;
    }

    public void setTuserCollection(Collection<User> tuserCollection) {
        this.tuserCollection = tuserCollection;
    }
    
}
