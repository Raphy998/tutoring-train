package at.train.tutorial.tutoringtrainapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import at.train.tutorial.tutoringtrainapp.Data.URLExtension;
import at.train.tutorial.tutoringtrainapp.Data.User;
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
public class OkHttpAsyncHandler extends AsyncTask<Void,Void,Void>{
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String url;
    private String postBody;
    private LoginListener listener;
    private Response response;
    private String method;

    public static void performLogin(String username, String password, LoginListener listener){
        new OkHttpAsyncHandler(JSONConverter.userToJson(new User(username,password)),
                listener, URLExtension.AUTHENTICATION).execute();
    }

    public static void performSessionCheck(LoginListener listener){
        new OkHttpAsyncHandler("", listener,URLExtension.AUTHENTICATION + URLExtension.CHECK).execute();
    }

    private OkHttpAsyncHandler(String postBody, LoginListener listener,String method){
        this.method = method;
        System.out.println(method);
        this.url = (Database.getInstance().getUrl() + method);
        this.listener = listener;
        this.postBody = postBody;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        client = new OkHttpClient.Builder()
                .connectTimeout(15,TimeUnit.SECONDS)
                .readTimeout(15,TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        if(!URLExtension.AUTHENTICATION.equals(method)) {
            String sessionKey;
            if ((sessionKey =Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization","Bearer " + sessionKey).build();
            }
        }

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
                System.out.println(code + " <-- Code");
                if (code == HttpURLConnection.HTTP_OK) {
                    listener.loginSuccess();
                    if(method == URLExtension.AUTHENTICATION) {
                        Database.getInstance().saveSessionKey(response.body().string());
                    }
                    System.out.println("alles ok");
                } else if (code == HttpURLConnection.HTTP_BAD_REQUEST) {
                    System.out.println("bad request");
                    listener.loginFailure("bad request");
                } else if (code == 450) {
                    System.out.println("user is blocked");
                    listener.loginFailure("blocked");
                } else if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    if(method == URLExtension.AUTHENTICATION) {
                        System.out.println("wrong user or password");
                        listener.loginFailure("falscher user oder passwort");
                    }
                    else{
                        System.out.println("invalid sessionkey");
                        listener.loginFailure("invalid sessionkey");
                    }
                } else {
                    System.out.println(url);
                    System.out.println(code);
                    System.out.println(response.body().toString());
                    listener.loginFailure(response.body().toString());
                }
            } else {
                System.out.println("server is not reachable");
                listener.loginFailure("server not reachable or no Authorization");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
