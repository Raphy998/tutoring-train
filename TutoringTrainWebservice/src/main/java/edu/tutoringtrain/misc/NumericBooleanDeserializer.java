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

/**
 *
 * @author Elias
 */
public class NumericBooleanDeserializer extends JsonDeserializer<Character> {
    @Override
    public Character deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        return (Boolean.parseBoolean(parser.getText()) ? '1' : '0');
    }       
}
