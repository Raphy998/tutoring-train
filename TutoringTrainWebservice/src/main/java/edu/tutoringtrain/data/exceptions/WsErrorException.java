/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.exceptions;

import edu.tutoringtrain.data.error.ErrorBuilder;

/**
 *
 * @author Elias
 */
public abstract class WsErrorException extends Exception {
    private ErrorBuilder error;

    public WsErrorException(ErrorBuilder error) {
        this.error = error;
    }

    public ErrorBuilder getError() {
        return error;
    }
    
    
}
