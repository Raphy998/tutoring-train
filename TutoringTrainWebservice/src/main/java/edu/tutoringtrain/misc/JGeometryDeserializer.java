/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.misc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import oracle.spatial.geometry.JGeometry;

/**
 *
 * @author Elias
 */
public class JGeometryDeserializer extends JsonDeserializer<JGeometry> {
    @Override
    public JGeometry deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        GeoPoint gp = parser.readValueAs(GeoPoint.class);
        double[] coords = new double[] {gp.getLon(), gp.getLat()};
        return JGeometry.createPoint(coords, 2, 8307);
    }       
}
