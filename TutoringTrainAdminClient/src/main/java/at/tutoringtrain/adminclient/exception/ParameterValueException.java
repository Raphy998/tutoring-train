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
public class ParameterValueException extends Exception {

    /**
     * Creates a new instance of <code>ParameterValueException</code> without
     * detail message.
     */
    public ParameterValueException() {
    }

    /**
     * Constructs an instance of <code>ParameterValueException</code> with the
     * specified detail message.
     *
     * @param value
     * @param msg the detail message.
     */
    public ParameterValueException(Object value, String msg) {
        super(msg + " (VALUE=" + value.toString() + ")");
    }

}
