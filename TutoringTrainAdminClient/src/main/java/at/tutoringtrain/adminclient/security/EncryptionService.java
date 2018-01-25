package at.tutoringtrain.adminclient.security;

import at.tutoringtrain.adminclient.exception.RequiredParameterException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * Encrypt and decrypt with AES/CBC/PKCS5PADDING
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class EncryptionService {
    private final IvParameterSpec ivParameterSpec;
    private final SecretKeySpec secretKeySpec;
    private final Cipher cipher;

    public EncryptionService(String key, String iv) throws Exception {
        if (StringUtils.isBlank(key)) {
            throw new RequiredParameterException(key, "Key must not be null or blank");
        }
        if (StringUtils.isBlank(iv)) {
            throw new RequiredParameterException(iv, "Initialization vector must not be null or blank");
        }
        this.ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        this.secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
    }
    
    public String encrypt(String value) throws Exception {
        if (StringUtils.isBlank(value)) {
            throw new RequiredParameterException(value, "Value must not be null or blank");
        }
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return Base64.encodeBase64String(cipher.doFinal(value.getBytes()));
    }
    
    public String decrypt(String encrypted) throws Exception {
        if (StringUtils.isBlank(encrypted)) {
            throw new RequiredParameterException(encrypted, "Value must not be null or blank");
        }
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(Base64.decodeBase64(encrypted)));
    }
}
