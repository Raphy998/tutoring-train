/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Elias
 */
public class DateUtils {
    private static final String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static String toString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return ((date != null) ? sdf.format(date) : null);
    }
    
    public static Date toDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        }
        catch (java.text.ParseException ex) {
            ex.printStackTrace();
            date = null;
        }
        return date;
    }
}
