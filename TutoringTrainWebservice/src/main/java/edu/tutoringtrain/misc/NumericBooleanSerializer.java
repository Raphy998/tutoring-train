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

/**
 *
 * @author Elias
 */
public class NumericBooleanSerializer extends JsonSerializer<Character> {
    @Override
    public void serialize(Character c, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeBoolean(c.equals('1'));
    }
}
