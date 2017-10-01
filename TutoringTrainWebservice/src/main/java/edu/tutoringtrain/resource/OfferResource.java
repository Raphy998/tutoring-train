/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.CreateOfferRequest;
import edu.tutoringtrain.data.OfferResponse;
import edu.tutoringtrain.data.UpdateOfferRequest;
import edu.tutoringtrain.data.dao.OfferService;
import edu.tutoringtrain.entities.Offer;
import java.util.ArrayList;
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
public class OfferResource {

    @Secured
    @POST
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest httpServletRequest,
                    final CreateOfferRequest offer,
                    @Context SecurityContext securityContext) throws Exception {
        
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Offer o = OfferService.getInstance().createOffer(username, offer);
            response.entity(new OfferResponse(o.getDescription(), o.getId(), o.getIsactive(), o.getPostedon(), o.getDuedate(), o.getSubject(), o.getUsername().getUsername()));
        } 
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
    
    @Secured
    @PUT
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response update(@Context HttpServletRequest httpServletRequest,
                    final UpdateOfferRequest offer,
                    @Context SecurityContext securityContext) throws Exception {
        
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            OfferService.getInstance().updateOffer(username, offer);
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.UNKNOWN, ex.getMessage()));
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
                throw new IllegalArgumentException("start and pageSize must be given in query string");
            }
            
            ArrayList<OfferResponse> offers = new ArrayList<>();
            for (Offer o: OfferService.getInstance().getNewestOffers(start, pageSize)) {
                offers.add(new OfferResponse(o.getDescription(), o.getId(), o.getIsactive(), o.getPostedon(), o.getDuedate(), o.getSubject(), o.getUsername().getUsername()));
            }
            response.entity(offers.toArray(new OfferResponse[0]));
        } 
        catch (IllegalArgumentException ex) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.MISSING_QUERY_PARAMS, ex.getMessage()));
        }
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.UNKNOWN, ex.getMessage()));
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
                throw new IllegalArgumentException("username must be given");
            }
            
            if (start == null || pageSize == null) {
                throw new IllegalArgumentException("start and pageSize must be given in query string");
            }
            
            ArrayList<OfferResponse> offers = new ArrayList<>();
            for (Offer o: OfferService.getInstance().getNewestOffersOfUser(username, start, pageSize)) {
                offers.add(new OfferResponse(o.getDescription(), o.getId(), o.getIsactive(), o.getPostedon(), o.getDuedate(), o.getSubject(), o.getUsername().getUsername()));
            }
            response.entity(offers.toArray(new OfferResponse[0]));
        } 
        catch (IllegalArgumentException ex) {
            response.status(Response.Status.BAD_REQUEST);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.MISSING_QUERY_PARAMS, ex.getMessage()));
        }
        catch (Exception ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.UNKNOWN, ex.getMessage()));
        }

        return response.build();
    }
}
