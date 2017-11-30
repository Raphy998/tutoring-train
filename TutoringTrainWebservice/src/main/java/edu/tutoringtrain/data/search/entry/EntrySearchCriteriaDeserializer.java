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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import edu.tutoringtrain.data.search.BooleanSearchCriteria;
import edu.tutoringtrain.data.search.DateSearchCriteria;
import edu.tutoringtrain.data.search.EntityProp;
import edu.tutoringtrain.data.search.NumberSearchCriteria;
import edu.tutoringtrain.data.search.SearchCriteria;
import edu.tutoringtrain.data.search.StringSearchCriteria;
import java.io.IOException;

/**
 *
 * @author Elias
 */
public class EntrySearchCriteriaDeserializer extends StdDeserializer<SearchCriteria> {
    public EntrySearchCriteriaDeserializer() {
        this(null);
    }

    public EntrySearchCriteriaDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public SearchCriteria deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final ObjectMapper mapper = (ObjectMapper)parser.getCodec();
        
        //add a module to deserialize all EntityProps to UserProps since this Deserializer is only used for Users
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(EntityProp.class, new EntryEntityPropDeserializer());
        mapper.registerModule(module);
        
        SearchCriteria sc = null;
        JsonNode subNode = node.get("key");
        
        if (subNode == null) {
            throw new IOException("no key property given");
        }
        else {
            EntryProp prop = null;
            try {
                prop = EntryProp.valueOf(subNode.textValue().toUpperCase());
                if (null != prop.getDataType()) switch (prop.getDataType()) {
                    case STRING:
                        sc = mapper.treeToValue(node, StringSearchCriteria.class);
                        break;
                    case NUMBER:
                        sc = mapper.treeToValue(node, NumberSearchCriteria.class);
                        break;
                    case BOOLEAN:
                        sc = mapper.treeToValue(node, BooleanSearchCriteria.class);
                        break;
                    case DATE:
                        sc = mapper.treeToValue(node, DateSearchCriteria.class);
                        break;
                    default:
                        break;
                }
            }
            catch (IllegalArgumentException ex) {
                throw new IOException("invalid value '" + subNode.textValue() + "' for key");
            }
            catch (InvalidFormatException ex) {
                throw new IOException("operation '" + ex.getValue() + "' not possible on type " + prop.getDataType() + " of key " + prop);
            }
        }
        
        return sc;
    }
}