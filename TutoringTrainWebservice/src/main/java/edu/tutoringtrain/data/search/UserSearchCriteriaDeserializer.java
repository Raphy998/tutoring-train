/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;

/**
 *
 * @author Elias
 */
public class UserSearchCriteriaDeserializer extends StdDeserializer<SearchCriteria> {
    public UserSearchCriteriaDeserializer() {
        this(null);
    }

    public UserSearchCriteriaDeserializer(final Class<?> vc) {
        super(vc);
    }

    @Override
    public SearchCriteria deserialize(final JsonParser parser, final DeserializationContext context)
    throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        final ObjectMapper mapper = (ObjectMapper)parser.getCodec();
        
        //add a module to deserialize all EntityProps to UserProps since this Deserializer is only used for Users
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(EntityProp.class, new UserEntityPropDeserializer());
        mapper.registerModule(module);
        
        SearchCriteria sc = null;
        JsonNode subNode = node.get("key");
        
        if (subNode == null) {
            throw new IOException("no key property given");
        }
        else {
            UserProp prop;
            try {
                prop = UserProp.valueOf(subNode.textValue().toUpperCase());
                if (null != prop.getDataType()) switch (prop.getDataType()) {
                    case STRING:
                        sc = mapper.treeToValue(node, StringSearchCriteria.class);
                        break;
                    case NUMBER:
                        sc = mapper.treeToValue(node, NumberSearchCriteria.class);
                        break;
                    case CHAR:
                        sc = mapper.treeToValue(node, CharacterSearchCriteria.class);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex) {
                throw new IOException("invalid value '" + subNode.textValue() + "' for key");
            }
        }
        
        return sc;
    }
}