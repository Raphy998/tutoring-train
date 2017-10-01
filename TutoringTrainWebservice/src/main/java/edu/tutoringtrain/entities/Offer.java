/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
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
@Table(name = "OFFER", catalog = "", schema = "D5B15")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Offer.findAll", query = "SELECT o FROM Offer o")
    , @NamedQuery(name = "Offer.findById", query = "SELECT o FROM Offer o WHERE o.id = :id")
    , @NamedQuery(name = "Offer.findByPostedon", query = "SELECT o FROM Offer o WHERE o.postedon = :postedon")
    , @NamedQuery(name = "Offer.findByDuedate", query = "SELECT o FROM Offer o WHERE o.duedate = :duedate")
    , @NamedQuery(name = "Offer.findByIsactive", query = "SELECT o FROM Offer o WHERE o.isactive = :isactive")
    , @NamedQuery(name = "Offer.findByDescription", query = "SELECT o FROM Offer o WHERE o.description = :description")
    , @NamedQuery(name = "Offer.findByIdAndUsername", query = "SELECT o FROM Offer o WHERE o.id = :id AND o.username.username = :username")
    , @NamedQuery(name = "Offer.findNewest", query = "SELECT o FROM Offer o ORDER BY o.postedon DESC")
    , @NamedQuery(name = "Offer.findNewestOfUser", query = "SELECT o FROM Offer o WHERE o.username.username = :username ORDER BY o.postedon DESC")})
public class Offer implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(generator="seq_offer")
    @SequenceGenerator(name="seq_offer",sequenceName="seq_offer", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false, precision = 0, scale = -127)
    private BigDecimal id;
    @Column(name = "POSTEDON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date postedon;
    @Column(name = "DUEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date duedate;
    @Column(name = "ISACTIVE")
    private Character isactive;
    @Size(max = 500)
    @Column(name = "DESCRIPTION", length = 500)
    private String description;
    @JoinColumn(name = "SUBJECT", referencedColumnName = "ID")
    @ManyToOne
    private Subject subject;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne
    private User username;

    public Offer() {
    }

    public Offer(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Date getPostedon() {
        return postedon;
    }

    public void setPostedon(Date postedon) {
        this.postedon = postedon;
    }

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }

    public Character getIsactive() {
        return isactive;
    }

    public void setIsactive(Character isactive) {
        this.isactive = isactive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
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
        if (!(object instanceof Offer)) {
            return false;
        }
        Offer other = (Offer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Offer[ id=" + id + " ]";
    }
    
}
