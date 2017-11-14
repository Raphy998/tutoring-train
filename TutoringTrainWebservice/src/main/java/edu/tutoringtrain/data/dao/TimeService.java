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
public class TimeService extends AbstractService {
    
    //Every 15 minutes
    @Schedule(second= "0", minute = "0,15,30,45", hour = "*", persistent = false)
    public void unblockUsers() {
        StoredProcedureQuery query = em
        .createStoredProcedureQuery("PRO_UNBLOCK");
        query.execute();
        System.out.println("Unblocked Users");
    }
    
    //Every 24 hours at 00:00:00
    @Schedule(second= "0", minute = "0", hour = "0", persistent = false)
    public void clearTokens() {
        StoredProcedureQuery query = em
        .createStoredProcedureQuery("PRO_TSESSION");
        query.execute();
        System.out.println("Cleared expired Session Tokens");
    }
}
