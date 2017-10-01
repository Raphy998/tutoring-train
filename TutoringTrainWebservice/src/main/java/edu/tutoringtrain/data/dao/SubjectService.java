/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.entities.Subject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 *
 * @author Elias
 */
public class SubjectService extends AbstractService {
    private static SubjectService instance = null;
    
    private SubjectService() {
    }
    
    public static SubjectService getInstance() {
        if (instance == null) {
            instance = new SubjectService();
        }
        return instance;
    }
    
    public List<Subject> getAllSubjects() {
        List<Subject> results = new ArrayList<>();
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Subject> query =
            em.createNamedQuery("Subject.findAll", Subject.class);
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
    
    public Subject getSubject(BigDecimal id) throws NullPointerException {
        Subject s;
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            s = em.find(Subject.class, id);
            if (s == null) {
                throw new NullPointerException("subject no found");
            }
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }

        return s;
    }
    
    public Subject createSubject(String name) throws NullPointerException {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        Subject s = new Subject(name);
        try {
            this.persist(s);
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
        
        return s;
    }
    
    public void updateSubject(Subject subject) throws NullPointerException {
        if (subject == null) {
            throw new NullPointerException("subject must not be null");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Subject dbSubject = em.find(Subject.class, subject.getId());
            if (subject == null) {
                throw new NullPointerException("subject not found");
            }
            dbSubject.setName(subject.getName());
            em.getTransaction().commit();
        }
        finally {
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
    }
    
    public void removeSubject(int subjectID) throws NullPointerException {
        openEmf();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Subject subject = em.find(Subject.class, new BigDecimal(subjectID));
            if (subject == null) {
                throw new NullPointerException("subject not found");
            }
            
            em.remove(subject);
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
