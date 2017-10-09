/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.exceptions;

import edu.tutoringtrain.entities.Blocked;

/**
 *
 * @author Elias
 */
public class BlockedException extends Exception {
    private Blocked block;
    
    public BlockedException(Blocked block) {
        super();
        this.block = block;
    }

    public Blocked getBlock() {
        return block;
    }
}
