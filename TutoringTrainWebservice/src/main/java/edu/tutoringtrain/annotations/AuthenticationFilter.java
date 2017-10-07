/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.annotations;

import edu.tutoringtrain.data.CustomHttpStatusCodes;
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.dao.AuthenticationService;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.entities.User;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Elias
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
@RequestScoped
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    
    @Inject
    AuthenticationService authService;

    @Context
    private ResourceInfo resourceInfo;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithStatus(requestContext, Response.Status.UNAUTHORIZED.getStatusCode());
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                            .substring(AUTHENTICATION_SCHEME.length()).trim();

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);
        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);
        
        try {
            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                authService.checkPermissions(token, classRoles);
            } else {
                authService.checkPermissions(token, methodRoles);
            }

        } 
        catch (NotAuthorizedException e) {
            abortWithStatus(requestContext, Response.Status.UNAUTHORIZED.getStatusCode());
        }
        catch (ForbiddenException e) {
            abortWithStatus(requestContext, Response.Status.FORBIDDEN.getStatusCode());
        }
        catch (BlockedException e) {
            abortWithStatus(requestContext, CustomHttpStatusCodes.BLOCKED, e.getMessage());
        }
        
        User user = authService.getUserByToken(token);
        final String username = user != null ? user.getUsername() : null;
        
        final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
            requestContext.setSecurityContext(new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {

                    return new Principal() {

                        @Override
                        public String getName() {
                            return username;
                        }
                    };
                }

                @Override
                public boolean isUserInRole(String role) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return currentSecurityContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return AUTHENTICATION_SCHEME;
                }
            });
    }
    
    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // Authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                    .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithStatus(ContainerRequestContext requestContext, int status) {
        // Abort the filter chain with a 401 status code
        // The "WWW-Authenticate" is sent along with the response
        requestContext.abortWith(
                Response.status(status)
                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                        .build());
    }
    
    private void abortWithStatus(ContainerRequestContext requestContext, int status, String msg) {
        // Abort the filter chain with a 401 status code
        // The "WWW-Authenticate" is sent along with the response
        requestContext.abortWith(
                Response.status(status)
                        .header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME)
                        .entity(msg)
                        .build());
    }
}
