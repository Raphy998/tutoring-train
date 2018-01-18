package at.train.tutorial.tutoringtrainapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Data.Entry;
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
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ"));
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
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
}
