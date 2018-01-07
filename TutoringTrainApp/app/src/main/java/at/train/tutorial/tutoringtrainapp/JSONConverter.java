package at.train.tutorial.tutoringtrainapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.Views;

/**
 * Created by Moe on 06.01.2018.
 */

public class JSONConverter {
    public static String userToJson(User user){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION,false);
            mapper.writerWithView(Views.User.Out.Login.class);
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
