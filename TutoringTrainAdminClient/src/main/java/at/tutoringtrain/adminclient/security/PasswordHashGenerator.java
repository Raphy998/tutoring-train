package at.tutoringtrain.adminclient.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Password hash generator
 * (default hash algorithm: MD5)
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class PasswordHashGenerator {
    public static HashAlgorithm defaultHashAlgorithm = HashAlgorithm.MD5;
    
    /**
     * Generates a password hash by using the default algorithm (MD5)
     * @param password plaintext password
     * @return hashed password (hex string)
     */
    public static String generate(String password) {
        return generate(defaultHashAlgorithm, password);
    }
    
    /**
     * Generates a password hash by using the specified algorithm
     * @param algorithm hash algorithm
     * @param password plaintext password
     * @return hashed password (hex string)
     */
    private static String generate(HashAlgorithm algorithm, String password) {
        String hash;
        hash = null;
        if (password != null) {
            switch (algorithm) {
                case MD5:
                    hash = DigestUtils.md5Hex(password);
                    break;
                case SHA1:
                    hash = DigestUtils.sha1Hex(password);
                    break;
                case SHA256:
                    hash = DigestUtils.sha256Hex(password);
                    break;
                case SHA512:
                    hash = DigestUtils.sha512Hex(password);
                    break;
            }
        }
        return hash;
    }
}
