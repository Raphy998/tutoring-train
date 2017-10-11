/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tutoringtrain.data.CustomHttpStatusCodes;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.BlockException;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.data.exceptions.ForbiddenException;
import edu.tutoringtrain.data.exceptions.OfferNotFoundException;
import edu.tutoringtrain.data.exceptions.QueryStringException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UnauthorizedException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.utils.Views;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author Elias
 */
public abstract class AbstractResource {
    private static ObjectMapper mapper = new ObjectMapper();
    
    static {
        //By default all fields without explicit view definition are included, disable this
        mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setVisibility(FIELD, Visibility.NONE);
        mapper.setVisibility(GETTER, Visibility.PROTECTED_AND_PUBLIC);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ"));
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    protected void handleException(Exception exception, ResponseBuilder response, Language lang) throws Exception {
        try {
            throw exception;
        }
        catch (JsonMappingException | JsonParseException ex) {
            response.status(CustomHttpStatusCodes.MALFORMED_JSON);
            response.entity(new ErrorBuilder(Error.JSON_INVALID)
                    .withParams(ex.getMessage())
                    .withLang(lang)
                    .build());
        }
        catch (UnauthorizedException ex) {
            response.status(Response.Status.UNAUTHORIZED);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (ForbiddenException ex) {
            response.status(Response.Status.FORBIDDEN);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (BlockedException ex) {
            response.status(CustomHttpStatusCodes.BLOCKED);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (SubjectNotFoundException ex) {
            response.status(CustomHttpStatusCodes.SUBJECT_NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (UserNotFoundException ex) {
            response.status(CustomHttpStatusCodes.USER_NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (OfferNotFoundException ex) {
            response.status(CustomHttpStatusCodes.OFFER_NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (QueryStringException ex) {
            response.status(CustomHttpStatusCodes.INVALID_QUERY_STRING);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (BlockException ex) {
            response.status(CustomHttpStatusCodes.BLOCK_ERROR);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    protected void unknownError(Exception ex, ResponseBuilder response, Language lang) throws Exception {
        response.status(Response.Status.INTERNAL_SERVER_ERROR);
        response.entity(new ErrorBuilder(Error.UNKNOWN)
                .withParams(ex.getMessage())
                .withLang(lang)
                .build());
    }
}