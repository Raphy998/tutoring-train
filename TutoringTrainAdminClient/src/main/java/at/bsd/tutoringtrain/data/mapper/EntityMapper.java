package at.bsd.tutoringtrain.data.mapper;

import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.Offer;
import at.bsd.tutoringtrain.data.entities.Subject;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.network.Credentials;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Marco Wilscher <marco.wilscher@edu.htl-villach.at>
 */
public class EntityMapper {
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static User toUser(String json) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.readValue(json, User.class);
    }
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static ArrayList<User> toUsers(String json) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<ArrayList<User>>(){});
    }
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static Subject toSubject(String json) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.readValue(json, Subject.class);
    }
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static Offer toOffer(String json) throws IOException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.readValue(json, Offer.class);
    }
    
    /**
     * 
     * @param subject
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(Subject subject) throws JsonProcessingException  {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.writeValueAsString(subject);
    }
    
    /**
     * 
     * @param user
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(User user) throws JsonProcessingException  {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.writeValueAsString(user);
    }
    
    /**
     * 
     * @param offer
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(Offer offer) throws JsonProcessingException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.writeValueAsString(offer);
    }
   
    /**
     * 
     * @param blockRequest
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(BlockRequest blockRequest) throws JsonProcessingException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.writeValueAsString(blockRequest);
    }
    
    public static String toJSON(Credentials credentials) throws JsonProcessingException {
        ObjectMapper mapper;
        mapper = new ObjectMapper();
        return mapper.writeValueAsString(credentials);
    }
}
