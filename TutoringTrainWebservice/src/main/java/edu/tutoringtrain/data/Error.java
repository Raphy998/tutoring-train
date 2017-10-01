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
public class Error implements Serializable {
    public static final int UNKNOWN = 1;
    public static final int DUPLICATE_USERNAME = 2;
    public static final int DUPLICATE_SUBJECT_NAME = 3;
    public static final int MISSING_QUERY_PARAMS = 4;
    
    private int code;
    private String message;

    public Error() {
    }
    
    public Error(int code) {
        this.code = code;
    }

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" + "code=" + code + ", message=" + message + '}';
    }

}
