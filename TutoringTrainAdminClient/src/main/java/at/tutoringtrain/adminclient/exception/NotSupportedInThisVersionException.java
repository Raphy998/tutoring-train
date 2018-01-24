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
public class NotSupportedInThisVersionException extends Exception {

    /**
     * Creates a new instance of <code>NotSupportedInThisVersion</code> without
     * detail message.
     */
    public NotSupportedInThisVersionException() {
    }

    /**
     * Constructs an instance of <code>NotSupportedInThisVersion</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotSupportedInThisVersionException(String msg) {
        super(msg);
    }

}
