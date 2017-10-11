/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import static edu.tutoringtrain.data.error.ErrorBuilder.*;
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
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.utils.Views;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.TransactionalException;
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
@RequestScoped
public class SubjectResource extends AbstractResource {
    
    @Inject
    SubjectService subjectService;
    
    @Secured
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).writeValueAsString(subjects.toArray()));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.getDefault());
            }
            catch (Exception e) {
                unknownError(e, response, Language.getDefault());
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
            Subject subjectOut = subjectService.createSubject(subjectIn);
            
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).writeValueAsString(subjectOut));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.getDefault());
            }
            catch (TransactionalException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity(new ErrorBuilder(Error.SUBJECT_CONFLICT)
                        .withParams(subjectIn.getName())
                        .withLang(Language.getDefault())
                        .build());
            }
            catch (Exception e) {
                unknownError(e, response, Language.getDefault());
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
            subjectService.updateSubject(subjectIn);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.getDefault());
            }
            catch (NullValueException npex) {
                response.status(Response.Status.NOT_FOUND);
                response.entity(new ErrorBuilder(Error.USER_NOT_FOUND)
                        .withLang(Language.getDefault())
                        .build());
            }
            catch (Exception e) {
                unknownError(e, response, Language.getDefault());
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
            subjectService.removeSubject(subjectId);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.getDefault());
            }
            catch (Exception e) {
                unknownError(e, response, Language.getDefault());
            } 
        }

        return response.build();
    }
}
