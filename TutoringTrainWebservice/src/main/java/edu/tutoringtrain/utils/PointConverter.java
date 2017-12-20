/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.utils;

/**
 *
 * @author Elias
 */
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import oracle.spatial.geometry.JGeometry;

@Converter
public class PointConverter implements AttributeConverter<JGeometry, Object> {

    @Override
    public Object convertToDatabaseColumn(JGeometry x) {
        System.out.println("CLASS: " + x.getClass());
        return null;
    }

    @Override
    public JGeometry convertToEntityAttribute(Object y) {
        System.out.println("STRING: " + y);
        return new JGeometry(1, 1, 0);
    }

 
}