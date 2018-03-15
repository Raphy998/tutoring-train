/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.PrincipalInRole;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.EntryType;
import edu.tutoringtrain.data.ResettableEntryProp;
import edu.tutoringtrain.data.UserRole;
import edu.tutoringtrain.data.dao.CommentService;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.dao.EntryService;
import edu.tutoringtrain.data.dao.UserService;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.EntryNotFoundException;
import edu.tutoringtrain.data.exceptions.QueryStringException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.data.search.SearchCriteria;
import edu.tutoringtrain.data.search.entry.EntryProp;
import edu.tutoringtrain.data.search.entry.EntrySearch;
import edu.tutoringtrain.data.search.entry.EntrySearchCriteriaDeserializer;
import edu.tutoringtrain.entities.Comment;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.Views;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Localized
@Path("/offer")
@RequestScoped
public class OfferResource extends AbstractResource {

    private static final EntryType type = EntryType.OFFER; 
    
    @Inject
    UserService userService;
    @Inject
    EntryService entryService;
    @Inject
    CommentService commentService;
    
    @Secured
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final String offerStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        String username = securityContext.getUserPrincipal().getName();
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Entry offerIn = getMapper().readerWithView(Views.Entry.In.Create.class).forType(Entry.class).readValue(offerStr);
            checkConstraints(offerIn, lang, ConstraintGroups.Create.class);
            Entry offerOut = entryService.createEntry(type, username, offerIn);
            response.entity(getMapper().writerWithView(Views.Entry.Out.Public.class).with(lang.getLocale()).writeValueAsString(offerOut));
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
    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    final String offerStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Entry offerIn = getMapper().readerWithView(Views.Entry.In.Update.class).forType(Entry.class).readValue(offerStr);
            checkConstraints(offerIn, lang, ConstraintGroups.Update.class);
            User user = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            
            //if user is Admin, he can reset properties of any offer, if not only of the ones the user created
            if (UserRole.toUserRole(user.getRole()).isAdmin())
                entryService.updateEntry(type, offerIn);
            else
                entryService.updateEntry(type, offerIn, securityContext.getUserPrincipal().getName());
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
    @Path("/new")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getNewest(@Context HttpServletRequest httpServletRequest,
            @QueryParam(value = "start") Integer start,
            @QueryParam(value = "pageSize") Integer pageSize) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (start == null || pageSize == null) {
                throw new QueryStringException(new ErrorBuilder(Error.START_PAGESIZE_QUERY_MISSING));
            }
            checkStartPageSize(start, pageSize);
            
            List<Entry> newestOffers = entryService.getNewestEntries(type, start, pageSize);
            response.entity(getMapper().writerWithView(Views.Entry.Out.Public.class).with(lang.getLocale()).writeValueAsString(newestOffers.toArray()));
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
    @Path("/new/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getNewestOfUser(@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "username") String username,
            @QueryParam(value = "start") Integer start,
            @QueryParam(value = "pageSize") Integer pageSize) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (username == null) {
                throw new UserNotFoundException(new ErrorBuilder(Error.USERNAME_NULL));
            }
            
            if (start == null || pageSize == null) {
                throw new QueryStringException(new ErrorBuilder(Error.START_PAGESIZE_QUERY_MISSING));
            }
            
