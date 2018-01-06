package at.train.tutorial.tutoringtrainapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Moe on 01.12.2017.
 */

public class JSONConverter {

    public static interface Test{};

    private static String userToJSON(String username, String password){
        try {
            User user = new User(username,password);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static class User{
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
