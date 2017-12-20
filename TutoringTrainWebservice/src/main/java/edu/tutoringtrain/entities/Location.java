/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.entities;
 
import edu.tutoringtrain.utils.PointConverter;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import oracle.spatial.geometry.JGeometry;
import org.eclipse.persistence.annotations.StructConverter;

/**
 *
 * @author Elias
 */
@Entity
@Table(name = "LOCATION", catalog = "", schema = "D5B15")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l")
    , @NamedQuery(name = "Location.findByLid", query = "SELECT l FROM Location l WHERE l.lid = :lid")
    , @NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE l.name = :name")})
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "LID", nullable = false, precision = 38, scale = 0)
    private BigDecimal lid;
    @Size(max = 40)
    @Column(name = "NAME", length = 40)
    private String name;
    @Lob
    @Column(name = "POINT")
    @StructConverter(name="JGeometry",
        converter="org.eclipse.persistence.platform.database.oracle.converters.JGeometryConverter")
    private JGeometry point;

    public Location() {
    }

    public Location(BigDecimal lid) {
        this.lid = lid;
    }

    public BigDecimal getLid() {
        return lid;
    }

    public void setLid(BigDecimal lid) {
        this.lid = lid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JGeometry getPoint() {
        return point;
    }

    public void setPoint(JGeometry point) {
        this.point = point;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lid != null ? lid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        if ((this.lid == null && other.lid != null) || (this.lid != null && !this.lid.equals(other.lid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.tutoringtrain.entities.Location[ lid=" + lid + " ]";
    }
    
}
