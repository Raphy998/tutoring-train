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
import java.util.Locale;

/**
 *
 * @author Elias
 */
public class SubjectLocalizeSerializer extends JsonSerializer<Subject> {
    @Override
    public void serialize(Subject subject, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        Locale loc = provider.getLocale();
        String name;
        if (loc.equals(Locale.GERMAN)) {
            name = subject.getDename();
        }
        else {
            name = subject.getEnname();
        }
        
        generator.writeStartObject();
        generator.writeNumberField("id", subject.getId());
        generator.writeStringField("name", name);
        generator.writeStringField("dename", subject.getDename());
        generator.writeStringField("enname", subject.getEnname());
        generator.writeBooleanField("isactive", subject.getIsactive().equals('1'));
        generator.writeEndObject();
    }
}
