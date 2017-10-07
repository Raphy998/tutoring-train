/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.exceptions.SubjectNotFoundException;
import edu.tutoringtrain.entities.Subject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

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
        List<Subject> results = new ArrayList<>();
        
        TypedQuery<Subject> query =
        em.createNamedQuery("Subject.findAll", Subject.class);
        results = query.getResultList();

        return results;
    }
    
    @Transactional
    public Subject getSubject(BigDecimal id) throws SubjectNotFoundException {
        Subject s = em.find(Subject.class, id);
        if (s == null) {
            throw new SubjectNotFoundException("subject no found");
        }

        return s;
    }
    
    @Transactional
    public Subject createSubject(Subject s) throws NullPointerException {
        if (s.getName() == null) {
            throw new NullPointerException("name must not be null");
        }
        em.persist(s);
        
        return s;
    }
    
    @Transactional
    public void updateSubject(Subject subject) throws NullPointerException, SubjectNotFoundException {
        if (subject == null) {
            throw new NullPointerException("subject must not be null");
        }
        
        Subject dbSubject = em.find(Subject.class, subject.getId());
        if (subject == null) {
            throw new SubjectNotFoundException("subject not found");
        }
        dbSubject.setName(subject.getName());
    }
    
    @Transactional
    public void removeSubject(int subjectID) throws SubjectNotFoundException {
        
        Subject subject = em.find(Subject.class, new BigDecimal(subjectID));
        if (subject == null) {
            throw new SubjectNotFoundException("subject not found");
        }

        em.remove(subject);
    }
}
