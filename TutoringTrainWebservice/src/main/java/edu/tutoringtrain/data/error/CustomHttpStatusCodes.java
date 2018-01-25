/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.error;

/**
 *
 * @author Elias
 */
public abstract class CustomHttpStatusCodes {
    public static final int BLOCKED = 450;
    public static final int MALFORMED_JSON = 451;
    public static final int SUBJECT_NOT_FOUND = 452;
    public static final int USER_NOT_FOUND = 453;
    public static final int OFFER_NOT_FOUND = 454;
    public static final int INVALID_QUERY_STRING = 455;
    public static final int BLOCK_ERROR = 456;
    public static final int SUBJECT_NOT_ACTIVE = 457;
    public static final int UNPROCESSABLE_ENTITY = 422;
    public static final int SUBJECT_USED = 458;
    public static final int COMMENT_NOT_FOUND = 459;
}
