/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.data.Credentials;
import edu.tutoringtrain.data.dao.AuthenticationService;
import javax.ws.rs.Consumes;
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
public class AuthenticationResource {
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateUser(Credentials creds) {
        try {
            AuthenticationService authService = AuthenticationService.getInstance();
            // Authenticate the user using the credentials provided
            authService.authenticate(creds.getUsername(), creds.getPassword());

            // Issue a token for the user
            String token = authService.issueToken(creds.getUsername());

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }      
    }
}
