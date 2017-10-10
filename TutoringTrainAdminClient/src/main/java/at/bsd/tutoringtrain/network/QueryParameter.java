/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.bsd.tutoringtrain.network;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class QueryParameter {
    private final String key;
    private final String value;
    
    public QueryParameter(String key, String value) {
        if (key == null) {
            this.key = "";
        } else {
            this.key = key;
        }
        if (value == null) {
            this.value = "";
        } else {
            this.value = value;
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
