package at.bsd.tutoringtrain.network.enumeration;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public enum ResultType {
    OK,
    INVALID_CREDENTIALS,
    INSUFFICIENT_PRIVILEGES,
    USER_BLOCKED,
    SESSION_KEY_INVALID_OR_EXPIRED,
    INVALID_JSON,
    SERVER_ERROR,
    USERNAME_ALREADY_EXISTS,
    USER_NOT_FOUND,
    BLOCKING_FAILED,
    SUBJECT_ALREADY_EXISTS,
    SUBJECT_NOT_FOUND,
    OFFER_NOT_FOUND,
    QUERY_PARAMETERS_MISSING,
    ERROR,
    DEFAULT
}
