/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.CalledFrom;
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
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

/**
 *
 * @author Elias
 */
public class AuthenticationService extends AbstractService {
    private static AuthenticationService instance = null;
    
    private AuthenticationService() {
    }
    
    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    public void authenticate(String username, String password, String calledFrom) throws NotAuthorizedException, ForbiddenException, BlockedException {
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<User> query =
            em.createNamedQuery("User.findByUsernameAndPassword", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            List<User> results = query.getResultList();
            
            if (results.isEmpty()) {
                throw new NotAuthorizedException("authentication failed");
            }
            
            if (!canAuthenticate(calledFrom, results.get(0).getRole())) {
                throw new ForbiddenException("only admins can access admin applications");
            }
            //check if user is blocked
            Blocked block = results.get(0).getBlock();
            if (block != null) {
                throw new BlockedException("user blocked: '" + block.getReason() + "'");
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
    }
    
    private boolean canAuthenticate(String calledFrom, Character role) {
        boolean canAuth = true;
        
        //if non-admin logges in via admin application
        if (calledFrom != null && calledFrom.equals(CalledFrom.ADMIN_APPL) && !role.equals(UserRoles.ADMIN)) {
            canAuth = false;
        }
        
        return canAuth;
    }
    
    /**
     * Retreives the authentication token from the database or issues a new one if the old one has expired
     * @param username 
     * @return authentication token
     */
    public String issueToken(String username) {
        String token = getRandomToken();
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            //persisting entity
            em.getTransaction().begin();
            
            Session session = new Session(username, token);
            session.setExpirydate(DateUtils.toDate(LocalDateTime.now().plusDays(1)));
            em.persist(session);
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
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
    
    public User getUserByToken(String token) {
        openEmf();
        EntityManager em = emf.createEntityManager();
        User u = null;
        
        try {
            TypedQuery<Session> query =
            em.createNamedQuery("Session.findByAuthkey", Session.class);
            query.setParameter("authkey", token);
            List<Session> results = query.getResultList();
            if (!results.isEmpty()) {
                Session session = results.get(0);
                if (session.getExpirydate() != null && session.getExpirydate().before(DateUtils.toDate(LocalDateTime.now()))) {
                    throw new NotAuthorizedException("token expired");
                    //TODO (maybe): remove old token
                }
                u = session.getUser();
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
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
