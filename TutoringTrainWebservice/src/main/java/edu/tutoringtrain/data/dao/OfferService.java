/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.CreateOfferRequest;
import edu.tutoringtrain.data.UpdateOfferRequest;
import edu.tutoringtrain.entities.Offer;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Elias
 */
public class OfferService extends AbstractService {
    private static OfferService instance = null;
    
    private OfferService() {
    }
    
    public static OfferService getInstance() {
        if (instance == null) {
            instance = new OfferService();
        }
        return instance;
    }
    
    /**
     * Get a list of newest orders (postedon)
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     */
    public List<Offer> getNewestOffers(int start, int pageSize) {
        List<Offer> results = new ArrayList<>();
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Offer> query = em.createNamedQuery("Offer.findNewest", Offer.class);
            query.setFirstResult(start);
            query.setMaxResults(pageSize);
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
    
    /**
     * Get a list of newest orders (postedon)
     * @param username user of whom to get offers
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     */
    public List<Offer> getNewestOffersOfUser(String username, int start, int pageSize) {
        List<Offer> results = new ArrayList<>();
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Offer> query = em.createNamedQuery("Offer.findNewestOfUser", Offer.class);
            query.setParameter("username", username);
            query.setFirstResult(start);
            query.setMaxResults(pageSize);
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
    
    public Offer createOffer(String username, CreateOfferRequest offerReq) throws NullPointerException {
        if (username == null) {
            throw new NullPointerException("name must not be null");
        }
        if (offerReq == null) {
            throw new NullPointerException("offer must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        Offer offer = new Offer();
        try {
            em.getTransaction().begin();
            offer.setDescription(offerReq.getDescription());
            offer.setSubject(SubjectService.getInstance().getSubject(offerReq.getSubject()));
            offer.setIsactive('1');
            offer.setPostedon(DateUtils.toDate(LocalDateTime.now()));
            offer.setUsername(UserService.getInstance().getUserByUsername(username));
            this.persist(offer);
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return offer;
    }
    
    public void updateOffer(String username, UpdateOfferRequest offerReq) throws NullPointerException {
        if (offerReq == null) {
            throw new NullPointerException("offer must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            
            TypedQuery<Offer> query = em.createNamedQuery("Offer.findByIdAndUsername", Offer.class);
            query.setParameter("id", offerReq.getId());
            query.setParameter("username", username);
            System.out.println(query.getParameterValue("id") + " - " + query.getParameterValue("username"));
            List<Offer> results = query.getResultList();

            if (results.isEmpty()) {
                throw new NullPointerException("offer not found");
            }
            Offer dbOffer = results.get(0);  
            if (offerReq.getDescription() != null) dbOffer.setDescription(offerReq.getDescription());
            if (offerReq.getDuedateDate() != null) dbOffer.setDuedate(offerReq.getDuedateDate());
            if (offerReq.getPostedonDate() != null) dbOffer.setPostedon(offerReq.getPostedonDate());
            if (offerReq.getIsactive() != null) dbOffer.setIsactive(offerReq.getIsactive());
            if (offerReq.getSubject() != null) dbOffer.setSubject(SubjectService.getInstance().getSubject(offerReq.getSubject()));
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
    }
}
