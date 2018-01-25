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
public class NoHostException extends Exception {

    /**
     * Creates a new instance of <code>NoHostException</code> without detail
     * message.
     */
    public NoHostException() {
    }

    /**
     * Constructs an instance of <code>NoHostException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NoHostException(String msg) {
        super(msg);
    }

}
