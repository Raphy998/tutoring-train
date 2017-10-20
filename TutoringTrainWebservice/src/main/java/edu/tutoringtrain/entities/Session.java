/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "TSESSION", catalog = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s")
    , @NamedQuery(name = "Session.findByAuthkey", query = "SELECT s FROM Session s WHERE s.authkey = :authkey")
    , @NamedQuery(name = "Session.findByExpirydate", query = "SELECT s FROM Session s WHERE s.expirydate = :expirydate")})
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 32)
    @Column(name = "AUTHKEY", nullable = false, length = 32)
    private String authkey;
    @Column(name = "EXPIRYDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirydate;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne
    private User user;

    public Session() {
    }

    public Session(String authkey) {
        this.authkey = authkey;
    }

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public Date getExpirydate() {
        return expirydate;
    }

    public void setExpirydate(Date expirydate) {
        this.expirydate = expirydate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authkey != null ? authkey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.authkey == null && other.authkey != null) || (this.authkey != null && !this.authkey.equals(other.authkey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Session[ authkey=" + authkey + " ]";
    }
    
}
