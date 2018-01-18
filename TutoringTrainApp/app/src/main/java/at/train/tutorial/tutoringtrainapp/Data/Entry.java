package at.train.tutorial.tutoringtrainapp.Data;

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
    private Subject subject;
    @JsonView({Views.Entry.In.loadNewest.class})
    private String headline;
    @JsonView({Views.Entry.In.loadNewest.class})
    private User user;

    public Entry(){

    }

    @Override
    public String toString() {
        return id + " - " + postedOn + " - " + isActive + " - " + description + " - " + subject + " - " + headline + " - " + user;
    }
}
