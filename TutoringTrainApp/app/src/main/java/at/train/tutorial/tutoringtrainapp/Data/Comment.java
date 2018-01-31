package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

/**
 * Created by Moe on 30.01.2018.
 *
 */

public class Comment {

    @JsonIgnoreProperties
    @JsonView({Views.Comment.In.loadNewest.class})
    private int id;
    @JsonView({Views.Comment.In.loadNewest.class})
    private Date postedOn;
    @JsonView({Views.Comment.In.loadNewest.class,Views.Comment.Out.create.class})
    private String text;
    @JsonView({Views.Comment.In.loadNewest.class})
    private User user;

    public Comment() {
    }

    public Comment(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                ", postedOn=" + postedOn +
                ", text='" + text + '\'' +
                ", user=" + user +
                '}';
    }

    public int getId() {
        return id;
    }

    public Date getPostedOn() {
        return postedOn;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }
}
