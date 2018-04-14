package at.tutoringtrain.adminclient.data.user.newsletter;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.TreeSet;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class Newsletter {
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String subject;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String title;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String text_top;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String text_bottom;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String url_img_top;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private String url_img_bottom;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.class
    })
    private TreeSet<Character> targets;

    public Newsletter() {
        this(null, null, null, null, null, null, new TreeSet<>());
    }

    public Newsletter(String subject, String title, String text_top, String text_bottom, String url_img_top, String url_img_bottom, TreeSet<Character> targets) {
        this.subject = subject;
        this.title = title;
        this.text_top = text_top;
        this.text_bottom = text_bottom;
        this.url_img_top = url_img_top;
        this.url_img_bottom = url_img_bottom;
        this.targets = targets;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText_top() {
        return text_top;
    }

    public void setText_top(String text_top) {
        this.text_top = text_top;
    }

    public String getText_bottom() {
        return text_bottom;
    }

    public void setText_bottom(String text_bottom) {
        this.text_bottom = text_bottom;
    }

    public String getUrl_img_top() {
        return url_img_top;
    }

    public void setUrl_img_top(String url_img_top) {
        this.url_img_top = url_img_top;
    }

    public String getUrl_img_bottom() {
        return url_img_bottom;
    }

    public void setUrl_img_bottom(String url_img_bottom) {
        this.url_img_bottom = url_img_bottom;
    }
    
    public TreeSet<Character> getTargets() {
        return targets;
    }

    public void setTargets(TreeSet<Character> targets) {
        this.targets = targets;
    }
}
