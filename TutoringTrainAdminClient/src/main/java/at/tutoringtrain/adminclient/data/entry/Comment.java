package at.tutoringtrain.adminclient.data.entry;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import at.tutoringtrain.adminclient.data.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Comment {
    @JsonView({
        DataMappingViews.Entry.Comment.In.Get.class
    })
    private BigDecimal id;
    
    @JsonView({
        DataMappingViews.Entry.Comment.In.Get.class
    })
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date postedon;
    
    @JsonView({
        DataMappingViews.Entry.Comment.In.Get.class
    })
    private String text;
    
    @JsonView({
        DataMappingViews.Entry.Comment.In.Get.class
    })
    private User user;

    public Comment() {
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
