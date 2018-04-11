/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Elias
 */
@Entity
public class NewsletterRequest implements Serializable {
    
    public static final String DEFAULT_URL_IMG_TOP = "http://tutoringtrain.wilscher.eu/tt_logo.png";
    public static final String DEFAULT_URL_IMG_BOTTOM = "http://tutoringtrain.wilscher.eu/logo.png";
    
    @NotNull
    private String subject;
    @Id
    @NotNull
    private String title;
    @NotNull
    private String text_top;
    @NotNull
    private String text_bottom;
    
    private String url_img_top;
    private String url_img_bottom;
    
    private Character[] targets;

    public NewsletterRequest() {
    }

    public NewsletterRequest(String subject, String title, String text_top, String text_bottom, String url_img_top, String url_img_bottom, Character[] targets) {
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

    public Character[] getTargets() {
        return targets;
    }

    public void setTargets(Character[] targets) {
        this.targets = targets;
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

    @Override
    public String toString() {
        return "NewsletterRequest{" + "subject=" + subject + ", title=" + title + ", text_top=" + text_top + ", text_bottom=" + text_bottom + ", url_img_top=" + url_img_top + ", url_img_bottom=" + url_img_bottom + ", targets=" + targets + '}';
    }
}
