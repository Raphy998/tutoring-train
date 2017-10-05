/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.Gender;
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
    
    public User registerUser(User userReq) throws IllegalArgumentException, NullPointerException {
        if (userReq == null) {
            throw new NullPointerException("user must not be null");
        }
        if (!EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new IllegalArgumentException("email has invalid format");
        }
        
        //get gender for user
        Gender gender = getGenderOrDefault(userReq.getGender().getId());
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            userReq.setGender(gender);
            userReq.setRole(UserRoles.USER);
            this.persist(userReq);
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return userReq;
        //TODO: send verification email
    }
    
    public void updateUser(User userReq) throws IllegalArgumentException, NullPointerException, UserNotFoundException {
        if (userReq == null || userReq.getUsername() == null) {
            throw new NullPointerException("user must not be null");
        }
        if (!EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new IllegalArgumentException("email has invalid format");
        }
        
        //get gender for user
        Gender gender = getGenderOrDefault(userReq.getGender().getId());
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            em.getTransaction().begin();
            User currentUser = em.find(User.class, userReq.getUsername());
            if (currentUser == null) {
                throw new UserNotFoundException("user not found");
            }
            
            if (userReq.getEducation() != null) currentUser.setEducation(userReq.getEducation());
            if (userReq.getEmail() != null) currentUser.setEmail(userReq.getEmail());
            if (gender != null) currentUser.setGender(gender);
            if (userReq.getName() != null) currentUser.setName(userReq.getName());
            if (userReq.getPassword()!= null) currentUser.setPassword(userReq.getPassword());

            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }

        //TODO: send verification email
    }
    
    private Gender getGenderOrDefault(BigDecimal id) {
        Gender gender;
        try {
            gender = getGenderById(id);
        }
        catch (NullPointerException ex) {
            try {
                gender = getGenderById(DEFAULT_GENDER);
            }
            catch (NullPointerException npex) {
                gender = null;
            }
        }
        
        return gender;
    } 
    
    public User getUserByUsername(String username) throws NullPointerException, UserNotFoundException {
        if (username == null) {
            throw new NullPointerException("username must not be null");
        }
        openEmf();
        EntityManager em = emf.createEntityManager();
        User user;
        
        try {
            user = em.find(User.class, username);
            if (user == null) {
                throw new UserNotFoundException("user not found");
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
    public void blockUser(Blocked block, boolean isBlock) throws IllegalArgumentException, NullPointerException {
        if (block.getUsername() == null) {
            throw new NullPointerException("username must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            User user = em.find(User.class, block.getUsername());
            if (user == null) {
                throw new NullPointerException("user not found");
            }
            em.getTransaction().begin();
            Blocked oldBlock = getBlockOfUser(block.getUsername(), em);
            
            if (oldBlock != null && !isBlock) {
                em.remove(oldBlock);
            }
            else if (oldBlock == null && isBlock) {
                em.persist(block);
            }
            else if (oldBlock != null && isBlock) {
                oldBlock.setReason(block.getReason());
                oldBlock.setDuedate(block.getDuedate());
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
