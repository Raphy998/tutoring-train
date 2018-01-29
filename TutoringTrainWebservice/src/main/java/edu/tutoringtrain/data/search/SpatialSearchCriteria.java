/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

import edu.tutoringtrain.misc.GeoPoint;

/**
 *
 * @author Elias
 * @param <T>
 */
public class SpatialSearchCriteria<T extends EntityProp> extends SearchCriteria<T> {
    private SpatialOperation operation;
    private double radius;      //in meters
    private GeoPoint value;

    public SpatialSearchCriteria() {
        super();
    }

    public SpatialSearchCriteria(T key, SpatialOperation operation, Object value, double radius) {
        this(key, operation, value, radius, false);
    }
    
    public SpatialSearchCriteria(T key, SpatialOperation operation, Object value, double radius, boolean not) {
        super(key, value, not);
        
        if (!(value instanceof GeoPoint)) {
            throw new IllegalArgumentException("value has to be of type '" + GeoPoint.class.getTypeName() + "'");
        }
        
        checkDataType(key);
        this.operation = operation;
        this.radius = radius;
    }

    public SpatialOperation getOperation() {
        return operation;
    }

    public void setOperation(SpatialOperation operation) {
        this.operation = operation;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public GeoPoint getValue() {
        return value;
    }

    public void setValue(GeoPoint value) {
        this.value = value;
    }
    
    @Override
    public void setKey(T key) {
        checkDataType(key);
        super.setKey(key);
    }
    
    private void checkDataType(T key) throws IllegalArgumentException {
        if (key.getDataType() != DataType.SPATIAL) {
            throw new IllegalArgumentException("key '" + key + "' is not of type " + DataType.SPATIAL);
        }
    }
}
