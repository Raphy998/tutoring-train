/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import com.mysema.query.jpa.EclipseLinkTemplates;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import edu.tutoringtrain.data.EntryType;
import edu.tutoringtrain.data.ResettableEntryProp;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.EntryNotFoundException;
import edu.tutoringtrain.data.exceptions.SubjectNotActiveException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.data.exceptions.UserNotFoundException;
import edu.tutoringtrain.data.geo.GeocodingInterface;
import edu.tutoringtrain.data.search.entry.EntryQueryGenerator;
import edu.tutoringtrain.data.search.entry.EntrySearch;
import edu.tutoringtrain.entities.Entry;
import edu.tutoringtrain.entities.QEntry;
import edu.tutoringtrain.entities.Subject;
import edu.tutoringtrain.entities.User;
import edu.tutoringtrain.utils.DateUtils;
import java.math.BigDecimal;
import java.text.ParseException;
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
public class EntryService extends AbstractService {
    @Inject
    UserService userService;
    @Inject
    SubjectService subjectService;
    
    public EntryService() {
    }
    
    /**
     * Get a list of newest orders (postedon)
     * @param type Type of entries to get (Offer / Request)
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     */
    @Transactional
    public List<Entry> getNewestEntries(EntryType type, int start, int pageSize) {
        List<Entry> results;
        TypedQuery<Entry> query;
        if (type == EntryType.OFFER) query = em.createNamedQuery("Entry.findOfferNewest", Entry.class);
        else query = em.createNamedQuery("Entry.findRequestNewest", Entry.class);
        
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        results = query.getResultList();

        return results;
    }
    
    /**
     * Get a list of newest orders (postedon)
     * @param type Type of entries to get (Offer / Request)
     * @param username user of whom to get offers
     * @param start start of offer selection (beginning at 0)
     * @param pageSize maximum number of results returned
     * @return 
     * @throws edu.tutoringtrain.data.exceptions.UserNotFoundException 
     */
    @Transactional
    public List<Entry> getNewestEntiresOfUser(EntryType type, String username, int start, int pageSize) throws UserNotFoundException {
        User user = em.find(User.class, username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }
        
        TypedQuery<Entry> query;
        if (type == EntryType.OFFER) query = em.createNamedQuery("Entry.findOfferNewestOfUser", Entry.class);
        else query = em.createNamedQuery("Entry.findRequestNewestOfUser", Entry.class);
        
        query.setParameter("username", username);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    @Transactional
    public Entry createEntry(EntryType type, String username, Entry e) throws NullValueException, SubjectNotFoundException, UserNotFoundException, SubjectNotActiveException, Exception {
        if (username == null) {
            throw new NullValueException(new ErrorBuilder(Error.USERNAME_NULL));
        }
        if (e == null) {
            throw new NullValueException(new ErrorBuilder(Error.ENTRY_NULL));
        }
        
        Subject s = subjectService.getSubject(e.getSubject().getId());
        if (s == null) {
            throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(e.getSubject().getId()));
        }
        else {
            //if subject is not active
            if (!s.getIsactive().equals('1')) {
                throw new SubjectNotActiveException(new ErrorBuilder(Error.SUBJECT_NOT_ACTIVE));
            }
        }
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(new ErrorBuilder(Error.USER_NOT_FOUND).withParams(username));
        }

        try {
            e.setSubject(s);
            e.setUser(user);
            e.setIsactive('1');
            e.setFlag(type.getChar());
            e.setPostedon(DateUtils.toDate(LocalDateTime.now()));
            if (e.getLocation() != null) {
                e.setLocationName(GeocodingInterface.getLocationName(e.getLocation()));
            }
            em.persist(e);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        
        return e;
    }
    
    @Transactional
    public Entry getEntry(EntryType type, BigDecimal id) throws EntryNotFoundException {
        Entry e = em.find(Entry.class, id);
        if (e == null || e.getFlag() != type.getChar() && type == EntryType.OFFER) {
            throw new EntryNotFoundException(new ErrorBuilder(Error.OFFER_NOT_FOUND).withParams(id));
        }
        else if (e == null || e.getFlag() != type.getChar() && type == EntryType.REQUEST) {
            throw new EntryNotFoundException(new ErrorBuilder(Error.REQUEST_NOT_FOUND).withParams(id));
        }
        return e;
    }
    
    @Transactional
    public Entry getEntry(EntryType type, BigDecimal id, String username) throws EntryNotFoundException {
        Entry e = getEntry(type, id);
        if (e == null || !e.getUser().getUsername().equals(username) && type == EntryType.OFFER) {
            throw new EntryNotFoundException(new ErrorBuilder(Error.OFFER_OF_USER_NOT_FOUND).withParams(id, username));
        }
        else if (e == null || !e.getUser().getUsername().equals(username) && type == EntryType.REQUEST) {
            throw new EntryNotFoundException(new ErrorBuilder(Error.REQUEST_OF_USER_NOT_FOUND).withParams(id, username));
        }
        return e;
    }
    
    @Transactional
    public void deleteEntry(EntryType type, BigDecimal id) throws EntryNotFoundException {
        Entry e = getEntry(type, id);
        em.remove(e);
    }
    
    @Transactional
    public void deleteEntry(EntryType type, BigDecimal id, String username) throws EntryNotFoundException {
        Entry e = getEntry(type, id);
        
        if (!e.getUser().getUsername().equals(username)) {
            if (type == EntryType.OFFER)
                throw new EntryNotFoundException(new ErrorBuilder(Error.OFFER_OF_USER_NOT_FOUND).withParams(id, username));
            else 
                throw new EntryNotFoundException(new ErrorBuilder(Error.REQUEST_OF_USER_NOT_FOUND).withParams(id, username));
        }
        
        em.remove(e);
    }
    
