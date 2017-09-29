/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.User;
import edu.tutoringtrain.data.dao.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
                    final User userToRegister) throws Exception {

        String msg = null;
        Response.ResponseBuilder response = Response.status(Response.Status.NOT_ACCEPTABLE);

        try {
            if (User.isEmailValid(userToRegister.getEmail())) {
                if (UserService.getInstance().sendRegisterVerificationEmail(userToRegister)) {
                    msg = "Sent verification email to: '" + userToRegister.getEmail() + "'";
                    System.out.println(msg);
                    response.status(Response.Status.OK);
                }
                else {
                    msg = "Error sending email to: '" + userToRegister.getEmail() + "'";
                    System.out.println(msg);
                    response.status(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }

        } catch (Exception e) {
                response.status(Response.Status.INTERNAL_SERVER_ERROR);
                msg = "Error sending email";
        }
        finally {
            response.entity(msg);
        }

        return response.build();
    }
    
    @GET
    @Secured
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
    
    
}
