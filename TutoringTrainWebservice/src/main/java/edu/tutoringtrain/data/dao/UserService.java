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
import edu.tutoringtrain.data.error.Language;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Blocked;
import edu.tutoringtrain.data.Gender;
import edu.tutoringtrain.data.ResettableUserProp;
import edu.tutoringtrain.data.UserRole;
import edu.tutoringtrain.data.exceptions.XMPPException;
import edu.tutoringtrain.data.search.user.UserQueryGenerator;
import edu.tutoringtrain.data.search.user.UserSearch;
import edu.tutoringtrain.entities.QUser;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.EmailUtils;
import edu.tutoringtrain.utils.ImageUtils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLIntegrityConstraintViolationException;
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
    private static final Character DEFAULT_GENDER = Gender.OTHER;
    private static final boolean IS_XMPP_ACTIVE = false;
    
    @Inject
    private XMPPService xmppService;
    
    public UserService() {
    }
    
    @Transactional(rollbackOn = XMPPException.class)
    public User registerUser(User user, ErrorBuilder errB) throws InvalidArgumentException, NullValueException, ConstraintViolationException, XMPPException, edu.tutoringtrain.data.exceptions.ConstraintViolationException {
        if (user == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        if (user.getEmail() != null && !EmailUtils.isEmailValid(user.getEmail())) {
            throw new InvalidArgumentException(new ErrorBuilder(Error.INVALID_EMAIL).withParams(user.getEmail()));
        }

        try {
            Character gender = getGenderOrDefault(user.getGender());
            user.setGender(gender);
            user.setRole(UserRole.USER.getChar());

            em.persist(user);
            em.flush();

            //only add xmpp user if registration was successful (if transaction is not marked for rollback)
            if (IS_XMPP_ACTIVE) xmppService.createUser(user);
        }
        catch (Exception ex) {
            if (ex instanceof javax.persistence.PersistenceException) {
                ErrorBuilder tmpErrB = getError((SQLIntegrityConstraintViolationException) ex.getCause().getCause(), user);
                errB.withErrorCode(tmpErrB.getErrorCode()).withParams(tmpErrB.getParams());
            }
            else if (ex instanceof XMPPException) {
                errB.withErrorCode(Error.XMPP_ERROR);
            }
            
            throw ex;
        }
        
        return user;
    }
    
    private static ErrorBuilder getError(SQLIntegrityConstraintViolationException ex, User userIn) {
        ErrorBuilder err;
        try {
            if (ex.getMessage().contains("U_USER_EMAIL")) {
                err = new ErrorBuilder(Error.EMAIL_CONFLICT).withParams(userIn.getEmail());
            }
            else if (ex.getMessage().contains("PK_TUSER")) {
                err = new ErrorBuilder(Error.USERNAME_CONFLICT).withParams(userIn.getUsername());
            }
            else {
                err = new ErrorBuilder(Error.UNKNOWN).withParams(ex.getMessage());
            }
        }
        catch (Exception e) {
            err = new ErrorBuilder(Error.UNKNOWN).withParams(e.getMessage());
        }
        
        return err;
    }
    
    @Transactional
    public void deleteUser(String username) throws InvalidArgumentException, NullValueException, ConstraintViolationException {
        em.remove(em.find(User.class, username));
    }
    
    @Transactional(rollbackOn = XMPPException.class)
    public void updateUser(User userReq, ErrorBuilder errB) throws InvalidArgumentException, NullValueException, UserNotFoundException, XMPPException {
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

        String oldPW = currentUser.getPassword();
        
        if (userReq.getEducation() != null) currentUser.setEducation(userReq.getEducation());
        if (userReq.getEmail() != null) currentUser.setEmail(userReq.getEmail());
        if (userReq.getGender() != null) currentUser.setGender(getGenderOrDefault(userReq.getGender()));
        if (userReq.getName() != null) currentUser.setName(userReq.getName());
        if (userReq.getPassword()!= null) currentUser.setPassword(userReq.getPassword());
        
        try {
            em.flush();
            if (IS_XMPP_ACTIVE) xmppService.updateUser(userReq.getUsername(), oldPW, userReq.getPassword(), userReq.getName());
        }
        catch (Exception ex) {
            if (ex instanceof javax.persistence.PersistenceException) {
                ErrorBuilder tmpErrB = getError((SQLIntegrityConstraintViolationException) ex.getCause().getCause(), currentUser);
                errB.withErrorCode(tmpErrB.getErrorCode()).withParams(tmpErrB.getParams());
            }
            else if (ex instanceof XMPPException) {
                errB.withErrorCode(Error.XMPP_ERROR);
            }
            
            throw ex;
        }
    }
    
    @Transactional
    public void setRole(String username, UserRole newRole) throws InvalidArgumentException, NullValueException, UserNotFoundException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        if (newRole == null) {
            throw new NullValueException(new ErrorBuilder(Error.UNKNOWN));
        }
        
        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }
        user.setRole(newRole.getChar());
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
        
        TypedQuery<User> query = (TypedQuery<User>) em.createNamedQuery("User.findAll");
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
    
    @Transactional(rollbackOn = XMPPException.class)
    public void setAvatar(String username, InputStream avatar, String imgType) throws UserNotFoundException, NullValueException, IOException, XMPPException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        
        User user = getUserByUsername(username);
        
        //set TT Avatar
        byte[] imageInByte = ImageUtils.getScaledImage(avatar, imgType, 360);
        user.setAvatar(imageInByte);
        
        em.flush();
        //set XMPP avatar
        if (IS_XMPP_ACTIVE) {
            imageInByte = ImageUtils.getScaledImage(imageInByte, imgType, 64);
            xmppService.setAvatarImage(user.getUsername(), user.getPassword(), imageInByte);
        }
    }
    
    @Transactional(rollbackOn = XMPPException.class)
    public void resetAvatar(String username) throws UserNotFoundException, NullValueException, XMPPException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USER_NULL));
        }
        
        User user = getUserByUsername(username);
        user.setAvatar(null);
        em.flush();
        
        if (IS_XMPP_ACTIVE) xmppService.setAvatarImage(user.getUsername(), user.getPassword(), null);
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
        List<User> users;
        
        try {
            users = query.from(QUser.user)
                .where(gen.getPredicates(searchCriteria))
                .orderBy(gen.getOrders(searchCriteria))
                .list(QUser.user);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
         
         return users;
    }
    
    @Transactional
    public List<User> search(UserSearch searchCriteria, int start, int pageSize) {
        UserQueryGenerator gen = new UserQueryGenerator();
        JPQLQuery query = new JPAQuery (em, EclipseLinkTemplates.DEFAULT);
        List<User> users;
        
        try {
            users = query.from(QUser.user)
                .where(gen.getPredicates(searchCriteria))
                .orderBy(gen.getOrders(searchCriteria))
                .offset(start)
                .limit(pageSize)
                .list(QUser.user);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
         
         return users;
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
