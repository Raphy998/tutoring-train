/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Elias
 */
public abstract class StringUtils {
    public static String getRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
