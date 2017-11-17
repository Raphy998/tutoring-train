/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class StringSearchCriteria<T extends EntityProp> extends SearchCriteria<T> {
    private StringOperation operation;
    private boolean ignoreCase;

    public StringSearchCriteria() {
        super();
    }
    
    public StringSearchCriteria(T key, StringOperation operation, Object value) {
        this(key, operation, value, false, false);
    }
    
    public StringSearchCriteria(T key, StringOperation operation, Object value, boolean ignoreCase) {
        this(key, operation, value, ignoreCase, false);
    }

    public StringSearchCriteria(T key, StringOperation operation, Object value, boolean ignoreCase, boolean not) {
        super(key, value, not);
        checkDataType(key);
        this.operation = operation;
        this.ignoreCase = ignoreCase;
    }

    public StringOperation getOperation() {
        return operation;
    }

    public void setOperation(StringOperation operation) {
        this.operation = operation;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.STRING) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.STRING);
        }
    }
}
