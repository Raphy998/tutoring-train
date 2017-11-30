/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search.entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import edu.tutoringtrain.data.search.EntityProp;
import java.io.IOException;

/**
 *
 * @author Elias
 */
public class EntryEntityPropDeserializer extends StdDeserializer<EntityProp> {
    public EntryEntityPropDeserializer() {
        this(null);
    }

    public EntryEntityPropDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public EntityProp deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final ObjectMapper mapper = (ObjectMapper)parser.getCodec();
        
        return mapper.treeToValue(node, EntryProp.class);
    }
}