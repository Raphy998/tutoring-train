/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data;

import java.io.Serializable;

/**
 *
 * @author Elias
 */
public class DefaultNewsletterImages implements Serializable {

    private String default_url_img_top;
    private String default_url_img_bottom;

    public DefaultNewsletterImages() {
    }

    public DefaultNewsletterImages(String default_url_img_top, String default_url_img_bottom) {
        this.default_url_img_top = default_url_img_top;
        this.default_url_img_bottom = default_url_img_bottom;
    }

    public String getDefault_url_img_top() {
        return default_url_img_top;
    }

    public void setDefault_url_img_top(String default_url_img_top) {
        this.default_url_img_top = default_url_img_top;
    }

    public String getDefault_url_img_bottom() {
        return default_url_img_bottom;
    }

    public void setDefault_url_img_bottom(String default_url_img_bottom) {
        this.default_url_img_bottom = default_url_img_bottom;
    }

    @Override
    public String toString() {
        return "DefaultNewsletterImages{" + "default_url_img_top=" + default_url_img_top + ", default_url_img_bottom=" + default_url_img_bottom + '}';
    }

    
}
