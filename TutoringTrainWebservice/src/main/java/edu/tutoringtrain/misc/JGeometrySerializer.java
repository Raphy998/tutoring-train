/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.misc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author Elias
 */
public class JGeometrySerializer extends JsonSerializer<JGeometry> {
    @Override
    public void serialize(JGeometry g, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        //generator.writeObject(new GeoPoint(g.getPointN(0).getX(), g.getPointN(0).getY()));
        generator.writeObject(new GeoPoint(g.getPoint()[0], g.getPoint()[1]));
    }
}
