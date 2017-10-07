/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.Session;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class AuthenticationService extends AbstractService {
    
    public AuthenticationService() {
    }
    
    @Transactional(dontRollbackOn = Exception.class)
    public void authenticate(String username, String password, Character requiredRole) throws NotAuthorizedException, ForbiddenException, BlockedException {
        TypedQuery<User> query =
        em.createNamedQuery("User.findByUsernameAndPassword", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<User> results = query.getResultList();
        
        if (results.isEmpty()) {
            throw new NotAuthorizedException("authentication failed");
        }

        if (!canAuthenticate(requiredRole, results.get(0).getRole())) {
            throw new ForbiddenException("only admins can access admin applications");
        }
        //check if user is blocked
        Blocked block = results.get(0).getBlock();
        if (block != null) {
            throw new BlockedException("user blocked: '" + block.getReason() + "'");
        }
    }
    
    private boolean canAuthenticate(Character requiredRole, Character role) {
        boolean canAuth = true;
        
        //if non-admin logges in via admin application
        if (requiredRole != null && requiredRole.equals(UserRoles.ADMIN) && !role.equals(UserRoles.ADMIN)) {
            canAuth = false;
        }
        
        return canAuth;
    }
    
    /**
     * Retreives the authentication token from the database or issues a new one if the old one has expired
     * @param username 
     * @return authentication token
     */
    @Transactional
    public String issueToken(String username) {
        String token = getRandomToken();

        Session session = new Session(username, token);
        session.setExpirydate(DateUtils.toDate(LocalDateTime.now().plusDays(1)));
        em.persist(session);
            
        return token;
    }
    
    public void checkPermissions(String token, List<Role> roles) throws NotAuthorizedException, ForbiddenException, BlockedException {
        User user = getUserByToken(token);
        if (user == null) {
            throw new NotAuthorizedException("token '" + token + "' not valid");
        }
        //check if user is blocked
        Blocked block = user.getBlock();
        if (block != null) {
            throw new BlockedException("user blocked: '" + block.getReason() + "'");
        }
        
        if (!hasPermissions(user, roles)) {
            throw new ForbiddenException("insufficient priveliges");
        }
    }
    
    private boolean hasPermissions(User user, List<Role> roles) {
        boolean hasPerm = false;
        
        if (user.getRole().equals(UserRoles.ADMIN)) {
            hasPerm = true;
        }
        else if (user.getRole().equals(UserRoles.MODERATOR)) {
            if (!roles.contains(Role.ADMIN)) {
                hasPerm = true;
            }
        }
        else if (user.getRole().equals(UserRoles.USER)) {
            if (!(roles.contains(Role.ADMIN) || roles.contains(Role.MODERATOR))) {
                hasPerm = true;
            }
        }
        
        return hasPerm;
    }
    
    @Transactional
    public User getUserByToken(String token) {
        User u = null;
        
        TypedQuery<Session> query =
        em.createNamedQuery("Session.findByAuthkey", Session.class);
        query.setParameter("authkey", token);
        List<Session> results = query.getResultList();
        if (!results.isEmpty()) {
            Session session = results.get(0);
            if (session.getExpirydate() != null && session.getExpirydate().before(DateUtils.toDate(LocalDateTime.now()))) {
                throw new NotAuthorizedException("token expired");
            }
            u = session.getUser();
        }
        
        return u;
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
