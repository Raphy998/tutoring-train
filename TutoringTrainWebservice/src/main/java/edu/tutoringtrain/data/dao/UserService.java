/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.RegisterUserRequest;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.Gender;
import edu.tutoringtrain.entities.Offer;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.EmailUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Elias
 */
public class UserService extends AbstractService {
    private static final BigDecimal DEFAULT_GENDER = new BigDecimal(1);
    private static UserService instance = null;
    
    private UserService() {
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public User registerUser(RegisterUserRequest userReq) throws IllegalArgumentException, NullPointerException {
        if (userReq == null) {
            throw new NullPointerException("user must not be null");
        }
        if (!EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new IllegalArgumentException("email has invalid format");
        }
        
        //get gender for user
        Gender gender;
        try {
            gender = getGenderById(userReq.getGender());
        }
        catch (NullPointerException ex) {
            try {
                gender = getGenderById(DEFAULT_GENDER);
            }
            catch (NullPointerException iex) {
                gender = null;
            }
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        User user = new User();
        
        try {
            em.getTransaction().begin();
            user.setName(userReq.getName());
            user.setUsername(userReq.getUsername());
            user.setEmail(userReq.getEmail());
            user.setPassword(userReq.getPassword());
            user.setGender(gender);
            user.setRole(UserRoles.USER);
            this.persist(user);
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return user;
        //TODO: send verification email
    }
    
    public User getUserByUsername(String username) throws IllegalArgumentException, NullPointerException {
        if (username == null) {
            throw new NullPointerException("username must not be null");
        }
        openEmf();
        EntityManager em = emf.createEntityManager();
        User user;
        
        try {
            user = em.find(User.class, username);
            if (user == null) {
                throw new NullPointerException("user not found");
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return user;
    }
    
    public List<User> getUsers() {
        return getUsers(0, Integer.MAX_VALUE);
    }
    
    public List<User> getUsers(int start, int pageSize) {
        openEmf();
        EntityManager em = emf.createEntityManager();
        List<User> users;
        
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);
            query.setFirstResult(start);
            query.setMaxResults(pageSize);
            users = query.getResultList();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return users;
    }
    
    public Blocked getBlockOfUser(String username) {
        Blocked block;
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            block = getBlockOfUser(username, em);
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return block;
    }
    
    public Blocked getBlockOfUser(String username, EntityManager em) {
        if (username == null) {
            throw new NullPointerException("username must not be null");
        }
        
        Blocked block = em.find(Blocked.class, username);
        
        return block;
    }
    
    //TODO: implement duedate
    public void lockUser(String username, boolean lock, String reason) throws IllegalArgumentException, NullPointerException {
        if (username == null) {
            throw new NullPointerException("username must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            User user = em.find(User.class, username);
            if (user == null) {
                throw new NullPointerException("user not found");
            }
            em.getTransaction().begin();
            Blocked block = getBlockOfUser(username, em);
            
            if (block != null && !lock) {
                em.remove(block);
            }
            else if (block == null && lock) {
                block = new Blocked(username);
                block.setReason(reason);
                em.persist(block);
            }
            else if (block != null && lock) {
                block.setReason(reason);
            }
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
    }
    
    
    public List<Gender> getAllGenders() {
        List<Gender> results = new ArrayList<>();
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Gender> query = em.createNamedQuery("Gender.findAll", Gender.class);
            results = query.getResultList();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return results;
    }
    
    public Gender getGenderById(BigDecimal id) throws NullPointerException {
        Gender result = null;
        
        if (id == null) {
            throw new NullPointerException("id must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            result = em.find(Gender.class, id);
            if (result == null) {
                throw new NullPointerException("gender not found");
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return result;
    }
    
    /*
    public boolean sendRegisterVerificationEmail(User user) {
        boolean success = true;
        String key = getRandomRegistrationKey();
        try {
            EmailService.getInstance().sendVerificationEmail(user, key);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            success = false;
        }
        
        return success;
    }
    
    public boolean verifyUserAccount(String key) {
        boolean retVal = false;
        if (user != null) {
            retVal = true;
            System.out.println("Verified " + user);
        }
        else {
            System.out.println("No user found for key " + key);
        }
        
        return retVal;
    }
    
    private String getRandomRegistrationKey() {
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
    }*/
}
