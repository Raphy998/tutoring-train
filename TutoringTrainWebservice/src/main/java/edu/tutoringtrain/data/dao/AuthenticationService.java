/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.Role;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.exceptions.BlockedException;
import edu.tutoringtrain.data.exceptions.ForbiddenException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.UnauthorizedException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
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
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class AuthenticationService extends AbstractService {
    @Inject
    UserService userService;
    
    public AuthenticationService() {
    }
    
    @Transactional(dontRollbackOn = Exception.class)
    public void authenticate(String username, String password, Character requiredRole) throws UnauthorizedException, ForbiddenException, BlockedException {
        TypedQuery<User> query =
        em.createNamedQuery("User.findByUsernameAndPassword", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<User> results = query.getResultList();
        
        if (results.isEmpty()) {
            throw new UnauthorizedException(new ErrorBuilder(Error.AUTH_FAILED));
        }

        if (!canAuthenticate(requiredRole, results.get(0).getRole())) {
            throw new ForbiddenException(new ErrorBuilder(Error.ADMIN_ONLY));
        }
        //check if user is blocked
        Blocked block = results.get(0).getBlock();
        if (block != null) {
            throwBlockedException(block);
        }
    }
    
    private void throwBlockedException(Blocked block) throws BlockedException {
        int errCode;
        Object[] params;

        if (block.getReason() != null && block.getDuedate() != null) {
            errCode = Error.BLOCKED;
            params = new Object[] {block.getReason(), block.getDuedate()};
        }
        else if (block.getReason() == null && block.getDuedate() != null) {
            errCode = Error.BLOCKED_NO_REASON;
            params = new Object[] {block.getDuedate()};
        }
        else if (block.getReason() != null && block.getDuedate() == null) {
            errCode = Error.BLOCKED_NO_DUEDATE;
            params = new Object[] {block.getReason()};
        }
        else {
            errCode = Error.BLOCKED_NO_PARAMS;
            params = new Object[0];
        }

        throw new BlockedException(new ErrorBuilder(errCode).withParams(params));
    }
    
    public boolean canAuthenticate(Character requiredRole, Character role) {
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
    public String issueToken(String username) throws UserNotFoundException, NullValueException {
        String token = getRandomToken();
        boolean persisted = false;
        
        while (!persisted) {
            try {
                insertToken(username, token);
                persisted = true;
            }
            catch (Exception ex) {
                //continue loop
            }
        }
        
            
        return token;
    }
    
    @Transactional
    private void insertToken(String username, String token) throws NullValueException, UserNotFoundException {
        User user = userService.getUserByUsername(username);

        Session session = new Session(token);
        session.setUser(user);
        session.setExpirydate(DateUtils.toDate(LocalDateTime.now().plusMonths(1)));
        em.persist(session);
    }
    
    @Transactional
    public void checkPermissions(String token, List<Role> roles) throws UnauthorizedException, ForbiddenException, BlockedException {
        User user = getUserByToken(token);
        if (user == null) {
            throw new UnauthorizedException(new ErrorBuilder(Error.TOKEN_INVALID).withParams(token));
        }
        
        //check if user is blocked
        Blocked block = user.getBlock();
        if (block != null) {
            throwBlockedException(block);
        }
        
        if (!hasPermissions(user, roles)) {
            throw new ForbiddenException(new ErrorBuilder(Error.INSUFF_PRIV));
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
    
    @Transactional(dontRollbackOn = UnauthorizedException.class)
    public User getUserByToken(String token) throws UnauthorizedException {
        User u = null;
        
        TypedQuery<Session> query =
        em.createNamedQuery("Session.findByAuthkey", Session.class);
        query.setParameter("authkey", token);
        
        List<Session> results = query.getResultList();
        if (!results.isEmpty()) {
            Session session = results.get(0);
            if (session.getExpirydate() != null && session.getExpirydate().before(DateUtils.toDate(LocalDateTime.now()))) {
                throw new UnauthorizedException(new ErrorBuilder(Error.TOKEN_EXPIRED).withParams(token));
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
