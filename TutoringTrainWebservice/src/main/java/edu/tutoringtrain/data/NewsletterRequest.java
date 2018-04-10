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
    @NotNull
    private String subject;
    @Id
    @NotNull
    private String title;
    @NotNull
    private String text_top;
    @NotNull
    private String text_bottom;
    private Character[] targets;

    public NewsletterRequest() {
    }

    public NewsletterRequest(String subject, String title, String text_top, String text_bottom, Character[] targets) {
        this.subject = subject;
        this.title = title;
        this.text_top = text_top;
        this.text_bottom = text_bottom;
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

    @Override
    public String toString() {
        return "NewsletterRequest{" + "subject=" + subject + ", title=" + title + ", text_top=" + text_top + ", text_bottom=" + text_bottom + ", targets=" + targets + '}';
    }
}
