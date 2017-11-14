/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.ResettableOfferProp;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.OfferNotFoundException;
import edu.tutoringtrain.data.exceptions.SubjectNotActiveException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class OfferService extends AbstractService {
    @Inject
    UserService userService;
    @Inject
    SubjectService subjectService;
    
    public OfferService() {
    }
    
    /**
     * Get a list of newest orders (postedon)
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     */
    @Transactional
    public List<Entry> getNewestOffers(int start, int pageSize) {
        List<Entry> results;

        TypedQuery<Entry> query = em.createNamedQuery("Entry.findOfferNewest", Entry.class);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        results = query.getResultList();

        return results;
    }
    
    /**
     * Get a list of newest orders (postedon)
     * @param username user of whom to get offers
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     */
    @Transactional
    public List<Entry> getNewestOffersOfUser(String username, int start, int pageSize) throws UserNotFoundException {
        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }

        TypedQuery<Entry> query = em.createNamedQuery("Entry.findOfferNewestOfUser", Entry.class);
        query.setParameter("username", username);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    @Transactional
    public Entry createOffer(String username, Entry offerReq) throws NullValueException, SubjectNotFoundException, UserNotFoundException, SubjectNotActiveException {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        if (offerReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.OFFER_NULL));
        }
        
        Subject s = subjectService.getSubject(offerReq.getSubject().getId());
        if (s == null) {
            throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(offerReq.getSubject().getId()));
        }else {
            //if subject is not active
            if (!s.getIsactive().equals('1')) {
                throw new SubjectNotActiveException(new ErrorBuilder(Error.SUBJECT_NOT_ACTIVE));
            }
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }

        offerReq.setSubject(s);
        offerReq.setUser(user);
        offerReq.setIsactive('1');
        offerReq.setFlag(Entry.FLAG_OFFER);
        offerReq.setPostedon(DateUtils.toDate(LocalDateTime.now()));
        em.persist(offerReq);
        
        return offerReq;
    }
    
    @Transactional
    public Entry getOffer(BigDecimal id) throws OfferNotFoundException {
        Entry offer = em.find(Entry.class, id);
        if (offer == null) {
            throw new OfferNotFoundException(new ErrorBuilder(Error.OFFER_NOT_FOUND).withParams(id));
        }
        return offer;
    }
    
    @Transactional
    public Entry getOffer(BigDecimal id, String username) throws OfferNotFoundException {
        Entry offer = getOffer(id);
        if (offer == null || !offer.getUser().getUsername().equals(username)) {
            throw new OfferNotFoundException(new ErrorBuilder(Error.OFFER_OF_USER_NOT_FOUND).withParams(id, username));
        }
        return offer;
    }
    
    @Transactional
    public void updateOffer(Entry offerReq, String username) throws NullValueException, OfferNotFoundException, SubjectNotFoundException, InvalidArgumentException, SubjectNotActiveException {
        if (offerReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.OFFER_NULL));
        }
        if (offerReq.getIsactive() != null && offerReq.getIsactive() != '0' && offerReq.getIsactive() != '1') {
            throw new InvalidArgumentException(new ErrorBuilder(Error.CONSTRAINT_VIOLATION).withParams("isActive must be 0 or 1"));
        }
        updateProperties(getOffer(offerReq.getId(), username), offerReq);
    }
    
    @Transactional
    public void updateOffer(Entry offerReq) throws NullValueException, OfferNotFoundException, SubjectNotFoundException, InvalidArgumentException, SubjectNotActiveException {
        if (offerReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.OFFER_NULL));
        }
        if (offerReq.getIsactive() != null && offerReq.getIsactive() != '0' && offerReq.getIsactive() != '1') {
            throw new InvalidArgumentException(new ErrorBuilder(Error.CONSTRAINT_VIOLATION).withParams("isActive must be 0 or 1"));
        }
        updateProperties(getOffer(offerReq.getId()), offerReq);
    }
    
    private void updateProperties(Entry dbOffer, Entry dataOffer) throws SubjectNotFoundException, SubjectNotActiveException {
        if (dataOffer.getDescription() != null) dbOffer.setDescription(dataOffer.getDescription());
        if (dataOffer.getHeadline()!= null) dbOffer.setHeadline(dataOffer.getHeadline());
        if (dataOffer.getDuedate() != null) dbOffer.setDuedate(dataOffer.getDuedate());
        if (dataOffer.getPostedon() != null) dbOffer.setPostedon(dataOffer.getPostedon());
        if (dataOffer.getIsactive() != null) dbOffer.setIsactive(dataOffer.getIsactive());
        if (dataOffer.getSubject() != null) {
            Subject s = subjectService.getSubject(dataOffer.getSubject().getId());
            if (s == null) {
                throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(dataOffer.getSubject().getId()));
            } else {
                //if subject is not active
                if (!s.getIsactive().equals('1')) {
                    throw new SubjectNotActiveException(new ErrorBuilder(Error.SUBJECT_NOT_ACTIVE));
                }
                else {
                    dbOffer.setSubject(s);
                }
            }
        }
    }
    
    @Transactional
    public int getCountAll() {
        return ((Number)em.createNamedQuery("Entry.countOfferAll").getSingleResult()).intValue();
    }
    
    @Transactional
    public void resetProperties(BigDecimal id, ResettableOfferProp[] props) throws NullValueException, OfferNotFoundException {
        if (id == null) {
            throw new NullValueException(new ErrorBuilder(Error.OFFER_NULL));
        }
        Entry offer = getOffer(id);
        
        for (ResettableOfferProp prop: props) {
            resetProp(offer, prop);
        }
    }
    
    @Transactional
    public void resetProperties(BigDecimal id, ResettableOfferProp[] props, String username) throws NullValueException, OfferNotFoundException {
        if (id == null) {
            throw new NullValueException(new ErrorBuilder(Error.OFFER_NULL));
        }
        Entry offer = getOffer(id, username);

        for (ResettableOfferProp prop: props) {
            resetProp(offer, prop);
        }
    }
    
    @Transactional
    private void resetProp(Entry offer, ResettableOfferProp prop) {
        switch (prop) {
            case DUEDATE:
                offer.setDuedate(null);
                break;
        }
    }
}
