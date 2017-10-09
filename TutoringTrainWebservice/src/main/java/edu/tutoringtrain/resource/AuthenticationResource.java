/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.data.Credentials;
import edu.tutoringtrain.data.CustomHttpStatusCodes;
import edu.tutoringtrain.data.dao.AuthenticationService;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.utils.Views;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Path("/authentication")
@RequestScoped
public class AuthenticationResource extends AbstractResource {
    
    @Inject
    AuthenticationService authService;
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials creds) throws Exception {
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            // Authenticate the user using the credentials provided
            authService.authenticate(creds.getUsername(), creds.getPassword(), creds.getRequiredRole());

            // Issue a token for the user
            String token = authService.issueToken(creds.getUsername());

            // Return the token on the response
            response.entity(token);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response);
            }
            catch (Exception e) {
                unknownError(e, response);
            } 
        }
        
        return response.build();
    }
}
