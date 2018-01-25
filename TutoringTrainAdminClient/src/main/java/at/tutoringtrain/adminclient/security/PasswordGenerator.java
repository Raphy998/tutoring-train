package at.tutoringtrain.adminclient.security;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Password generator
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class PasswordGenerator {
    private final static int DEFAULT_GENERATED_PASSWORD_LENGTH = 6;
    
    public static String generateRandomAlphanumericPassword() {
        return RandomStringUtils.randomAlphanumeric(DEFAULT_GENERATED_PASSWORD_LENGTH);
    }
    
    public static String generateRandomAlphanumericPassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
