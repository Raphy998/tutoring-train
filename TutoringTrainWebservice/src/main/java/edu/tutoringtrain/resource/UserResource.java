/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.resource;

import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.tutoringtrain.annotations.Localized;
import edu.tutoringtrain.annotations.Secured;
import edu.tutoringtrain.data.Gender;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.dao.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import edu.tutoringtrain.data.UserRole;
import edu.tutoringtrain.data.ResettableUserProp;
import edu.tutoringtrain.data.dao.EmailService;
import edu.tutoringtrain.data.error.ConstraintGroups;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.BlockException;
import edu.tutoringtrain.data.exceptions.UnauthorizedException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.data.search.SearchCriteria;
import edu.tutoringtrain.data.search.user.UserSearchCriteriaDeserializer;
import edu.tutoringtrain.data.search.user.UserSearch;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.ImageUtils;
import edu.tutoringtrain.utils.Views;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javax.activation.UnsupportedDataTypeException;
import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.transaction.TransactionalException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.SecurityContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * REST Web Service
 *
 * @author Elias
 */
@Localized
@Path("/user")
@RequestScoped
public class UserResource extends AbstractResource {
    @Inject
    UserService userService;
    @Inject
    EmailService emailService;
    
    @POST
    @Path("/register")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response register(@Context HttpServletRequest httpServletRequest,
                    final String userStr) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        User userIn = null;
        
        try {
            userIn = getMapper().readerWithView(Views.User.In.Register.class).forType(User.class).readValue(userStr);
            checkConstraints(userIn, lang, ConstraintGroups.Create.class);
            User userOut;
            
            if (userIn.getPassword() == null) {
                //if user doesn't have a password set, generate one and send it to the given email
                String genPassword = RandomStringUtils.randomAlphanumeric(8);
                userIn.setPassword(DigestUtils.md5Hex(genPassword));
                userOut = userService.registerUser(userIn);
                emailService.sendWelcomeEmail(userOut, false, genPassword);
            }
            else {
                userOut = userService.registerUser(userIn);
                emailService.sendWelcomeEmail(userIn, false);
            }
            
            response.entity(getMapper().writerWithView(Views.User.Out.Private.class).writeValueAsString(userOut));
        }
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity(getError(rbex, userIn).withLang(lang).build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
 
        return response.build();
    }
    
    @Secured
    @PUT
    @Path("/update/own")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateOwn(@Context HttpServletRequest httpServletRequest,
                    final String userStr,
                    @Context SecurityContext securityContext) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        User userIn = null;
        
