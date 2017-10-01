/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.BlockUserRequest;
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
import edu.tutoringtrain.data.Error;
import edu.tutoringtrain.data.RegisterUserRequest;
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.UserResponse;
import edu.tutoringtrain.entities.Gender;
import edu.tutoringtrain.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Path("/user")
public class UserResource {
    
    @POST
    @Path("/register")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response register(@Context HttpServletRequest httpServletRequest,
                    final RegisterUserRequest userToRegister) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(UserService.getInstance().registerUser(userToRegister));
        } 
        catch (RollbackException ex) {
            response.status(Response.Status.CONFLICT);
            response.entity(new Error(Error.DUPLICATE_USERNAME, "username '" + userToRegister.getUsername() + "' not available"));
        }
        catch (Exception ex) { 
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
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
            response.entity(UserService.getInstance().getAllGenders().toArray(new Gender[0]));
        } 
        catch (Exception ex) { 
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
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
            response.entity(new UserResponse(UserService.getInstance().getUserByUsername(username)));
        } 
        catch (Exception ex) { 
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
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
            List<UserResponse> users = new ArrayList<>();
            List<User> userEntities;
            if (start != null && pageSize != null) {
                userEntities = UserService.getInstance().getUsers(start, pageSize);
            }
            else {
                userEntities = UserService.getInstance().getUsers();

            }
            
            for (User u: userEntities) {
                users.add(new UserResponse(u));
            }
            response.entity(users.toArray(new UserResponse[0]));
        } 
        catch (Exception ex) { 
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
        }
 
        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @POST
    @Path("/block")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response blockUser(@Context HttpServletRequest httpServletRequest,
                    final BlockUserRequest blockReq,
                    @Context SecurityContext securityContext) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (securityContext.getUserPrincipal().getName().equals(blockReq.getUsername())) {
                throw new IllegalArgumentException("cannot block own user");
            }
            
            User user2Block = UserService.getInstance().getUserByUsername(blockReq.getUsername());
            if (user2Block == null) {
                throw new IllegalArgumentException("cannot find user");
            }
            else if (user2Block.getRole().equals('A')) {
                throw new IllegalArgumentException("cannot block other admins");
            }
            
            
            UserService.getInstance().lockUser(blockReq.getUsername(), blockReq.isBlock(), blockReq.getReason());
        } 
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
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
