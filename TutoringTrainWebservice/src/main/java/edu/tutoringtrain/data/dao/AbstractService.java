/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Elias
 */
public abstract class AbstractService {
    protected EntityManagerFactory emf;
    
    protected void persist(Object entity) {
        if (!entity.getClass().isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("not a entity class");
        }
        
        openEmf();
        EntityManager em = emf.createEntityManager();
        
        try {
            //persisting entity
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
        finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (em.isOpen()) {
                em.close();
            }
            closeEmf();
        }
    }
    
    protected void openEmf() {
        emf =  EmProvider.getInstance().getEntityManagerFactory();
    }
    
    protected void closeEmf() {
        EmProvider.getInstance().closeEmf();
    }
}
