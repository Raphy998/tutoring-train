/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

import com.fasterxml.jackson.annotation.JsonView;
import edu.tutoringtrain.utils.Views;

/**
 *
 * @author Elias
 */
public class Error {
    public static final int UNKNOWN = 1;
    public static final int ADMIN_ONLY = 2;
    public static final int TOKEN_INVALID = 3;
    public static final int TOKEN_EXPIRED = 4;
    public static final int INSUFF_PRIV = 5;
    public static final int USERNAME_NULL = 6;
    public static final int NAME_NULL = 7;
    public static final int USER_NULL = 8;
    public static final int OFFER_NULL = 9;
    public static final int GENDER_NULL = 10;
    public static final int SUBJECT_NULL = 11;
    public static final int USER_NOT_FOUND = 12;
    public static final int SUBJECT_NOT_FOUND = 13;
    public static final int OFFER_OF_USER_NOT_FOUND = 14;
    public static final int GENDER_NOT_FOUND = 15;
    public static final int INVALID_EMAIL = 16;
    public static final int START_PAGESIZE_QUERY_MISSING = 17;
    public static final int SUBJECT_CONFLICT = 18;
    public static final int USERNAME_CONFLICT = 19;
    public static final int EMAIL_CONFLICT = 20;
    public static final int USER_BLOCK_OWN = 21;
    public static final int USER_UNBLOCK_OWN = 22;
    public static final int USER_BLOCK_ADMIN = 23;
    public static final int AUTH_FAILED = 24;
    public static final int BLOCKED = 25;
    public static final int BLOCKED_NO_PARAMS = 26;
    public static final int BLOCKED_NO_DUEDATE = 27;
    public static final int BLOCKED_NO_REASON = 28;
    public static final int JSON_INVALID = 29;
    public static final int CONSTRAINT_VIOLATION = 30;
    public static final int UNSUPPORTED_MEDIA_TYPE = 31;
    public static final int SUBJECT_NOT_ACTIVE = 32;
    public static final int START_LT_ZERO = 33;
    public static final int PAGE_SIZE_LTE_ZERO = 34;
    public static final int OFFER_NOT_FOUND = 35;
    public static final int SUBJECT_USED = 36;
    
    
    @JsonView({Views.Error.Out.Public.class})
    private int code;
    @JsonView({Views.Error.Out.Public.class})
    private String message;    

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public static int getMaxErrorCode() {
        return SUBJECT_USED;
    }

    public Error() {
        
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error{" + "code=" + code + ", message=" + message + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.code;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Error other = (Error) obj;
        if (this.code != other.code) {
            return false;
        }
        return true;
    }
    
    
}
