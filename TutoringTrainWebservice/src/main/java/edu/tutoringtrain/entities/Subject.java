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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "SUBJECT", catalog = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"DENAME"})
    , @UniqueConstraint(columnNames = {"ENNAME"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Subject.findAll", query = "SELECT s FROM Subject s WHERE s.isactive = '1' ORDER BY s.enname ASC")
    , @NamedQuery(name = "Subject.findById", query = "SELECT s FROM Subject s WHERE s.id = :id")
    , @NamedQuery(name = "Subject.findByDename", query = "SELECT s FROM Subject s WHERE s.dename = :dename")
    , @NamedQuery(name = "Subject.findByEnname", query = "SELECT s FROM Subject s WHERE s.enname = :enname")
    , @NamedQuery(name = "Subject.findByIsactive", query = "SELECT s FROM Subject s WHERE s.isactive = :isactive")
    , @NamedQuery(name = "Subject.countAll", query = "SELECT count(s) FROM Subject s")})
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(generator="seq_subject")
    @SequenceGenerator(name="seq_subject",sequenceName="seq_subject", allocationSize=1)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, precision = 0, scale = -127)
    @JsonView({Views.Offer.Out.Public.class, Views.Offer.In.Create.class, Views.Subject.Out.Public.class, 
        Views.Subject.In.Update.class})
    private BigDecimal id;
    @Size(max = 25)
    @Column(name = "DENAME", length = 25)
    @JsonView({Views.Offer.Out.Public.class, Views.Subject.Out.Public.class, Views.Subject.In.Create.class})
    private String dename;
    @Size(max = 25)
    @Column(name = "ENNAME", length = 25)
    @JsonView({Views.Offer.Out.Public.class, Views.Subject.Out.Public.class, Views.Subject.In.Create.class})
    private String enname;
    @Column(name = "ISACTIVE")
    private Character isactive;
    @OneToMany(mappedBy = "subject")
    private Collection<Entry> entryCollection;

    public Subject() {
    }

    public Subject(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getDename() {
        return dename;
    }

    public void setDename(String dename) {
        this.dename = dename;
    }

    public String getEnname() {
        return enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public Character getIsactive() {
        return isactive;
    }

    public void setIsactive(Character isactive) {
        this.isactive = isactive;
    }

    @XmlTransient
    public Collection<Entry> getEntryCollection() {
        return entryCollection;
    }

    public void setEntryCollection(Collection<Entry> entryCollection) {
        this.entryCollection = entryCollection;
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
        if (!(object instanceof Subject)) {
            return false;
        }
        Subject other = (Subject) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Subject[ id=" + id + " ]";
    }
    
}
