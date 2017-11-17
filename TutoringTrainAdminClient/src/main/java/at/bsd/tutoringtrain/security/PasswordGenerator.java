package at.bsd.tutoringtrain.security;

import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class PasswordGenerator {
    public static int defaultLength = 8;
    
    public static String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(defaultLength);
    }
}
