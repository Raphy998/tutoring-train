package at.bsd.tutoringtrain.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Offer implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private Date postedon;
    private Date duedate;
    private Character isactive;
    private String description;
    private Subject subject;
    private User user;

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
