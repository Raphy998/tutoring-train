package edu.tutoringtrain.data.geo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.tutoringtrain.data.geo.data.LocationObject;
import java.io.IOException;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class DataMapper {
    public static LocationObject fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(json, LocationObject.class);
    }
    
    public static String toJson(LocationObject object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(object);
    }
}
