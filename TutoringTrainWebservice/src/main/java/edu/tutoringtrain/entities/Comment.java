/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import com.fasterxml.jackson.annotation.JsonView;
import edu.tutoringtrain.data.error.ConstraintGroups;
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

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "TCOMMENT", catalog = "", schema = "D5B15")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c")
    , @NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id")
    , @NamedQuery(name = "Comment.findByPostedon", query = "SELECT c FROM Comment c WHERE c.postedon = :postedon")
    , @NamedQuery(name = "Comment.findByText", query = "SELECT c FROM Comment c WHERE c.text = :text")
    , @NamedQuery(name = "Comment.findByEntryId", query = "SELECT c FROM Comment c WHERE c.entry.id = :entryId ORDER BY c.postedon")})
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @GeneratedValue(generator="seq_comment")
    @SequenceGenerator(name="seq_comment", sequenceName="seq_comment", allocationSize=1)
    @Basic(optional = false)
    @NotNull(groups = ConstraintGroups.Update.class)
    @Column(name = "ID", nullable = false, precision = 0, scale = -127)
    @JsonView({Views.Comment.Out.Public.class, Views.Comment.In.Update.class})
    private BigDecimal id;
    @Column(name = "POSTEDON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonView({Views.Comment.Out.Public.class})
    private Date postedon;
    @Size(max = 300)
    @NotNull(groups = {ConstraintGroups.Create.class, ConstraintGroups.Update.class})
    @Column(name = "TEXT", length = 300)
    @JsonView({Views.Comment.Out.Public.class, Views.Comment.In.Create.class})
    private String text;
    @JoinColumn(name = "ENTRYID", referencedColumnName = "ID")
    @ManyToOne
    private Entry entry;
    @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME")
    @ManyToOne
    @JsonView({Views.Comment.Out.Public.class})
    private User user;

    public Comment() {
    }

    public Comment(BigDecimal id) {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entryid) {
        this.entry = entryid;
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Tcomment[ id=" + id + " ]";
    }
    
}
