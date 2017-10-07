/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import com.fasterxml.jackson.annotation.JsonView;
import edu.tutoringtrain.utils.Views;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Table(name = "BLOCKED", catalog = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Blocked.findAll", query = "SELECT b FROM Blocked b")
    , @NamedQuery(name = "Blocked.findByUsername", query = "SELECT b FROM Blocked b WHERE b.username = :username")
    , @NamedQuery(name = "Blocked.findByReason", query = "SELECT b FROM Blocked b WHERE b.reason = :reason")
    , @NamedQuery(name = "Blocked.findByDuedate", query = "SELECT b FROM Blocked b WHERE b.duedate = :duedate")})
public class Blocked implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "USERNAME", nullable = false, length = 20)
    @JsonView({Views.Block.In.Create.class})
    private String username;
    @Size(max = 100)
    @Column(name = "REASON", length = 100)
    @JsonView({Views.User.Out.Private.class, Views.Block.In.Create.class})
    private String reason;
    @Column(name = "DUEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.User.Out.Private.class, Views.Block.In.Create.class})
    private Date duedate;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public Blocked() {
    }

    public Blocked(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
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
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Blocked)) {
            return false;
        }
        Blocked other = (Blocked) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Blocked[ username=" + username + " ]";
    }
    
}
