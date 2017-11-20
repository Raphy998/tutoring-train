/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.tutoringtrain.adminclient.exception;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class InitializationFailedException extends Exception {

    /**
     * Creates a new instance of <code>InitializationFailedException</code>
     * without detail message.
     */
    public InitializationFailedException() {
    }

    /**
     * Constructs an instance of <code>InitializationFailedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InitializationFailedException(String msg) {
        super(msg);
    }

}
