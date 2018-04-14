package at.tutoringtrain.adminclient.data.user.newsletter;

import at.tutoringtrain.adminclient.data.mapper.DataMappingViews;
import com.fasterxml.jackson.annotation.JsonView;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class NewsletterTemplateDefaultImages {
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.DefaultImages.class
    })
    private String default_url_img_top;
    
    @JsonView ({
        DataMappingViews.User.Out.Newsletter.DefaultImages.class
    })
    private String default_url_img_bottom;

    public NewsletterTemplateDefaultImages() {
        this(null, null);
    }

    public NewsletterTemplateDefaultImages(String default_url_img_top, String default_url_img_bottom) {
        this.default_url_img_top = default_url_img_top;
        this.default_url_img_bottom = default_url_img_bottom;
    }

    public String getDefault_url_img_bottom() {
        return default_url_img_bottom;
    }

    public void setDefault_url_img_bottom(String default_url_img_bottom) {
        this.default_url_img_bottom = default_url_img_bottom;
    }

    public String getDefault_url_img_top() {
        return default_url_img_top;
    }

    public void setDefault_url_img_top(String default_url_img_top) {
        this.default_url_img_top = default_url_img_top;
    }
}
