package at.bsd.tutoringtrain.io.network.enumeration;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public enum ResultStatus {
    OK(0),
    INVALID_CREDENTIALS(0),
    INSUFFICIENT_PRIVILEGES(0),
    USER_BLOCKED(0),
    SESSION_KEY_INVALID_OR_EXPIRED(0),
    INVALID_JSON(0),
    SERVER_ERROR(0),
    USERNAME_ALREADY_EXISTS(0),
    USER_NOT_FOUND(0),
    BLOCKING_FAILED(0),
    SUBJECT_ALREADY_EXISTS(0),
    SUBJECT_NOT_FOUND(0),
    OFFER_NOT_FOUND(0),
    QUERY_PARAMETERS_MISSING(0),
    ERROR(0),
    UNKNOWN(0);
    
    private final int statusCode;
    
    ResultStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
    
    public static ResultStatus valueOf(int statusCode) {
        ResultStatus resultStatus;
        switch (statusCode) {
            default:
                resultStatus = UNKNOWN;
                break;
        }
        return resultStatus;
    }
}
