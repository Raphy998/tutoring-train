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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "RATING", catalog = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rating.findAll", query = "SELECT r FROM Rating r")
    , @NamedQuery(name = "Rating.findByText", query = "SELECT r FROM Rating r WHERE r.text = :text")
    , @NamedQuery(name = "Rating.findByRateduser", query = "SELECT r FROM Rating r WHERE r.ratingPK.rateduser = :rateduser")
    , @NamedQuery(name = "Rating.findByRatinguser", query = "SELECT r FROM Rating r WHERE r.ratingPK.ratinguser = :ratinguser")
    , @NamedQuery(name = "Rating.findByStars", query = "SELECT r FROM Rating r WHERE r.stars = :stars")
    , @NamedQuery(name = "Rating.findByWrittenon", query = "SELECT r FROM Rating r WHERE r.writtenon = :writtenon")})
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RatingPK ratingPK;
    @Size(max = 500)
    @Column(name = "TEXT", length = 500)
    private String text;
    @Column(name = "STARS")
    private Short stars;
    @Column(name = "WRITTENON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date writtenon;
    @JoinColumn(name = "RATEDUSER", referencedColumnName = "USERNAME", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "RATINGUSER", referencedColumnName = "USERNAME", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user1;

    public Rating() {
    }

    public Rating(RatingPK ratingPK) {
        this.ratingPK = ratingPK;
    }

    public Rating(String rateduser, String ratinguser) {
        this.ratingPK = new RatingPK(rateduser, ratinguser);
    }

    public RatingPK getRatingPK() {
        return ratingPK;
    }

    public void setRatingPK(RatingPK ratingPK) {
        this.ratingPK = ratingPK;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Short getStars() {
        return stars;
    }

    public void setStars(Short stars) {
        this.stars = stars;
    }

    public Date getWrittenon() {
        return writtenon;
    }

    public void setWrittenon(Date writtenon) {
        this.writtenon = writtenon;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ratingPK != null ? ratingPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Rating)) {
            return false;
        }
        Rating other = (Rating) object;
        if ((this.ratingPK == null && other.ratingPK != null) || (this.ratingPK != null && !this.ratingPK.equals(other.ratingPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Rating[ ratingPK=" + ratingPK + " ]";
    }
    
}
