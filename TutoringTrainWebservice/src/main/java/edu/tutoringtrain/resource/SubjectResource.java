/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.dao.SubjectService;
import edu.tutoringtrain.data.dao.UserService;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.data.error.CustomHttpStatusCodes;
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
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Localized
@Path("/subject")
@RequestScoped
public class SubjectResource extends AbstractResource {
    
    @Inject
    SubjectService subjectService;
    @Inject
    UserService userService;
    
    @Secured
    @GET
    @Path("all")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).with(lang.getLocale()).writeValueAsString(subjects.toArray()));
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
    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllActive(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            List<Subject> subjects = subjectService.getAllActiveSubjects();
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).with(lang.getLocale()).writeValueAsString(subjects.toArray()));
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
    
    @Secured(Role.ADMIN)
    @GET
    @Path("inactive")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllInactive(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            List<Subject> subjects = subjectService.getAllInactiveSubjects();
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).with(lang.getLocale()).writeValueAsString(subjects.toArray()));
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
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final String subjectStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        Subject subjectIn = null;
        
        try {
            subjectIn = getMapper().readerWithView(Views.Subject.In.Create.class).forType(Subject.class).readValue(subjectStr);
            checkConstraints(subjectIn, lang, ConstraintGroups.Create.class);
            
            //if user is admin, activate subject immediately
            if (userService.getUserByUsername(securityContext.getUserPrincipal().getName()).getRole().equals(UserRoles.ADMIN)) {
                subjectIn.setIsactive('1');
            }
            else {
                subjectIn.setIsactive('0');
            }
            Subject subjectOut = subjectService.createSubject(subjectIn);
            
            response.entity(getMapper().writerWithView(Views.Subject.Out.Public.class).with(lang.getLocale()).writeValueAsString(subjectOut));
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity(new ErrorBuilder(Error.SUBJECT_CONFLICT)
                        .withParams(subjectIn.getEnname() + "/" + subjectIn.getDename())
                        .withLang(lang)
                        .build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
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
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        Subject subjectIn = null;
        
        try {
            subjectIn = getMapper().readerWithView(Views.Subject.In.Update.class).forType(Subject.class).readValue(subjectStr);
            checkConstraints(subjectIn, lang, ConstraintGroups.Update.class);
            subjectService.updateSubject(subjectIn);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                String param;
                if (subjectIn.getDename() != null && subjectIn.getEnname() != null) 
                    param = subjectIn.getEnname() + "/" + subjectIn.getDename();
                else if (subjectIn.getEnname() != null)
                    param = subjectIn.getEnname();
                else if (subjectIn.getDename() != null)
                    param = subjectIn.getDename();
                else        //should never happen
                    param = "-";
                
                response.status(Response.Status.CONFLICT);
                response.entity(new ErrorBuilder(Error.SUBJECT_CONFLICT)
                        .withParams(param)
                        .withLang(lang)
                        .build());
            }
            catch (NullValueException npex) {
                response.status(Response.Status.NOT_FOUND);
                response.entity(new ErrorBuilder(Error.SUBJECT_NOT_FOUND)
                        .withLang(lang)
                        .build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
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
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            subjectService.removeSubject(subjectId);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                response.status(CustomHttpStatusCodes.SUBJECT_USED);
                response.entity(new ErrorBuilder(Error.SUBJECT_USED).withParams(subjectId).withLang(lang).build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }

        return response.build();
    }
    
    @Secured
    @GET
    @Path("/count")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getCountAll(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(subjectService.getCountAll());
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
