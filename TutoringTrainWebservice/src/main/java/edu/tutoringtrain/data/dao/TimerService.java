/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.StoredProcedureQuery;

/**
 *
 * @author Elias
 */
@Singleton
public class TimerService extends AbstractService {
    
    //Every 15 minutes
    @Schedule(second= "0", minute = "0,15,30,45", hour = "*", persistent = false)
    public void unblockUsers() {        
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRO_UNBLOCK"); 
        storedProcedure.execute();
        log("Unblocked Users");
    }
    
    //Every 24 hours at 00:00:00
    @Schedule(second= "0", minute = "0", hour = "0", persistent = false)
    public void clearTokens() {
        StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("PRO_TSESSION"); 
        storedProcedure.execute();
        log("Cleared expired Session Tokens");
    }
    
    private void log(String msg) {
        logger.info("TIMED EVENT EXECUTED: " + msg);
    }
}
