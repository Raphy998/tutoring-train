/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.misc;

import edu.tutoringtrain.data.geo.GeoPoint;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import edu.tutoringtrain.data.error.Error;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.data.exceptions.InvalidArgumentException;
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
        
        //the IOException is later handled by handleException() of AbstractResource
        try {
            checkValueRanges(gp);
        } catch (InvalidArgumentException ex) {
            throw new IOException(ex);
        }
        
        double[] coords = new double[] {gp.getLon(), gp.getLat()};
        return JGeometry.createPoint(coords, 2, 8307);
    }   

    private void checkValueRanges(GeoPoint gp) throws IOException, InvalidArgumentException {
        if (gp.getLat() < -90 || gp.getLat() > 90 ||
                gp.getLon() < -180 || gp.getLon() > 180) {
            
            throw new InvalidArgumentException(new ErrorBuilder(Error.CONSTRAINT_VIOLATION).withParams("invalid lat or lon range"));
        }
    }
}