        try {
            userIn = getMapper().readerWithView(Views.User.In.Update.class).forType(User.class).readValue(userStr);
            userIn.setUsername(securityContext.getUserPrincipal().getName());
            checkConstraints(userIn, lang);
            userService.updateUser(userIn);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity(getError(rbex, userIn).withLang(lang).build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
 
        return response.build();
    }
    
    @Secured(UserRole.ADMIN)
    @PUT
    @Path("/update")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response updateAny(@Context HttpServletRequest httpServletRequest,
                    final String userStr) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        User userIn = null;
        
        try {
            userIn = getMapper().readerWithView(Views.User.In.Update.class).forType(User.class).readValue(userStr);
            checkConstraints(userIn, lang);
            userService.updateUser(userIn);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (TransactionalException rbex) {
                response.status(Response.Status.CONFLICT);
                response.entity(getError(rbex, userIn).withLang(lang).build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
 
        return response.build();
    }
    
    @Secured(UserRole.ADMIN)
    @POST
    @Path("/reset/{username}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resetAny(@Context HttpServletRequest httpServletRequest,
                    @PathParam("username") String username,
                    final String propStr) throws Exception {

        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            ResettableUserProp[] props2reset = getMapper().reader().forType(ResettableUserProp[].class).readValue(propStr);
            userService.resetProperties(username, props2reset);
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
    @Path("/reset")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resetOwn(@Context HttpServletRequest httpServletRequest,
                    final String propStr,
                    @Context SecurityContext securityContext) throws Exception {

        return resetAny(httpServletRequest, propStr, securityContext.getUserPrincipal().getName());
    }
    
    @Secured
    @GET
    @Path("/gender")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getAllGenders(@Context HttpServletRequest httpServletRequest) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            List<Gender> genders = userService.getAllGenders(lang);
            response.entity(getMapper().writeValueAsString(genders.toArray()));
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
    public Response getOwnUser(@Context HttpServletRequest httpServletRequest,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        String username = securityContext.getUserPrincipal().getName();
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            response.entity(getMapper().writerWithView(Views.User.Out.Private.class)
                    .writeValueAsString(userService.getUserByUsername(username)));
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
    
    @Secured(UserRole.ADMIN)
    @GET
    @Path("/all")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getUsers(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "start") Integer start,
                    @QueryParam(value = "pageSize") Integer pageSize) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            checkStartPageSize(start, pageSize);
            List<User> userEntities;
            if (start != null && pageSize != null) {
                userEntities = userService.getUsers(start, pageSize);
            }
            else {
                userEntities = userService.getUsers();
            }

            response.entity(getMapper().writerWithView(Views.User.Out.Private.class)
                    .writeValueAsString(userEntities.toArray()));
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
    
    @Secured(UserRole.ADMIN)
    @POST
    @Path("/block")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response blockUser(@Context HttpServletRequest httpServletRequest,
                    final String blockStr,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            Blocked blockIn = getMapper().readerWithView(Views.Block.In.Create.class).forType(Blocked.class).readValue(blockStr);
            checkConstraints(blockIn, lang);
            
            if (securityContext.getUserPrincipal().getName().equals(blockIn.getUsername())) {
                throw new BlockException(new ErrorBuilder(Error.USER_BLOCK_OWN));
            }
            
            User user2Block = userService.getUserByUsername(blockIn.getUsername());
            if (user2Block == null) {
                throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(blockIn.getUsername()));
            }
            else if (user2Block.getRole().equals('A')) {
                throw new BlockException(new ErrorBuilder(Error.USER_BLOCK_ADMIN));
            }
            
            userService.blockUser(blockIn, true);
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
    
    @Secured(UserRole.ADMIN)
    @GET
    @Path("/unblock/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response unblockUser(@Context HttpServletRequest httpServletRequest,
                    @PathParam("username") String user2unblock,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            if (securityContext.getUserPrincipal().getName().equals(user2unblock)) {
                throw new BlockException(new ErrorBuilder(Error.USER_UNBLOCK_OWN));
            }
            
            User user = userService.getUserByUsername(user2unblock);
            if (user == null) {
                throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(user2unblock));
            }
            
            userService.blockUser(new Blocked(user2unblock), false);
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
            response.entity(userService.getCountAll());
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
    
    private static ErrorBuilder getError(TransactionalException ex, User userIn) {
        ErrorBuilder err;
        try {
            SQLIntegrityConstraintViolationException innerEx = (SQLIntegrityConstraintViolationException) ex.getCause().getCause().getCause().getCause();
            
            if (innerEx.getMessage().contains("U_USER_EMAIL")) {
                err = new ErrorBuilder(Error.EMAIL_CONFLICT).withParams(userIn.getEmail());
            }
            else if (innerEx.getMessage().contains("PK_TUSER")) {
                err = new ErrorBuilder(Error.USERNAME_CONFLICT).withParams(userIn.getUsername());
            }
            else {
                err = new ErrorBuilder(Error.UNKNOWN).withParams(innerEx.getMessage());
            }
        }
        catch (Exception e) {
            err = new ErrorBuilder(Error.UNKNOWN).withParams(e.getMessage());
        }
        
        return err;
    }
    
    @Secured
    @POST
    @Path("/avatar/B64")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response setAvatarB64(@Context HttpServletRequest httpServletRequest,
                    InputStream uploadedInputStream,
                    @Context SecurityContext securityContext) throws Exception {
        try {
            BufferedImage bi = ImageIO.read(uploadedInputStream);
            bi = ImageUtils.getScaledImage(bi, 360);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bi, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();

                userService.setAvatar(securityContext.getUserPrincipal().getName(), imageInByte);
            }
            return Response.ok().build();
        }      
        finally {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    
    @Secured
    @POST
    @Path("/avatar")
    @Consumes(value = MediaType.MULTIPART_FORM_DATA)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response setAvatar(@Context HttpServletRequest httpServletRequest,
                    @FormDataParam("name") String name,
                    @FormDataParam("file") InputStream uploadedInputStream,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        String imgType = null;
        
        try {
            imgType = FilenameUtils.getExtension(name);
            if (imgType.equalsIgnoreCase("jpeg")) imgType = "jpg";

            if (!imgType.equalsIgnoreCase("png") && !imgType.equalsIgnoreCase("jpg")) {
                throw new UnsupportedDataTypeException("only png and jpg are supported");
            }
            else {
                BufferedImage bi = ImageIO.read(uploadedInputStream);
                bi = ImageUtils.getScaledImage(bi, 360);

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(bi, imgType, baos);
                    baos.flush();
                    byte[] imageInByte = baos.toByteArray();
                    
                    userService.setAvatar(securityContext.getUserPrincipal().getName(),
                            imageInByte);
                }
            }
            
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, lang);
            }
            catch (UnsupportedDataTypeException e) {
                response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
                response.entity(new ErrorBuilder(Error.UNSUPPORTED_MEDIA_TYPE).withParams(imgType).withLang(lang).build());
            }
            catch (Exception e) {
                unknownError(e, response, lang);
            } 
        }
 
        return response.build();
    }
    
    @Secured
    @GET
    @Path("/avatar/{username}")
    @Produces("image/jpg")
    public Response getAvatar(@Context HttpServletRequest httpServletRequest,
            @PathParam("username") String username) throws Exception
    {
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            byte[] avatar = userService.getAvatar(username);
            
            if (avatar != null) {
                
                final InputStream bigInputStream = new ByteArrayInputStream(avatar);
                response.type("image/jpg").entity(bigInputStream);
            }
            else {
                response.status(Response.Status.NO_CONTENT);
            }
        } 
        catch (Exception ex) {
            response.type(MediaType.APPLICATION_JSON);
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
    @Path("/avatar")
    @Produces("image/jpg")
    public Response getAvatarOwn(@Context HttpServletRequest httpServletRequest,
            @Context SecurityContext securityContext) throws Exception {
        return getAvatar(httpServletRequest, securityContext.getUserPrincipal().getName());
    }
    
    @Secured
    @DELETE
    @Path("/avatar")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resetAvatarOwn(@Context HttpServletRequest httpServletRequest,
                    @Context SecurityContext securityContext) throws Exception {
        return resetAvatar(httpServletRequest, securityContext.getUserPrincipal().getName());
    }
    
    @Secured(UserRole.ADMIN)
    @DELETE
    @Path("/avatar/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response resetAvatar(@Context HttpServletRequest httpServletRequest,
                    @PathParam("username") String username) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            userService.resetAvatar(username);
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
    
    @Secured(UserRole.ADMIN)
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
    }
    
    @Secured
    @GET
    @Path("/testNL/{username}")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response testNL(@Context HttpServletRequest httpServletRequeste,
                            @PathParam("username") String username) throws Exception {
        
        Response.ResponseBuilder response = Response.status(Response.Status.OK);

        try {
            emailService.sendNewsletter(userService.getUserByUsername(username), false);
        } 
        catch (Exception ex) {
            try {
                handleException(ex, response, Language.EN);
            }
            catch (Exception e) {
                unknownError(e, response, Language.EN);
            } 
        }
 
        return response.build();
    }
    
    @Secured(UserRole.ADMIN)
    @PUT
    @Path("/role/{username}")
    @Consumes(value = MediaType.APPLICATION_JSON)
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response setRole(@Context HttpServletRequest httpServletRequest,
                    @PathParam("username") String username,
                    final String newRole,
                    @Context SecurityContext securityContext) throws Exception {
        
        Language lang = getLang(httpServletRequest);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        
        try {
            UserRole role = UserRole.toUserRole(((User)getMapper().readerWithView(Views.User.In.Promote.class).forType(User.class).readValue(newRole)).getRole());
            User user2update = userService.getUserByUsername(username);
            
            if (username.equals(securityContext.getUserPrincipal().getName())) {
                throw new UnauthorizedException(new ErrorBuilder(Error.DEGRADE_OWN));
            }
            if (user2update.getRole().equals(UserRole.ADMIN.getChar())) {
                throw new UnauthorizedException(new ErrorBuilder(Error.DEGRADE_ADMIN));
            }
            else {
                userService.setRole(username, role);
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
    
    //TODO: Unused until static IP
    /*
    @GET
    @Path("/verify")
    @Produces(value = { MediaType.APPLICATION_JSON})
    public Response verifyAccount(@Context HttpServletRequest httpServletRequest,
                    @QueryParam(value = "key") String key) throws Exception {

        String msg = null;
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        try {
            if (UserService.getInstance().verifyUserAccount(key)) {
                msg = "Verified";
            }
            else {
                response.status(Response.Status.BAD_REQUEST);
                msg = "Error";
            }
        }
        catch (Exception e) {
           msg = "Ex";
        }
        finally {
            response.entity(msg);
        }

        return response.build();
    }
    */
}
