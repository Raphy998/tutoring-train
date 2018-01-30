package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

/**
 * Created by Moe on 12.01.2018.
 */

public class Entry {

    @JsonView({Views.Entry.In.loadNewest.class})
    private int id;
    @JsonView({Views.Entry.In.loadNewest.class})
    private Date postedOn;
    @JsonView({Views.Entry.In.loadNewest.class})
    private boolean isActive;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String description;
    @JsonView({Views.Entry.In.loadNewest.class})
    //@JsonIgnoreProperties
    private Subject subject;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String headline;
    @JsonView({Views.Entry.In.loadNewest.class})
    private User user;

    public Entry(){

    }

    public Entry(int id, Date postedOn, boolean isActive, String description, Subject subject, String headline, User user) {
        this.id = id;
        this.postedOn = postedOn;
        this.isActive = isActive;
        this.description = description;
        this.subject = subject;
        this.headline = headline;
        this.user = user;
    }

    @Override
    public String toString() {
        return id + " - " + postedOn + " - " + isActive + " - " + description + " - " + subject + " - " + headline + " - " + user;
    }

    public int getId() {
        return id;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getHeadline() {
        return headline;
    }

    public User getUser() {
        return user;
    }
}
