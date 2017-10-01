/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Elias
 */
public class EmProvider {
    private static final String DB_PU = "localDB";
    private static final EmProvider singleton = new EmProvider();
    private EntityManagerFactory emf;

    private EmProvider() {}

    public static EmProvider getInstance() {
        return singleton;
    }


    public EntityManagerFactory getEntityManagerFactory() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory(DB_PU);
        }
        return emf;
    }

    public void closeEmf() {
        if(emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

}//end class