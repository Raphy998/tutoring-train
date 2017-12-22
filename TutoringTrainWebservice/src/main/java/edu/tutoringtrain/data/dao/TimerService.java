/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 *
 * @author Elias
 */
@Singleton
public class TimerService extends AbstractService {
    
    //Every 15 minutes
    @Schedule(second= "0", minute = "0,15,30,45", hour = "*", persistent = false)
    public void unblockUsers() {
        em.createNativeQuery("{call PRO_UNBLOCK()}").executeUpdate();
        log("Unblocked Users");
    }
    
    //Every 24 hours at 00:00:00
    @Schedule(second= "0", minute = "0", hour = "0", persistent = false)
    public void clearTokens() {
        em.createNativeQuery("{call PRO_TSESSION()}").executeUpdate();
        log("Cleared expired Session Tokens");
    }
    
    private void log(String msg) {
        logger.info("TIMED EVENT EXECUTED: " + msg);
    }
}
