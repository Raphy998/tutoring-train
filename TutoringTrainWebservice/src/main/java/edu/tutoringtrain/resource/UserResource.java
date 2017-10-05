/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.dao.UserService;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.exceptions.BlockException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.Gender;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.Views;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Path("/user")
public class UserResource extends AbstractResource {
    
    @POST
    @Path("/register")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response register(@Context HttpServletRequest httpServletRequest,
                    final String userStr) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        User userIn = null;
        try {
            userIn = getMapper().readerWithView(Views.User.In.Register.class).withType(User.class).readValue(userStr);
            User userOut = UserService.getInstance().registerUser(userIn);
            
            response.entity(getMapper().writerWithView(Views.User.Out.Private.class).writeValueAsString(userOut));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response);
            }
            catch (RollbackException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity("username '" + userIn.getUsername() + "' not available");
            }
            catch (Exception e) {
                unknownError(e, response);
            } 
        }
 
        return response.build();
    }
    
    @Secured
    @PUT
    @Path("/update/own")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateOwn(@Context HttpServletRequest httpServletRequest,
                    final String userStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            User userIn = getMapper().readerWithView(Views.User.In.Update.class).withType(User.class).readValue(userStr);
            userIn.setUsername(securityContext.getUserPrincipal().getName());       //set user to logged in user (to avoid making another json view)
            
            UserService.getInstance().updateUser(userIn);
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
    
    @Secured(Role.ADMIN)
    @PUT
    @Path("/update")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateAny(@Context HttpServletRequest httpServletRequest,
                    final String userStr) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            User userIn = getMapper().readerWithView(Views.User.In.Update.class).withType(User.class).readValue(userStr);
            UserService.getInstance().updateUser(userIn);
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
    
    @Secured
    @GET
    @Path("/gender")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllGenders(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            List<Gender> genders = UserService.getInstance().getAllGenders();
            response.entity(getMapper().writerWithView(Views.User.Out.Public.class).writeValueAsString(genders.toArray()));
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
    
    @Secured
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getOwnUser(@Context HttpServletRequest httpServletRequest,
                    @Context SecurityContext securityContext) throws Exception {
        
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(getMapper().writerWithView(Views.User.Out.Private.class)
                    .writeValueAsString(UserService.getInstance().getUserByUsername(username)));
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
    
    @Secured(Role.ADMIN)
    @GET
    @Path("/all")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUsers(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "start") Integer start,
                    @QueryParam(value = "pageSize") Integer pageSize) throws Exception {

        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            List<User> userEntities;
            if (start != null && pageSize != null) {
                userEntities = UserService.getInstance().getUsers(start, pageSize);
            }
            else {
                userEntities = UserService.getInstance().getUsers();
            }

            response.entity(getMapper().writerWithView(Views.User.Out.Private.class)
                    .writeValueAsString(userEntities.toArray()));
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
    
    @Secured(Role.ADMIN)
    @POST
    @Path("/block")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response blockUser(@Context HttpServletRequest httpServletRequest,
                    final String blockStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Blocked blockIn = getMapper().readerWithView(Views.Block.In.Create.class).withType(Blocked.class).readValue(blockStr);
            
            if (securityContext.getUserPrincipal().getName().equals(blockIn.getUsername())) {
                throw new BlockException("cannot block own user");
            }
            
            User user2Block = UserService.getInstance().getUserByUsername(blockIn.getUsername());
            if (user2Block == null) {
                throw new UserNotFoundException("cannot find user");
            }
            else if (user2Block.getRole().equals('A')) {
                throw new BlockException("cannot block other admins");
            }
            
            UserService.getInstance().blockUser(blockIn, true);
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
    
    @Secured(Role.ADMIN)
    @GET
    @Path("/unblock/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response unblockUser(@Context HttpServletRequest httpServletRequest,
                    @PathParam("username") String user2unblock,
                    @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (securityContext.getUserPrincipal().getName().equals(user2unblock)) {
                throw new BlockException("cannot unblock own user");
            }
            
            User user = UserService.getInstance().getUserByUsername(user2unblock);
            if (user == null) {
                throw new UserNotFoundException("cannot find user");
            }
            
            UserService.getInstance().blockUser(new Blocked(user2unblock), false);
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
    
    //TODO: Unused in Sprint 1
    /*
    @GET
    @Path("/verify")
    @Produces(value = { MediaType.APPLICATION_JSON})
    public Response verifyAccount(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "key") String key) throws Exception {

        String msg = null;
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            if (UserService.getInstance().verifyUserAccount(key)) {
                msg = "Verified";
            }
            else {
                response.status(Response.Status.BAD_REQUEST);
                msg = "Error";
            }
        }
        catch (Exception e) {
           msg = "Ex";
        }
        finally {
            response.entity(msg);
        }

        return response.build();
    }
    */
    
}
