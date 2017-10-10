package at.bsd.tutoringtrain.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class PasswordHash {
    public static HashAlgorithm defaultHashAlgorithm = HashAlgorithm.MD5;
    
    /**
     * 
     * @param password
     * @return 
     */
    public static String generate(String password) {
        return generate(defaultHashAlgorithm, password);
    }
    
    /**
     * 
     * @param algorithm
     * @param password
     * @return 
     */
    public static String generate(HashAlgorithm algorithm, String password) {
        String hash;
        hash = null;
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
        return hash;
    }
}
