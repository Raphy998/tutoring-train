/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Elias
 */
@Embeddable
public class RatingPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "RATEDUSER", nullable = false, length = 20)
    private String rateduser;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "RATINGUSER", nullable = false, length = 20)
    private String ratinguser;

    public RatingPK() {
    }

    public RatingPK(String rateduser, String ratinguser) {
        this.rateduser = rateduser;
        this.ratinguser = ratinguser;
    }

    public String getRateduser() {
        return rateduser;
    }

    public void setRateduser(String rateduser) {
        this.rateduser = rateduser;
    }

    public String getRatinguser() {
        return ratinguser;
    }

    public void setRatinguser(String ratinguser) {
        this.ratinguser = ratinguser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rateduser != null ? rateduser.hashCode() : 0);
        hash += (ratinguser != null ? ratinguser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RatingPK)) {
            return false;
        }
        RatingPK other = (RatingPK) object;
        if ((this.rateduser == null && other.rateduser != null) || (this.rateduser != null && !this.rateduser.equals(other.rateduser))) {
            return false;
        }
        if ((this.ratinguser == null && other.ratinguser != null) || (this.ratinguser != null && !this.ratinguser.equals(other.ratinguser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.RatingPK[ rateduser=" + rateduser + ", ratinguser=" + ratinguser + " ]";
    }
    
}
