/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.exceptions.OfferNotFoundException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.entities.Offer;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.DateUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public List<Offer> getNewestOffers(int start, int pageSize) {
        List<Offer> results;

        TypedQuery<Offer> query = em.createNamedQuery("Offer.findNewest", Offer.class);
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
    public List<Offer> getNewestOffersOfUser(String username, int start, int pageSize) throws UserNotFoundException {
        List<Offer> results;
        
        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException("user not found");
        }

        TypedQuery<Offer> query = em.createNamedQuery("Offer.findNewestOfUser", Offer.class);
        query.setParameter("username", username);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        results = query.getResultList();

        return results;
    }
    
    @Transactional
    public Offer createOffer(String username, Offer offerReq) throws NullPointerException, SubjectNotFoundException, UserNotFoundException {
        if (username == null) {
            throw new NullPointerException("name must not be null");
        }
        if (offerReq == null) {
            throw new NullPointerException("offer must not be null");
        }
        
        Subject s = subjectService.getSubject(offerReq.getSubject().getId());
        if (s == null) {
            throw new SubjectNotFoundException("subject not found");
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("user not found");
        }

        offerReq.setSubject(s);
        offerReq.setUser(user);
        offerReq.setIsactive('1');
        offerReq.setPostedon(DateUtils.toDate(LocalDateTime.now()));
        em.persist(offerReq);
        
        return offerReq;
    }
    
    @Transactional
    public void updateOffer(String username, Offer offerReq) throws NullPointerException, OfferNotFoundException, SubjectNotFoundException {
        if (offerReq == null) {
            throw new NullPointerException("offer must not be null");
        }
        
        TypedQuery<Offer> query = em.createNamedQuery("Offer.findByIdAndUsername", Offer.class);
        query.setParameter("id", offerReq.getId());
        query.setParameter("username", username);
        List<Offer> results = query.getResultList();

        if (results.isEmpty()) {
            throw new OfferNotFoundException("offer not found");
        }
        Offer dbOffer = results.get(0);  
        if (offerReq.getDescription() != null) dbOffer.setDescription(offerReq.getDescription());
        if (offerReq.getDuedate() != null) dbOffer.setDuedate(offerReq.getDuedate());
        if (offerReq.getPostedon() != null) dbOffer.setPostedon(offerReq.getPostedon());
        if (offerReq.getIsactive() != null) dbOffer.setIsactive(offerReq.getIsactive());
        if (offerReq.getSubject() != null) dbOffer.setSubject(subjectService.getSubject(offerReq.getSubject().getId()));
    }
}
