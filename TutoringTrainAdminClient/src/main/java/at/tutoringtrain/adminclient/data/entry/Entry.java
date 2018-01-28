package at.tutoringtrain.adminclient.data.entry;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.subject.Subject;
import at.tutoringtrain.adminclient.data.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public abstract class Entry {
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class,
        DataMappingViews.Entry.Out.Update.class
    })
    private BigDecimal id;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class,
        DataMappingViews.Entry.Out.Create.class,
        DataMappingViews.Entry.Out.Update.class
    })
    private String headline;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class,
        DataMappingViews.Entry.Out.Create.class,
        DataMappingViews.Entry.Out.Update.class
    })
    private String description;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class
    })
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date postedon;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class
    })
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date duedate;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class,
        DataMappingViews.Entry.Out.Update.class
    })
    private boolean isactive;
       
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class,
        DataMappingViews.Entry.Out.Create.class,
        DataMappingViews.Entry.Out.Update.class
    })
    private Subject subject;
    
    @JsonView({
        DataMappingViews.Entry.In.Get.class,
        DataMappingViews.Entry.In.Create.class
    })
    private User user;
    
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

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPostedon() {
        return postedon;
    }

    public void setPostedon(Date postedon) {
        this.postedon = postedon;
    }

    public boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
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

    public Date getDuedate() {
        return duedate;
    }

    public void setDuedate(Date duedate) {
        this.duedate = duedate;
    }

    @Override
    public String toString() {
        return "Entry {" + "id=" + id + ", headline=" + headline + ", description=" + description + ", postedon=" + postedon + ", isactive=" + isactive + ", subject=" + subject + ", user=" + user + '}';
    }    
}
