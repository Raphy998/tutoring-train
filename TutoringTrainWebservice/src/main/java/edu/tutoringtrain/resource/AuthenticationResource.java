/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.Credentials;
import edu.tutoringtrain.data.dao.AuthenticationService;
import edu.tutoringtrain.data.dao.UserService;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.ForbiddenException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Localized
@Path("/authentication")
@RequestScoped
public class AuthenticationResource extends AbstractResource {
    
    @Inject
    AuthenticationService authService;
    @Inject
    UserService userService;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(@Context HttpServletRequest httpServletRequest,
                    String creds) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            Credentials credentials = getMapper().reader().forType(Credentials.class).readValue(creds);
            // Authenticate the user using the credentials provided
            authService.authenticate(credentials.getUsername(), credentials.getPassword(), credentials.getRequiredRole());

            // Issue a token for the user
            String token = authService.issueToken(credentials.getUsername());

            // Return the token on the response
            response.entity(token);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
        
        return response.build();
    }
    
    @Secured
    @POST
    @Path("/check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkAuthKey(@Context HttpServletRequest httpServletRequest,
            String creds,
            @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        String username = securityContext.getUserPrincipal().getName();
        
        try {
            if (creds != null && !creds.isEmpty()) {
                Credentials credentials = getMapper().reader().forType(Credentials.class).readValue(creds);
                if (credentials.getRequiredRole() != null) {
                    if (!authService.canAuthenticate(credentials.getRequiredRole(),
                            userService.getUserByUsername(username).getRole())) {
                        throw new ForbiddenException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.ADMIN_ONLY));
                    }
                }
            }
            // Authenticate the user using the credentials provided
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
        
        return response.build();
    }
}
