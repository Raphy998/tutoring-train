/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Elias
 */
public class ErrorBuilder {
    private int errorCode;
    private List<Object> params;
    private Language lang;
    
    public ErrorBuilder() {
        params = new ArrayList<>();
    }
    
    public ErrorBuilder(int errorCode) {
        this();
        this.errorCode = errorCode;
    }
    
    public ErrorBuilder withErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }
    
    public ErrorBuilder withParams(Object... params) {
        this.params.addAll(Arrays.asList(params));
        return this;
    }
    
    public ErrorBuilder withLang(Language lang) {
        this.lang = lang;
        return this;
    }
    
    public Error build() {
        return new Error(errorCode, getErrorMessage());
    }
    
    private String getErrorMessage() {
        String retVal = null;
        
        try {
            retVal = String.format(ErrorMessageProvider.getInstance().get(errorCode, lang), params.toArray(new Object[0]));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return retVal;
                
    }
}
