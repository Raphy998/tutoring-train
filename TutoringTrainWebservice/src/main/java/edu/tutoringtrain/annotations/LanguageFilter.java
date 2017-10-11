/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.annotations;

import edu.tutoringtrain.data.error.Language;
import java.io.IOException;
import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Elias
 */
@Localized
@Provider
@Priority(Priorities.USER)
@RequestScoped
public class LanguageFilter implements ContainerRequestFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the ACCEPT_LANGUAGE header from the request
        String languageHeader =
                requestContext.getHeaderString(HttpHeaders.ACCEPT_LANGUAGE);
        
        Language lang;
        
        try {
            lang = Language.valueOf(languageHeader.toUpperCase());
        } 
        catch (Exception e) {
            lang = Language.getDefault();
        }
        
        requestContext.setProperty("lang", lang);
    }
}