            List<Entry> newestOffers = entryService.getNewestEntiresOfUser(type, username, start, pageSize);
            response.entity(getMapper().writerWithView(Views.Entry.Out.Public.class).with(lang.getLocale()).writeValueAsString(newestOffers.toArray()));
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
    @DELETE
    @Path("/{id}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "id") String id,
            @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Integer idInt;
            try { idInt = Integer.parseInt(id); }
            catch (NumberFormatException ex) { throw new NumberFormatException(id); }
            
            if (((PrincipalInRole) securityContext.getUserPrincipal()).getRole().isAdmin())
                entryService.deleteEntry(type, new BigDecimal(idInt));
            else 
                entryService.deleteEntry(type, new BigDecimal(idInt), securityContext.getUserPrincipal().getName());
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
    @Path("/count")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getCountAll(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(entryService.getCountAll(type));
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
    @Path("/reset/{id}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response reset(@Context HttpServletRequest httpServletRequest,
                    @PathParam("id") String id,
                    final String propStr,
                    @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            ResettableEntryProp[] props2reset = getMapper().reader().forType(ResettableEntryProp[].class).readValue(propStr);
            User user = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            BigDecimal offerID;
            try {
                offerID = new BigDecimal(id);
            }
            catch (Exception ex) {
                throw new EntryNotFoundException(new ErrorBuilder(Error.OFFER_NOT_FOUND).withParams(id));
            }
            
            //if user is Admin, he can reset properties of any offer, if not only of the ones he created
            if (UserRole.toUserRole(user.getRole()).isAdmin()) {
                entryService.resetProperties(type, offerID, props2reset);
            }
            else {
                entryService.resetProperties(type, offerID, props2reset, securityContext.getUserPrincipal().getName());
            }
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
    @Path("/search")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response searchOffers(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "start") Integer start,
                    @QueryParam(value = "pageSize") Integer pageSize,
                    final String searchStr) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            checkStartPageSize(start, pageSize);
            
            //register module to deserialize SearchCriteria for offer
            ObjectMapper customMapper = getMapper().copy();
            final SimpleModule module = new SimpleModule();
            module.addDeserializer(SearchCriteria.class, new EntrySearchCriteriaDeserializer());
            customMapper.registerModule(module);
            
            EntrySearch osIn = customMapper.reader().forType(EntrySearch.class).readValue(searchStr);
            List<Entry> offers;
            if (start != null && pageSize != null) offers = entryService.search(type, osIn, start, pageSize);
            else offers = entryService.search(type, osIn);
            
            
            response.entity(getMapper().writerWithView(Views.Entry.Out.Public.class)
                    .writeValueAsString(offers));
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
    @Path("/{id}/comments")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getComments(@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "id") String id) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Integer idInt;
            try { idInt = Integer.parseInt(id); }
            catch (NumberFormatException ex) { throw new NumberFormatException(id); }
            
            response.entity(getMapper().writerWithView(Views.Comment.Out.Public.class).with(lang.getLocale()).writeValueAsString(
                    commentService.getComments(EntryType.OFFER, idInt).toArray()));
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
    @Path("/{id}/comments")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response createComment (@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "id") String id,
            final String commentStr,
            @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Integer idInt;
            try { idInt = Integer.parseInt(id); }
            catch (NumberFormatException ex) { throw new NumberFormatException(id); }
            
            Comment comment = getMapper().readerWithView(Views.Comment.In.Create.class).forType(Comment.class).readValue(commentStr);
            checkConstraints(comment, lang, ConstraintGroups.Create.class);
            comment.setUser(new User(securityContext.getUserPrincipal().getName()));
            
            response.entity(getMapper().writerWithView(Views.Comment.Out.Public.class).with(lang.getLocale()).writeValueAsString(
                commentService.createComment(type, idInt, comment)));
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
    @PUT
    @Path("/{id}/comments")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateComment (@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "id") String id,
            final String commentStr,
            @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Integer idInt;
            try { idInt = Integer.parseInt(id); }
            catch (NumberFormatException ex) { throw new NumberFormatException(id); }
            
            Comment comment = getMapper().readerWithView(Views.Comment.In.Update.class).forType(Comment.class).readValue(commentStr);
            checkConstraints(comment, lang, ConstraintGroups.Update.class);
            
            if (((PrincipalInRole) securityContext.getUserPrincipal()).getRole().isAdmin())
                commentService.updateComment(type, idInt, comment);
            else 
                commentService.updateComment(type, idInt, comment, securityContext.getUserPrincipal().getName());
            
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
    @DELETE
    @Path("/{id}/comments/{commentId}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response deleteComment (@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "id") String id,
            @PathParam(value = "commentId") String commentId,
            @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Integer idInt;
            try { idInt = Integer.parseInt(id); }
            catch (NumberFormatException ex) { throw new NumberFormatException(id); }
            
            Integer commentIdInt;
            try { commentIdInt = Integer.parseInt(commentId); }
            catch (NumberFormatException ex) { throw new NumberFormatException(commentId); }
            
            if (((PrincipalInRole) securityContext.getUserPrincipal()).getRole().isAdmin())
                commentService.deleteComment(type, idInt, commentIdInt);
            else 
                commentService.deleteComment(type, idInt, commentIdInt, securityContext.getUserPrincipal().getName());
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
    @Path("/simpleSearch")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response simpleSearchOffers(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "start") Integer start,
                    @QueryParam(value = "pageSize") Integer pageSize,
                    @QueryParam(value = "searchStr") String searchStr) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            checkStartPageSize(start, pageSize);
            if (searchStr == null) {
                throw new QueryStringException(new ErrorBuilder(Error.SEARCH_STRING_NULL));
            }
            
            List<Entry> offers;
            if (start != null && pageSize != null) offers = entryService.simpleSearch(type, searchStr, start, pageSize);
            else offers = entryService.simpleSearch(type, searchStr);
            
            response.entity(getMapper().writerWithView(Views.Entry.Out.Public.class)
                    .writeValueAsString(offers));
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
