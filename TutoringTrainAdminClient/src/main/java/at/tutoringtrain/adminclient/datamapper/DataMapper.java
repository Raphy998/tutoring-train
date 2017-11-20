package at.tutoringtrain.adminclient.datamapper;

import at.tutoringtrain.adminclient.data.BlockRequest;
import at.tutoringtrain.adminclient.data.Gender;
import at.tutoringtrain.adminclient.data.Subject;
import at.tutoringtrain.adminclient.data.User;
import at.tutoringtrain.adminclient.io.network.Credentials;
import at.tutoringtrain.adminclient.main.ApplicationConfiguration;
import at.tutoringtrain.adminclient.main.MessageCodes;
import at.tutoringtrain.adminclient.main.MessageContainer;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
public class DataMapper {
    private static DataMapper INSTANCE;

    public static DataMapper getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new DataMapper();
        }
        return INSTANCE;
    }
    
    private final Logger logger;   
    private final ObjectMapper mapper;
    
    private DataMapper() {
        this.logger = LogManager.getLogger(this);
        this.mapper = new ObjectMapper();  
        this.mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.setVisibility(FIELD, JsonAutoDetect.Visibility.NONE);
        this.mapper.setVisibility(GETTER, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
        this.mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ"));
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);      
        this.logger.debug("DataMapper initialized");
    }
    
    public ArrayList<Gender> toGenderArrayList(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<ArrayList<Gender>>(){});
    }
   
    public User toUser(String json) throws IOException {
        return mapper.readValue(json, User.class);
    }

    public User toUser(String json, Class jsonView) throws IOException {
        return mapper.readerWithView(jsonView).forType(User.class).readValue(json);
    }

    public String toJSON(User user) throws JsonProcessingException  {
        return mapper.writeValueAsString(user);
    }
    
    public String toJSON(User user, Class jsonView) throws JsonProcessingException  {
        return mapper.writerWithView(jsonView).forType(User.class).writeValueAsString(user);
    }
    
    public ArrayList<User> toUserArrayList(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<ArrayList<User>>(){});
    }
    
    public ArrayList<User> toUserArrayList(String json, Class jsonView) throws IOException {
        return mapper.readerWithView(jsonView).forType(new TypeReference<ArrayList<User>>(){}).readValue(json);
    }
    
    public Subject toSubject(String json) throws IOException {
        return mapper.readValue(json, Subject.class);
    }
    
    public Subject toSubject(String json, Class jsonView) throws IOException {
        return mapper.readerWithView(jsonView).forType(Subject.class).readValue(json);
    }

    public ArrayList<Subject> toSubjectArrayList(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<ArrayList<Subject>>(){});
    }
    
    public ArrayList<Subject> toSubjectArrayList(String json, Class jsonView) throws IOException {
        return mapper.readerWithView(jsonView).forType(new TypeReference<ArrayList<Subject>>(){}).readValue(json);
    }
    
    public String toJSON(Subject subject) throws JsonProcessingException  {
        return mapper.writeValueAsString(subject);
    }
    
    public String toJSON(Subject subject, Class jsonView) throws JsonProcessingException  {
        return mapper.writerWithView(jsonView).forType(Subject.class).writeValueAsString(subject);
    }

    public MessageContainer toMessageContainer(String json) {
        MessageContainer messageContainer;
        try {
             messageContainer = mapper.readValue(json, MessageContainer.class);
        } catch (IOException ioex) {
            messageContainer = new MessageContainer(MessageCodes.MAPPING_FAILED, json);
        }  
        return messageContainer;
    }
    
    public String toJSON(MessageContainer container) throws JsonProcessingException {
        return mapper.writeValueAsString(container);
    }
    
    public ApplicationConfiguration toApplicationConfiguration(String json) throws IOException {
        return mapper.readValue(json, ApplicationConfiguration.class);
    }
     
    public String toJson(ApplicationConfiguration configuration) throws JsonProcessingException {
        return mapper.writeValueAsString(configuration);
    }
    
    public String toJSON(BlockRequest blockRequest) throws JsonProcessingException {
        return mapper.writeValueAsString(blockRequest);
    }
    
    public String toJSON(Credentials credentials) throws JsonProcessingException {
        return mapper.writeValueAsString(credentials);
    }
    
    public String toJSON(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
