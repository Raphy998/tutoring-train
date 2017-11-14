/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.misc.NumericBooleanDeserializer;
import edu.tutoringtrain.misc.NumericBooleanSerializer;
import edu.tutoringtrain.utils.Views;
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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "ENTRY", catalog = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Entry.findAll", query = "SELECT e FROM Entry e")
    , @NamedQuery(name = "Entry.findById", query = "SELECT e FROM Entry e WHERE e.id = :id")
    , @NamedQuery(name = "Entry.findByPostedon", query = "SELECT e FROM Entry e WHERE e.postedon = :postedon")
    , @NamedQuery(name = "Entry.findByDuedate", query = "SELECT e FROM Entry e WHERE e.duedate = :duedate")
    , @NamedQuery(name = "Entry.findByIsactive", query = "SELECT e FROM Entry e WHERE e.isactive = :isactive")
    , @NamedQuery(name = "Entry.findByDescription", query = "SELECT e FROM Entry e WHERE e.description = :description")
    , @NamedQuery(name = "Entry.findByFlag", query = "SELECT e FROM Entry e WHERE e.flag = :flag")
    , @NamedQuery(name = "Entry.findOfferByIdAndUsername", query = "SELECT e FROM Entry e WHERE e.flag = 'O' AND e.id = :id AND e.user.username = :username")
    , @NamedQuery(name = "Entry.findOfferNewest", query = "SELECT e FROM Entry e WHERE e.flag = 'O' ORDER BY e.postedon DESC")
    , @NamedQuery(name = "Entry.findOfferNewestOfUser", query = "SELECT e FROM Entry e WHERE e.flag = 'O' AND e.user.username = :username ORDER BY e.postedon DESC")
    , @NamedQuery(name = "Entry.countOfferAll", query = "SELECT count(e) FROM Entry e WHERE e.flag = 'O'")})
public class Entry implements Serializable {
    public static final Character FLAG_OFFER = 'O';
    public static final Character FLAG_REQUEST = 'R';

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(generator="seq_entry")
    @SequenceGenerator(name="seq_entry",sequenceName="seq_entry", allocationSize=1)
    @NotNull(groups = ConstraintGroups.Update.class)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, precision = 0, scale = -127)
    @JsonView({Views.Offer.In.Update.class, Views.Offer.Out.Public.class})
    private BigDecimal id;
    @Column(name = "POSTEDON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView(Views.Offer.Out.Public.class)
    private Date postedon;
    @Column(name = "DUEDATE")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Create.class})
    private Date duedate;
    @Column(name = "ISACTIVE")
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Update.class})
    @JsonSerialize(using=NumericBooleanSerializer.class)
    @JsonDeserialize(using=NumericBooleanDeserializer.class)
    private Character isactive;
    @Size(max = 500)
    @NotNull(groups = ConstraintGroups.Create.class)
    @Column(name = "DESCRIPTION", length = 500)
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Create.class})
    private String description;
    @Column(name = "FLAG")
    private Character flag;
    @NotNull(groups = ConstraintGroups.Create.class)
    @JoinColumn(name = "SUBJECT", referencedColumnName = "ID")
    @ManyToOne
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Create.class})
    private Subject subject;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne
    @XmlTransient
    @JsonView(Views.Offer.Out.Public.class)
    private User user;
    @Size(max = 50)
    //@NotNull(groups = ConstraintGroups.Create.class)
    @Column(name = "HEADLINE", length = 50)
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Create.class})
    private String headline;

    public Entry() {
    }

    public Entry(BigDecimal id) {
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

    public Character getFlag() {
        return flag;
    }

    public void setFlag(Character flag) {
        this.flag = flag;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Entry)) {
            return false;
        }
        Entry other = (Entry) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Entry[ id=" + id + " ]";
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
    
}