    @Transactional
    public void updateEntry(EntryType type, Entry entryReq, String username) throws NullValueException, EntryNotFoundException, SubjectNotFoundException, InvalidArgumentException, SubjectNotActiveException, Exception {
        if (entryReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.ENTRY_NULL));
        }
        if (entryReq.getIsactive() != null && entryReq.getIsactive() != '0' && entryReq.getIsactive() != '1') {
            throw new InvalidArgumentException(new ErrorBuilder(Error.CONSTRAINT_VIOLATION).withParams("isActive must be 0 or 1"));
        }
        updateProperties(getEntry(type, entryReq.getId(), username), entryReq);
    }
    
    @Transactional
    public void updateEntry(EntryType type, Entry entryReq) throws NullValueException, EntryNotFoundException, SubjectNotFoundException, InvalidArgumentException, SubjectNotActiveException, Exception {
        if (entryReq == null) {
            throw new NullValueException(new ErrorBuilder(Error.ENTRY_NULL));
        }
        if (entryReq.getIsactive() != null && entryReq.getIsactive() != '0' && entryReq.getIsactive() != '1') {
            throw new InvalidArgumentException(new ErrorBuilder(Error.CONSTRAINT_VIOLATION).withParams("isActive must be 0 or 1"));
        }
        try {
        updateProperties(getEntry(type, entryReq.getId()), entryReq);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    private void updateProperties(Entry dbEntry, Entry dataEntry) throws SubjectNotFoundException, SubjectNotActiveException, Exception {
        if (dataEntry.getDescription() != null) dbEntry.setDescription(dataEntry.getDescription());
        if (dataEntry.getHeadline()!= null) dbEntry.setHeadline(dataEntry.getHeadline());
        if (dataEntry.getDuedate() != null) dbEntry.setDuedate(dataEntry.getDuedate());
        if (dataEntry.getPostedon() != null) dbEntry.setPostedon(dataEntry.getPostedon());
        if (dataEntry.getIsactive() != null) dbEntry.setIsactive(dataEntry.getIsactive());
        if (dataEntry.getLocation()!= null) dbEntry.setLocation(dataEntry.getLocation());
        if (dataEntry.getLocation() != null) {
                dbEntry.setLocationName(GeocodingInterface.getLocationName(dataEntry.getLocation()));
            }
        if (dataEntry.getSubject() != null) {
            Subject s = subjectService.getSubject(dataEntry.getSubject().getId());
            if (s == null) {
                throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(dataEntry.getSubject().getId()));
            } else {
                //if subject is not active
                if (!s.getIsactive().equals('1')) {
                    throw new SubjectNotActiveException(new ErrorBuilder(Error.SUBJECT_NOT_ACTIVE));
                }
                else {
                    dbEntry.setSubject(s);
                }
            }
        }
    }
    
    @Transactional
    public int getCountAll(EntryType type) {
        int count;
        if (type == EntryType.OFFER) count = ((Number)em.createNamedQuery("Entry.countOfferAll").getSingleResult()).intValue();
        else count = ((Number)em.createNamedQuery("Entry.countRequestAll").getSingleResult()).intValue();
         
         return count;
    }
    
    @Transactional
    public void resetProperties(EntryType type, BigDecimal id, ResettableEntryProp[] props) throws NullValueException, EntryNotFoundException {
        if (id == null) {
            throw new NullValueException(new ErrorBuilder(Error.ENTRY_NULL));
        }
        Entry e = getEntry(type, id);
        
        for (ResettableEntryProp prop: props) {
            resetProp(e, prop);
        }
    }
    
    @Transactional
    public void resetProperties(EntryType type, BigDecimal id, ResettableEntryProp[] props, String username) throws NullValueException, EntryNotFoundException {
        if (id == null) {
            throw new NullValueException(new ErrorBuilder(Error.ENTRY_NULL));
        }
        Entry e = getEntry(type, id, username);

        for (ResettableEntryProp prop: props) {
            resetProp(e, prop);
        }
    }
    
    @Transactional
    private void resetProp(Entry offer, ResettableEntryProp prop) {
        switch (prop) {
            case DUEDATE:
                offer.setDuedate(null);
                break;
            case LOCATION:
                offer.setLocation(null);
                break;
        }
    }
    
    @Transactional
    public List<Entry> search(EntryType type, EntrySearch searchCriteria) throws ParseException {
        EntryQueryGenerator gen = new EntryQueryGenerator();
        JPQLQuery query = new JPAQuery (em, EclipseLinkTemplates.DEFAULT);
        List<Entry> e = null;

        try {
            e = query.from(QEntry.entry)
                .where(gen.getPredicates(searchCriteria))
                .where(QEntry.entry.flag.eq(type.getChar()))
                .orderBy(gen.getOrders(searchCriteria))
                .list(QEntry.entry);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        return e;
    }
    
    @Transactional
    public List<Entry> search(EntryType type, EntrySearch searchCriteria, int start, int pageSize) throws ParseException {
        EntryQueryGenerator gen = new EntryQueryGenerator();
        JPQLQuery query = new JPAQuery (em, EclipseLinkTemplates.DEFAULT);
        
        return query.from(QEntry.entry)
            .where(gen.getPredicates(searchCriteria))
            .where(QEntry.entry.flag.eq(type.getChar()))
            .orderBy(gen.getOrders(searchCriteria))
            .offset(start)
            .limit(pageSize)
            .list(QEntry.entry);
    }
}