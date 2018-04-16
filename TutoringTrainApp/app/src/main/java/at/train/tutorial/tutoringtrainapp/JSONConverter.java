package at.train.tutorial.tutoringtrainapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.train.tutorial.tutoringtrainapp.Data.Comment;
import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.Error;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.Views;

import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;

/**
 * Created by Moe on 06.01.2018.
 */

public class JSONConverter {
    private static ObjectMapper mapper;

    private static ObjectMapper getMapper(){
        if(mapper == null){
            mapper = new ObjectMapper();
            mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setVisibility(FIELD, JsonAutoDetect.Visibility.NONE);
            mapper.setVisibility(GETTER, JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
        }
        return mapper;
    }


    public static String userToJson(User user){
        String retVal = "";
        try {
            ObjectMapper mapper = getMapper();
            mapper.writerWithView(Views.User.Out.Login.class);
            retVal = mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public static ArrayList<Entry> JsonToEntry(String json){
        ArrayList<Entry> entries = new ArrayList<>();
        try {
            ObjectMapper mapper = getMapper();
            mapper.readerWithView(Views.Entry.In.loadNewest.class);
            entries = mapper.readValue(json,new TypeReference<ArrayList<Entry>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static ArrayList<Comment> JsonToComments(String json) throws Exception{
        ArrayList<Comment> comments = new ArrayList<>();
            ObjectMapper mapper = getMapper();
            mapper.readerWithView(Views.Comment.In.loadNewest.class);
            comments = mapper.readValue(json,new TypeReference<List<Comment>>() {});
        return comments;
    }

    public static Comment JsonToComment(String json){
        Comment comment = new Comment();
        try {
            ObjectMapper mapper = getMapper();
            mapper.readerWithView(Views.Comment.In.loadNewest.class);
            comment = mapper.readValue(json,new TypeReference<Comment>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return comment;
    }


    public static String commentToJson(Comment comment){
        String retVal = "";
        try {
            ObjectMapper mapper = getMapper();
            mapper.writerWithView(Views.Comment.Out.create.class);
            retVal = mapper.writeValueAsString(comment);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("-------------------" + retVal);
        return retVal;
    }

    public static Error jsonToError(String json){
        Error error = new Error();
        try {
            ObjectMapper mapper = getMapper();
            mapper.readerWithView(Views.Error.class);
            error = mapper.readValue(json,new TypeReference<Error>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }

    public static ArrayList <User> JsonToUser(String json) {
        ArrayList<User> users = new ArrayList<>();
        try {
            ObjectMapper mapper = getMapper();
            mapper.readerWithView(Views.Entry.In.loadNewest.class);
            users = mapper.readValue(json,new TypeReference<ArrayList<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}
