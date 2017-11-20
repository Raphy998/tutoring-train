package at.tutoringtrain.adminclient.security;

/**
 * Supported hash algorithms
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum HashAlgorithm {
    MD5(32),    //DEFAULT
    SHA1(40),
    SHA256(64),
    SHA512(128);
    
    private final int length;

    private HashAlgorithm(int length) {
        this.length = length;
    }

    public int getHashStringLength() {
        return length;
    }
}
