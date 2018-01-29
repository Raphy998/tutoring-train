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
import edu.tutoringtrain.data.error.CustomHttpStatusCodes;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.error.LocaleSpecificMessageInterpolator;
import edu.tutoringtrain.data.exceptions.BlockException;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.data.exceptions.CommentNotFoundException;
import edu.tutoringtrain.data.exceptions.ForbiddenException;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.EntryNotFoundException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.QueryStringException;
import edu.tutoringtrain.data.exceptions.SubjectNotActiveException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UnauthorizedException;
import edu.tutoringtrain.data.exceptions.UnperformableActionException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.data.exceptions.XMPPException;
import java.text.SimpleDateFormat;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }
    
    protected ObjectMapper getMapper() {
        return mapper;
    }
    
    protected Language getLang(HttpServletRequest httpServletRequest) {
        return (Language)httpServletRequest.getAttribute("lang");
    }
    
    protected void checkConstraints(Object o, Language lang) throws ConstraintViolationException {
        checkConstraints(o, lang, null);
    }
    
    protected void checkConstraints(Object o, Language lang, Class group) throws ConstraintViolationException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();   
        MessageInterpolator interpolator = new LocaleSpecificMessageInterpolator(
                                       factory.getMessageInterpolator(),
                                       lang.getLocale());

        Validator validator = factory.usingContext()
                                      .messageInterpolator(interpolator)
                                      .getValidator();

        Set<ConstraintViolation<Object>> constraintViolations;
        if (group != null) {
            constraintViolations = validator.validate(o, group);
        }
        else {
            constraintViolations = validator.validate(o);
        }
        
        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
    
    protected void handleException(Exception exception, ResponseBuilder response, Language lang) throws Exception {
        try {
            throw exception;
        }
        catch (NumberFormatException ex) {
            ErrorBuilder err = new ErrorBuilder(Error.VALUE_INVALID);
            err.withParams(ex.getMessage());
            response.status(CustomHttpStatusCodes.UNPROCESSABLE_ENTITY);
            response.entity(err
                    .withLang(lang)
                    .build());
        }
        catch (ConstraintViolationException ex) {
            ErrorBuilder err = new ErrorBuilder(Error.CONSTRAINT_VIOLATION);
            
            for (ConstraintViolation violation: ex.getConstraintViolations()) {
                err.withParams(violation.getPropertyPath() + " " + violation.getMessage());
            }
            
            response.status(CustomHttpStatusCodes.UNPROCESSABLE_ENTITY);
            response.entity(err
                    .withLang(lang)
                    .build());
        }
        catch (JsonMappingException | JsonParseException ex) {
            response.status(CustomHttpStatusCodes.MALFORMED_JSON);
            response.entity(new ErrorBuilder(Error.JSON_INVALID)
                    .withParams(ex.getMessage())
                    .withLang(lang)
                    .build());
        }
        catch(InvalidArgumentException ex) {
            response.status(CustomHttpStatusCodes.UNPROCESSABLE_ENTITY);
            response.entity(ex.getError().withLang(lang).build());
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
        catch (CommentNotFoundException ex) {
            response.status(CustomHttpStatusCodes.COMMENT_NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (UserNotFoundException ex) {
            response.status(CustomHttpStatusCodes.USER_NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (EntryNotFoundException ex) {
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
        catch (SubjectNotActiveException ex) {
            response.status(CustomHttpStatusCodes.SUBJECT_NOT_ACTIVE);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (NullValueException ex) {
            response.status(Response.Status.NOT_FOUND);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (UnperformableActionException ex) {
            response.status(CustomHttpStatusCodes.UNPERFORMABLE_ACTION);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (XMPPException ex) {
            response.status(Response.Status.INTERNAL_SERVER_ERROR);
            response.entity(ex.getError().withLang(lang).build());
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    protected void unknownError(Exception ex, ResponseBuilder response, Language lang) throws Exception {
        ex.printStackTrace();
        response.status(Response.Status.INTERNAL_SERVER_ERROR);
        response.entity(new ErrorBuilder(Error.UNKNOWN)
                .withParams(ex.getMessage())
                .withLang(lang)
                .build());
    }
    
    protected void checkStartPageSize(Integer start, Integer pageSize) throws QueryStringException {
        if (start != null && pageSize != null) {
            if (start < 0) {
                throw new QueryStringException(new ErrorBuilder(Error.START_LT_ZERO));
            }
            else if (pageSize <= 0) {
                throw new QueryStringException(new ErrorBuilder(Error.PAGE_SIZE_LTE_ZERO));
            }
        }
    }
}