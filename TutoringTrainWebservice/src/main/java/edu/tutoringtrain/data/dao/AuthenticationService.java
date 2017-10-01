/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.entities.Blocked;
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
    
    public void authenticate(String username, String password) throws Exception {
        Blocked blockOfUser = UserService.getInstance().getBlockOfUser(username);
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            TypedQuery<User> query =
            em.createNamedQuery("User.findByUsernameAndPassword", User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            List<User> results = query.getResultList();
           
            //System.out.println(results.get(0).getBlock());
            
            if (results.isEmpty()) {
                throw new Exception("authentication failed");
            }
            
            if (blockOfUser != null) {
                throw new Exception("user blocked: '" + blockOfUser.getReason() + "'");
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
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
            
            User user = em.find(User.class, username);
            //if user has no token or it's expired, issue new one, else take old one
            if (user.getAuthkey() != null && user.getAuthexpirydate().after(DateUtils.toDate(LocalDateTime.now()))) {
                token = user.getAuthkey();
            }
            else {
                user.setAuthkey(token);
                user.setAuthexpirydate(DateUtils.toDate(LocalDateTime.now().plusDays(7)));
            }
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
    
    public void checkPermissions(String token, List<Role> roles) throws NotAuthorizedException, ForbiddenException {
        User user = getUserByToken(token);
        if (user == null) {
            throw new NotAuthorizedException("token '" + token + "' not valid");
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
            TypedQuery<User> query =
            em.createNamedQuery("User.findByAuthkey", User.class);
            query.setParameter("authkey", token);
            List<User> results = query.getResultList();
            if (!results.isEmpty()) {
                u = results.get(0);
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
