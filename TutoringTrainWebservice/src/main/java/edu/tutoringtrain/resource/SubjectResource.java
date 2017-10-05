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
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.dao.SubjectService;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.utils.Views;
import java.util.List;
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
public class SubjectResource extends AbstractResource {
    
    @Secured
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            List<Subject> subjects = SubjectService.getInstance().getAllSubjects();
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).writeValueAsString(subjects.toArray()));
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
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final String subjectStr) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        Subject subjectIn = null;
        
        try {
            subjectIn = getMapper().readerWithView(Views.Subject.In.Create.class).withType(Subject.class).readValue(subjectStr);
            Subject subjectOut = SubjectService.getInstance().createSubject(subjectIn);
            
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).writeValueAsString(subjectOut));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response);
            }
            catch (RollbackException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity("subject '" + subjectIn.getName() + "' already exists");
            }
            catch (Exception e) {
                unknownError(e, response);
            } 
        }

        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    final String subjectStr) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Subject subjectIn = getMapper().readerWithView(Views.Subject.In.Update.class).withType(Subject.class).readValue(subjectStr);
            SubjectService.getInstance().updateSubject(subjectIn);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response);
            }
            catch (NullPointerException npex) {
                response.status(Response.Status.NOT_FOUND);
            }
            catch (Exception e) {
                unknownError(e, response);
            } 
        }

        return response.build();
    }
    
    @Secured(Role.ADMIN)
    @DELETE
    @Path("{id}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpServletRequest httpServletRequest,
                    @PathParam(value = "id") Integer subjectId) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            SubjectService.getInstance().removeSubject(subjectId);
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
