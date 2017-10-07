/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    , @NamedQuery(name = "Session.findByUsername", query = "SELECT s FROM Session s WHERE s.sessionPK.username = :username")
    , @NamedQuery(name = "Session.findByAuthkey", query = "SELECT s FROM Session s WHERE s.sessionPK.authkey = :authkey")
    , @NamedQuery(name = "Session.findByExpirydate", query = "SELECT s FROM Session s WHERE s.expirydate = :expirydate")})
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SessionPK sessionPK;
    @Column(name = "EXPIRYDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirydate;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public Session() {
    }

    public Session(SessionPK sessionPK) {
        this.sessionPK = sessionPK;
    }

    public Session(String username, String authkey) {
        this.sessionPK = new SessionPK(username, authkey);
    }

    public SessionPK getSessionPK() {
        return sessionPK;
    }

    public void setSessionPK(SessionPK sessionPK) {
        this.sessionPK = sessionPK;
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
        hash += (sessionPK != null ? sessionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.sessionPK == null && other.sessionPK != null) || (this.sessionPK != null && !this.sessionPK.equals(other.sessionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Session[ sessionPK=" + sessionPK + " ]";
    }
    
}
