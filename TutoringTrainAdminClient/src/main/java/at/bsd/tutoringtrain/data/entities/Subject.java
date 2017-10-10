package at.bsd.tutoringtrain.data.entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class Subject implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal id;
    private String name;
   
    public Subject() {
    }

    public Subject(BigDecimal id) {
        this.id = id;
    }
    
    public Subject(String name) {
        this.name = name;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
