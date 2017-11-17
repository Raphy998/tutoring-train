package at.bsd.tutoringtrain.data.mapper;

import at.bsd.tutoringtrain.data.configuration.Configuration;
import at.bsd.tutoringtrain.data.entities.BlockRequest;
import at.bsd.tutoringtrain.data.entities.User;
import at.bsd.tutoringtrain.io.network.Credentials;
import at.bsd.tutoringtrain.messages.MessageContainer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class DataMapper {
    private final static ObjectMapper MAPPER = new ObjectMapper();
    
    static {
        //By default all fields without explicit view definition are included, disable this
        MAPPER.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setVisibility(FIELD, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(GETTER, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ"));
        MAPPER.registerModule(new JavaTimeModule());
    }
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static User toUser(String json) throws IOException {
        return MAPPER.readValue(json, User.class);
    }
    
    /**
     * 
     * @param json
     * @param jsonView
     * @return
     * @throws IOException 
     */
    public static User toUser(String json, Class jsonView) throws IOException {
        return MAPPER.readerWithView(jsonView).forType(User.class).readValue(json);
    }
    
    /**
     * 
     * @param user
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(User user) throws JsonProcessingException  {
        return MAPPER.writeValueAsString(user);
    }
    
    /**
     * 
     * @param user
     * @param jsonView
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(User user, Class jsonView) throws JsonProcessingException  {
        return MAPPER.writerWithView(jsonView).forType(User.class).writeValueAsString(user);
    }
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    public static ArrayList<User> toUsers(String json) throws IOException {
        return MAPPER.readValue(json, new TypeReference<ArrayList<User>>(){});
    }
    
    /**
     * 
     * @param json
     * @param jsonView
     * @return
     * @throws IOException 
     */
    public static ArrayList<User> toUsers(String json, Class jsonView) throws IOException {
        return MAPPER.readerWithView(jsonView).forType(new TypeReference<ArrayList<User>>(){}).readValue(json);
    }
    
    
    
    
    
    
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    /*public static Subject toSubject(String json) throws IOException {
        return MAPPER.readValue(json, Subject.class);
    }*/
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
    /*public static Offer toOffer(String json) throws IOException {
        return MAPPER.readValue(json, Offer.class);
    }*/
    
    /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
     public static MessageContainer toMessageContainer(String json) throws IOException {
        return MAPPER.readValue(json, MessageContainer.class);
    }
    
     /**
     * 
     * @param json
     * @return
     * @throws IOException 
     */
     public static Configuration toConfiguration(String json) throws IOException {
        return MAPPER.readValue(json, Configuration.class);
    }
     
    /**
     * 
     * @param subject
     * @return
     * @throws JsonProcessingException 
     */
    /*public static String toJSON(Subject subject) throws JsonProcessingException  {
        return MAPPER.writeValueAsString(subject);
    }*/
    
    
    
    /**
     * 
     * @param offer
     * @return
     * @throws JsonProcessingException 
     */
    /*public static String toJSON(Offer offer) throws JsonProcessingException {
        return MAPPER.writeValueAsString(offer);
    }*/
   
    /**
     * 
     * @param blockRequest
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(BlockRequest blockRequest) throws JsonProcessingException {
        return MAPPER.writeValueAsString(blockRequest);
    }
    
    /**
     * 
     * @param credentials
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(Credentials credentials) throws JsonProcessingException {
        return MAPPER.writeValueAsString(credentials);
    }
    
    /**
     * 
     * @param container
     * @return
     * @throws JsonProcessingException 
     */
    public static String toJSON(MessageContainer container) throws JsonProcessingException {
        return MAPPER.writeValueAsString(container);
    }
    
    /**
     * 
     * @param configuration
     * @return 
     * @throws JsonProcessingException 
     */
    public static String toJson(Configuration configuration) throws JsonProcessingException {
        return MAPPER.writeValueAsString(configuration);
    }
    
    
    /*
    public static <T extends Entity> T fromJson(String json, Class<T> type, Class jsonView) throws IOException {
        return MAPPER.readerWithView(jsonView).forType(type).readValue(json);
    }
    
    public static <T extends Entity> String toJson(T object, Class serializationView) throws JsonProcessingException {
        return MAPPER.writerWithView(serializationView).writeValueAsString(object);
    }
    */
}
