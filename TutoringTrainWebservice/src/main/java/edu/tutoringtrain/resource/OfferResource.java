/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.dao.OfferService;
import edu.tutoringtrain.data.exceptions.QueryStringException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Offer;
import edu.tutoringtrain.utils.Views;
import java.util.List;
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
@Path("/offer")
public class OfferResource extends AbstractResource {

    @Secured
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final String offerStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Offer offerIn = getMapper().readerWithView(Views.Offer.In.Create.class).withType(Offer.class).readValue(offerStr);
            Offer offerOut = OfferService.getInstance().createOffer(username, offerIn);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).writeValueAsString(offerOut));
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
    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    final String offerStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Offer offerIn = getMapper().readerWithView(Views.Offer.In.Update.class).withType(Offer.class).readValue(offerStr);
            OfferService.getInstance().updateOffer(username, offerIn);
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
    @Path("/new")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getNewest(@Context HttpServletRequest httpServletRequest,
            @QueryParam(value = "start") Integer start,
            @QueryParam(value = "pageSize") Integer pageSize) throws Exception {

        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (start == null || pageSize == null) {
                throw new QueryStringException("start and pageSize must be given in query string");
            }
            List<Offer> newestOffers = OfferService.getInstance().getNewestOffers(start, pageSize);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).writeValueAsString(newestOffers.toArray()));
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
    @Path("/new/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getNewestOfUser(@Context HttpServletRequest httpServletRequest,
            @PathParam(value = "username") String username,
            @QueryParam(value = "start") Integer start,
            @QueryParam(value = "pageSize") Integer pageSize) throws Exception {

        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (username == null) {
                throw new UserNotFoundException("username must be given");
            }
            
            if (start == null || pageSize == null) {
                throw new QueryStringException("start and pageSize must be given in query string");
            }
            
            List<Offer> newestOffers = OfferService.getInstance().getNewestOffersOfUser(username, start, pageSize);
            response.entity(getMapper().writerWithView(Views.Offer.Out.Public.class).writeValueAsString(newestOffers.toArray()));
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
