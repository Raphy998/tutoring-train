/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Elias
 */
@ApplicationScoped
public class AbstractService {
    @PersistenceContext(unitName = "jtaUnit")
    protected EntityManager em;
    
    protected final Logger logger = LogManager.getLogger(this.getClass().getName());
}
