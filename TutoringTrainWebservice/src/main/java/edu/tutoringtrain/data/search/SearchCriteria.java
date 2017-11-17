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
public abstract class SearchCriteria<T extends EntityProp> {
    private T key;
    private Object value;
    private boolean not;

    public SearchCriteria() {
    }
    
    public SearchCriteria(T key, Object value) {
        this.key = key;
        this.value = value;
    }

    public SearchCriteria(T key, Object value, boolean not) {
        this.key = key;
        this.value = value;
        this.not = not;
    }

    public T getKey() {
        return key;
    }

    public void setKey(T key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }
}
