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
public class BlockedException extends WsErrorException {
    public BlockedException(ErrorBuilder err) {
        super(err);
    }
}
