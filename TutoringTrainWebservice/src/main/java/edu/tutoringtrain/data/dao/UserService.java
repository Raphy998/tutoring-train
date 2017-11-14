/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import com.mysema.query.jpa.EclipseLinkTemplates;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.UserRoles;
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.data.Gender;
import edu.tutoringtrain.data.ResettableUserProp;
import edu.tutoringtrain.data.search.CharacterOperation;
import edu.tutoringtrain.data.search.CharacterSearchCriteria;
import edu.tutoringtrain.data.search.OrderDirection;
import edu.tutoringtrain.data.search.OrderElement;
import edu.tutoringtrain.data.search.StringOperation;
import edu.tutoringtrain.data.search.StringSearchCriteria;
import edu.tutoringtrain.data.search.UserProp;
import edu.tutoringtrain.data.search.UserQueryGenerator;
import edu.tutoringtrain.data.search.UserSearch;
import edu.tutoringtrain.entities.QUser;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.EmailUtils;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
/**
 *
 * @author Elias
 */
@ApplicationScoped
public class UserService extends AbstractService {
    private static final Character DEFAULT_GENDER = Gender.OTHER;
    
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
        
        Character gender = getGenderOrDefault(userReq.getGender());
        userReq.setGender(gender);
        userReq.setRole(UserRoles.USER);
        
        em.persist(userReq);
        
        return userReq;
    }
    
    @Transactional
    public void updateUser(User userReq) throws InvalidArgumentException, NullValueException, UserNotFoundException {
        if (userReq == null || userReq.getUsername() == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        if (userReq.getEmail() != null && !EmailUtils.isEmailValid(userReq.getEmail())) {
            throw new InvalidArgumentException(new ErrorBuilder(Error.INVALID_EMAIL).withParams(userReq.getEmail()));
        }
        
        User currentUser = em.find(User.class, userReq.getUsername());
        if (currentUser == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(userReq.getUsername()));
        }

        if (userReq.getEducation() != null) currentUser.setEducation(userReq.getEducation());
        if (userReq.getEmail() != null) currentUser.setEmail(userReq.getEmail());
        if (userReq.getGender() != null) currentUser.setGender(getGenderOrDefault(userReq.getGender()));
        if (userReq.getName() != null) currentUser.setName(userReq.getName());
        if (userReq.getPassword()!= null) currentUser.setPassword(userReq.getPassword());
    }
    
    @Transactional
    public void resetProperties(String username, ResettableUserProp[] props) throws NullValueException, UserNotFoundException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }
        
        for (ResettableUserProp prop: props) {
            resetProp(user, prop);
        }
    }
    
    @Transactional
    private void resetProp(User user, ResettableUserProp prop) {
        switch (prop) {
            case NAME:
                user.setName(null);
                break;
            case EDUCATION:
                user.setEducation(null);
                break;
        }
    }
    
    @Transactional
    private Character getGenderOrDefault(Character g) throws InvalidArgumentException  {
        if (g != null && !Gender.isValidGender(g)) {
            throw new InvalidArgumentException(new ErrorBuilder(Error.GENDER_NOT_FOUND).withParams(g));
        }
        else {
            if (g == null) {
                g = DEFAULT_GENDER;
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
    
    public List<Gender> getAllGenders(Language lang) {
        return Gender.getAll(lang);
    }
    
    @Transactional
    public int getCountAll() {
        return ((Number)em.createNamedQuery("User.countAll").getSingleResult()).intValue();
    }
    
    @Transactional
    public void setAvatar(String username, byte[] avatar) throws UserNotFoundException, NullValueException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        
        User user = getUserByUsername(username);
        user.setAvatar(avatar);
    }
    
    @Transactional
    public void resetAvatar(String username) throws UserNotFoundException, NullValueException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        
        User user = getUserByUsername(username);
        user.setAvatar(null);
    }
    
    @Transactional
    public byte[] getAvatar(String username) throws UserNotFoundException, NullValueException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        
        User user = getUserByUsername(username);
        return (byte[]) user.getAvatar();
    }
    
    @Transactional
    public List<User> search(UserSearch searchCriteria) {
        UserQueryGenerator gen = new UserQueryGenerator();
        JPQLQuery query = new JPAQuery (em, EclipseLinkTemplates.DEFAULT);
        
        return query.from(QUser.user)
            .where(gen.getPredicates(searchCriteria))
            .orderBy(gen.getOrders(searchCriteria))
            .list(QUser.user);
    }
    
    @Transactional
    public List<User> search(UserSearch searchCriteria, int start, int pageSize) {
        UserQueryGenerator gen = new UserQueryGenerator();
        JPQLQuery query = new JPAQuery (em, EclipseLinkTemplates.DEFAULT);
        
        return query.from(QUser.user)
            .where(gen.getPredicates(searchCriteria))
            .orderBy(gen.getOrders(searchCriteria))
            .offset(start)
            .limit(pageSize)
            .list(QUser.user);
    }
    
    public UserSearch test() {
        UserSearch us = new UserSearch();
        us.getCriteria().add(new StringSearchCriteria<>(UserProp.USERNAME, StringOperation.CONTAINS, "Adm", true));
        us.getCriteria().add(new StringSearchCriteria<>(UserProp.USERNAME, StringOperation.ENDS_WITH, "in", true, true));
        us.getCriteria().add(new CharacterSearchCriteria<>(UserProp.GENDER, CharacterOperation.EQ, 'U', false, true));
        us.getOrder().add(new OrderElement<>(UserProp.USERNAME, OrderDirection.ASC));
        
        return us;
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
