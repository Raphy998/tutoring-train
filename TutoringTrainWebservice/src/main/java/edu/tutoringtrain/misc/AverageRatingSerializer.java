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
import edu.tutoringtrain.entities.Subject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

/**
 *
 * @author Elias
 */
public class AverageRatingSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal avgRating, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (avgRating.intValue() > 0) {
            generator.writeString(avgRating.toPlainString());
        }
        else {
            Locale loc = provider.getLocale();
            if (loc.equals(Locale.GERMAN)) {
                generator.writeString("Noch nicht bewertet");
            }
            else {
                generator.writeString("Not yet rated");
            }
        }
    }
}
