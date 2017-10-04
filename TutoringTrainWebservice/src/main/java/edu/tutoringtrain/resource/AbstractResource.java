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
import edu.tutoringtrain.data.exceptions.BlockedException;
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
    
    protected void handleException(Exception exception, ResponseBuilder response) throws Exception {
        try {
            throw exception;
        }
        catch (JsonMappingException | JsonParseException ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.MALFORMED_JSON, ex.getMessage()));
        }
        catch (BlockedException ex) {
            response.status(CustomHttpStatusCodes.BLOCKED);
            response.entity(ex.getMessage());
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    protected void unknownError(Exception exception, ResponseBuilder response) throws Exception {
        response.status(Response.Status.INTERNAL_SERVER_ERROR);
        response.entity(new edu.tutoringtrain.data.Error(edu.tutoringtrain.data.Error.UNKNOWN, exception.getMessage()));
    }
}