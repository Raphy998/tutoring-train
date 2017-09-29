/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.Role;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

/**
 *
 * @author Elias
 */
public class AuthenticationService {
    private static AuthenticationService instance = null;
    
    private AuthenticationService() {
    }
    
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    public void authenticate(String username, String password) throws Exception {
        // Authenticate against a database, LDAP, file or whatever
        // Throw an Exception if the credentials are invalid
    }

    public String issueToken(String username) {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        return getRandomToken();
    }
    
    public void checkPermissions(String token, List<Role> roles) throws NotAuthorizedException, ForbiddenException {
        //if token is invalid
        //throw new NotAuthorizedException("token '" + token + "' not valid");
        
        //if token is valid, but user has insufficient priveleges
        throw new ForbiddenException();
    }
    
    private String getRandomToken() {
        String key = null;
        String uuid = UUID.randomUUID().toString();
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(uuid.getBytes(), 0, uuid.length());
            key = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
}
