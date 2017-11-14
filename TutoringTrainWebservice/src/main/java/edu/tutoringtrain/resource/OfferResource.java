/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.ResettableOfferProp;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.dao.OfferService;
import edu.tutoringtrain.data.dao.UserService;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.OfferNotFoundException;
import edu.tutoringtrain.data.exceptions.QueryStringException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.Views;
import java.math.BigDecimal;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

    @Inject
    UserService userService;
    @Inject
    OfferService offerService;
    
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
            Entry offerIn = getMapper().readerWithView(Views.Offer.In.Create.class).forType(Entry.class).readValue(offerStr);
            checkConstraints(offerIn, lang, ConstraintGroups.Create.class);
            Entry offerOut = offerService.createOffer(username, offerIn);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).with(lang.getLocale()).writeValueAsString(offerOut));
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
            Entry offerIn = getMapper().readerWithView(Views.Offer.In.Update.class).forType(Entry.class).readValue(offerStr);
            checkConstraints(offerIn, lang, ConstraintGroups.Update.class);
            User user = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            
            //if user is Admin, he can reset properties of any offer, if not only of the ones the user created
            if (user.getRole().equals(UserRoles.ADMIN)) {
                offerService.updateOffer(offerIn);
            }
            else {
                offerService.updateOffer(offerIn, securityContext.getUserPrincipal().getName());
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
            
            List<Entry> newestOffers = offerService.getNewestOffers(start, pageSize);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).with(lang.getLocale()).writeValueAsString(newestOffers.toArray()));
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
            
            List<Entry> newestOffers = offerService.getNewestOffersOfUser(username, start, pageSize);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).with(lang.getLocale()).writeValueAsString(newestOffers.toArray()));
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
            response.entity(offerService.getCountAll());
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
            ResettableOfferProp[] props2reset = getMapper().reader().forType(ResettableOfferProp[].class).readValue(propStr);
            User user = userService.getUserByUsername(securityContext.getUserPrincipal().getName());
            BigDecimal offerID;
            try {
                offerID = new BigDecimal(id);
            }
            catch (Exception ex) {
                throw new OfferNotFoundException(new ErrorBuilder(Error.OFFER_NOT_FOUND).withParams(id));
            }
            
            //if user is Admin, he can reset properties of any offer, if not only of the ones the user created
            if (user.getRole().equals(UserRoles.ADMIN)) {
                offerService.resetProperties(offerID, props2reset);
            }
            else {
                offerService.resetProperties(offerID, props2reset, securityContext.getUserPrincipal().getName());
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
    
    //WIP
    /*@Secured
    @POST
    @Path("/search")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response searchUsers(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "start") Integer start,
                    @QueryParam(value = "pageSize") Integer pageSize,
                    final String searchStr) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            checkStartPageSize(start, pageSize);
            
            //register module to deserialize SearchCriteria for user
            final SimpleModule module = new SimpleModule();
            module.addDeserializer(SearchCriteria.class, new UserSearchCriteriaDeserializer());
            getMapper().registerModule(module);
            
            UserSearch usIn = getMapper().reader().forType(UserSearch.class).readValue(searchStr);
            List<User> users;
            if (start != null && pageSize != null) users = userService.search(usIn, start, pageSize);
            else users = userService.search(usIn);
            
            
            response.entity(getMapper().writerWithView(Views.User.Out.Private.class)
                    .writeValueAsString(users));
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
    }*/
}
