package at.tutoringtrain.adminclient.data;

import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Offer {
    @JsonView({
    
    })
    private BigDecimal id;
    @JsonView({
    
    })
    private Date postedon;
    @JsonView({
    
    })
    private Date duedate;
    @JsonView({
    
    })
    private Character isactive;
    @JsonView({
    
    })
    private String description;
    @JsonView({
    
    })
    private Subject subject;
    @JsonView({
    
    })
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
}
