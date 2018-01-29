/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.exceptions.NullValueException;
import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.entities.Subject;
import java.math.BigDecimal;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.eclipse.persistence.sessions.Session;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class SubjectService extends AbstractService {
    
    public SubjectService() {
    }
    
    @Transactional
    public List<Subject> getAllSubjects() {
        List<Subject> results;
        
        TypedQuery<Subject> query = em.createNamedQuery("Subject.findAll", Subject.class);
        results = query.getResultList();

        return results;
    }
    
    @Transactional
    public List<Subject> getAllActiveSubjects() {
        List<Subject> results;
        
        TypedQuery<Subject> query = em.createNamedQuery("Subject.findAllActive", Subject.class);
        results = query.getResultList();

        return results;
    }
    
    @Transactional
    public List<Subject> getAllInactiveSubjects() {
        List<Subject> results;
        
        TypedQuery<Subject> query = em.createNamedQuery("Subject.findAllInactive", Subject.class);
        results = query.getResultList();

        return results;
    }
    
    @Transactional
    public Subject getSubject(BigDecimal id) throws SubjectNotFoundException {
        Subject s = em.find(Subject.class, id);
        if (s == null) {
            throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(id));
        }

        return s;
    }
    
    @Transactional
    public Subject createSubject(Subject s) throws NullValueException {
        if (s.getEnname() == null || s.getDename() == null) {
            throw new NullValueException(new ErrorBuilder(Error.NAME_NULL));
        }
        em.persist(s);
        
        return s;
    }
    
    @Transactional
    public void updateSubject(Subject subject) throws NullValueException, SubjectNotFoundException {
        if (subject == null) {
            throw new NullValueException(new ErrorBuilder(Error.SUBJECT_NULL));
        }
        
        Subject dbSubject = em.find(Subject.class, subject.getId());
        if (subject == null) {
            throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(subject.getId()));
        }
        if (subject.getEnname() != null) dbSubject.setEnname(subject.getEnname());
        if (subject.getDename() != null) dbSubject.setDename(subject.getDename());
        if (subject.getIsactive() != null) dbSubject.setIsactive(subject.getIsactive());
    }
    
    @Transactional
    public void removeSubject(int subjectID) throws SubjectNotFoundException {
        
        Subject subject = em.find(Subject.class, new BigDecimal(subjectID));
        if (subject == null) {
            throw new SubjectNotFoundException(new ErrorBuilder(Error.SUBJECT_NOT_FOUND).withParams(subjectID));
        }

        em.remove(subject);
    }
    
    @Transactional
    public int getCountAll() {
        return ((Number)em.createNamedQuery("Subject.countAll").getSingleResult()).intValue();
    }
}
