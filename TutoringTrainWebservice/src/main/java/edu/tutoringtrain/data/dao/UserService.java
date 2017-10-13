/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.entities.Gender;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.EmailUtils;
import java.math.BigDecimal;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
/**
 *
 * @author Elias
 */
@ApplicationScoped
public class UserService extends AbstractService {
    private static final BigDecimal DEFAULT_GENDER = new BigDecimal(1);
    
    public UserService() {
        
    }
    
    @Transactional
    public User registerUser(User userReq) throws InvalidArgumentException, NullValueException, ConstraintViolationException {
        if (userReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        if (userReq.getEmail() != null && !EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new InvalidArgumentException(new ErrorBuilder(Error.INVALID_EMAIL).withParams(userReq.getEmail()));
        }
        
        Gender gender = getGenderOrDefault(userReq.getGender());
        userReq.setGender(gender);
        userReq.setRole(UserRoles.USER);
        
        em.persist(userReq);
        
        return userReq;
        //TODO: send verification email
    }
    
    @Transactional
    public void updateUser(User userReq) throws InvalidArgumentException, NullValueException, UserNotFoundException {
        if (userReq == null || userReq.getUsername() == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        if (userReq.getEmail() != null && !EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new InvalidArgumentException(new ErrorBuilder(Error.INVALID_EMAIL).withParams(userReq.getEmail()));
        }
        
        //get gender for user
        Gender gender = getGenderOrDefault(userReq.getGender());
        
        User currentUser = em.find(User.class, userReq.getUsername());
        if (currentUser == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(userReq.getUsername()));
        }

        if (userReq.getEducation() != null) currentUser.setEducation(userReq.getEducation());
        if (userReq.getEmail() != null) currentUser.setEmail(userReq.getEmail());
        if (gender != null) currentUser.setGender(gender);
        if (userReq.getName() != null) currentUser.setName(userReq.getName());
        if (userReq.getPassword()!= null) currentUser.setPassword(userReq.getPassword());

        //TODO: send verification email
    }
    
    @Transactional
    private Gender getGenderOrDefault(Gender g)  {
        try {
            g = getGenderById(g.getId());
        }
        catch (NullPointerException | NullValueException ex) {
            try {
                g = getGenderById(DEFAULT_GENDER);
            }
            catch (NullPointerException | NullValueException npex) {
                g = null;
            }
        }
        
        return g;
    } 
    
    @Transactional
    public User getUserByUsername(String username) throws NullValueException, UserNotFoundException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }

        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }
        
        return user;
    }
    
    @Transactional
    public List<User> getUsers() {
        return getUsers(0, Integer.MAX_VALUE);
    }
    
    @Transactional
    public List<User> getUsers(int start, int pageSize) {
        List<User> users;
        
        TypedQuery<User> query = em.createNamedQuery("User.findAll", User.class);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        users = query.getResultList();
        
        return users;
    }
    
    @Transactional
    public Blocked getBlockOfUser(String username) throws NullValueException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        
        return em.find(Blocked.class, username);
    }
    
    //TODO: implement duedate
    @Transactional
    public void blockUser(Blocked block, boolean isBlock) throws InvalidArgumentException, NullValueException {
        if (block.getUsername() == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        
        User user = em.find(User.class, block.getUsername());
        if (user == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(block.getUsername()));
        }
        Blocked oldBlock = getBlockOfUser(block.getUsername());

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
    }
    
    @Transactional
    public List<Gender> getAllGenders() {
        List<Gender> results;
        
        TypedQuery<Gender> query = em.createNamedQuery("Gender.findAll", Gender.class);
        results = query.getResultList();
        
        return results;
    }
    
    @Transactional
    public Gender getGenderById(BigDecimal id) throws NullValueException {
        Gender result = null;
        
        if (id == null) {
            throw new NullValueException(new ErrorBuilder(Error.GENDER_NULL));
        }
        
        result = em.find(Gender.class, id);
        if (result == null) {
            throw new NullValueException(new ErrorBuilder(Error.GENDER_NOT_FOUND).withParams(id));
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
