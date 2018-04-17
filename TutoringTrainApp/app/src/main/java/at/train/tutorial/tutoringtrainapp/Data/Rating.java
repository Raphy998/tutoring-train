package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

public class Rating {
    @JsonIgnoreProperties
    @JsonView({Views.Rating.Out.create.class,Views.Rating.In.load.class})
    private String text;
    @JsonView({Views.Rating.Out.create.class,Views.Rating.In.load.class})
    private int stars;
    @JsonView({Views.Rating.In.load.class})
    private Date writtenon;
    @JsonView({Views.Rating.In.load.class})
    private User ratedUser;
    @JsonView({Views.Rating.In.load.class})
    private User ratingUser;

    public Rating(){

    }

    public Rating(String text,int stars){
        this.stars = stars;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public Date getWrittenon() {
        return writtenon;
    }

    public void setWrittenon(Date writtenon) {
        this.writtenon = writtenon;
    }

    public User getRatedUser() {
        return ratedUser;
    }

    public void setRatedUser(User ratedUser) {
        this.ratedUser = ratedUser;
    }

    public User getRatingUser() {
        return ratingUser;
    }

    public void setRatingUser(User ratingUser) {
        this.ratingUser = ratingUser;
    }
}
