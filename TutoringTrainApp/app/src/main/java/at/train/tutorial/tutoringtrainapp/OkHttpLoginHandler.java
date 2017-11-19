package at.train.tutorial.tutoringtrainapp;

import android.os.AsyncTask;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author moserr
 *
 */


//todo optimize
public class OkHttpLoginHandler extends AsyncTask<Void,Void,Void>{
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String url;
    private String postBody;
    private LoginListener listener;
    private Response response;

    public OkHttpLoginHandler(String url,String username, String password, LoginListener listener){
        this.url = url + "/authentication";
        postBody = createJsonString(new User(username,Encrypter.md5(password)));
        this.listener = listener;
        System.out.println(postBody);
    }

    private String createJsonString(User user){
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }


    @Override
    protected Void doInBackground(Void... voids) {
        client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            response = client.newCall(request).execute();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            if (response != null && !isCancelled()) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    listener.loginSuccess();
                    Database.getInstance().saveSessionKey(response.body().string());
                    System.out.println("alles ok");
                } else if (code == HttpURLConnection.HTTP_BAD_REQUEST) {
                    System.out.println("bad request");
                    listener.loginFailure("bad request");
                } else if (code == 450) {
                    System.out.println("user is blocked");
                    listener.loginFailure("blocked");
                } else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    System.out.println("wrong user or password");
                    listener.loginFailure("falscher user oder passwort");
                } else {
                    System.out.println(response.body().toString());
                    listener.loginFailure(response.body().toString());
                }
            } else {
                System.out.println("server is not reachable");
                listener.loginFailure("server not reachable");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class User{
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
