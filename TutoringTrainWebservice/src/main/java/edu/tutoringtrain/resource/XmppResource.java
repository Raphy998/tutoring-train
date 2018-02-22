/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.dao.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.tutoringtrain.data.dao.XMPPService;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.entities.User;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.SecurityContext;
/**
 * REST Web Service
 *
 * @author Elias
 */
@Localized
@Path("/xmpp")
@RequestScoped
public class XmppResource extends AbstractResource {
    @Inject
    UserService userService;
    @Inject
    XMPPService xmppService; 
    
    @GET
    @Path("/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getXMPPCredentials(@Context HttpServletRequest httpServletRequest,
                            @PathParam("username") String username) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            User u = userService.getUserByUsername(username);
            response.entity(xmppService.getCredentials(username, u.getPassword()));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.EN);
            }
            catch (Exception e) {
                unknownError(e, response, Language.EN);
            } 
        }
 
        return response.build();
    }
    
    @Secured
    @POST
    @Path("/add/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response addXMPPRosterContact(@Context HttpServletRequest httpServletRequest,
                            @PathParam("username") String username,
                            @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            User thisUser = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            User user2add = userService.getUserByUsername(username);
            xmppService.addToRoster(thisUser.getUsername(), thisUser.getPassword(), user2add.getUsername());
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.EN);
            }
            catch (Exception e) {
                unknownError(e, response, Language.EN);
            } 
        }
 
        return response.build();
    }
    
    @Secured
    @GET
    @Path("/myRoster/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response isUserOnMyRoster(@Context HttpServletRequest httpServletRequest,
                            @PathParam("username") String username,
                            @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            User thisUser = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            User user2check = userService.getUserByUsername(username);
            response.entity(
                    xmppService.getContactType(thisUser.getUsername(), thisUser.getPassword(), user2check.getUsername()));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.EN);
            }
            catch (Exception e) {
                unknownError(e, response, Language.EN);
            } 
        }
 
        return response.build();
    }
}