/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
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
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.dao.SubjectService;
import edu.tutoringtrain.entities.Subject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Path("/subject")
public class SubjectResource {
    
    @Secured
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest httpServletRequest) throws Exception {

        Subject[] subjects;
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            subjects = SubjectService.getInstance().getAllSubjects().toArray(new Subject[0]);
            response.entity(subjects);
        } 
        catch (Exception ex) { 
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final Subject subject) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(SubjectService.getInstance().createSubject(subject.getName()));
        } 
        catch (RollbackException ex) {
            response.status(Response.Status.CONFLICT);
            response.entity(new Error(Error.DUPLICATE_SUBJECT_NAME, "subject '" + subject.getName() + "' already exists"));
        }
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    final Subject subject) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            SubjectService.getInstance().updateSubject(subject);
        } 
        catch (NullPointerException ex) {
            response.status(Response.Status.NOT_FOUND);
        }
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @DELETE
    @Path("{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    @PathParam(value = "id") Integer subjectId) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            SubjectService.getInstance().removeSubject(subjectId);
        } 
        catch (NullPointerException ex) {
            response.status(Response.Status.NOT_FOUND);
        }
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new Error(Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
}
