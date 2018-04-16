package at.train.tutorial.tutoringtrainapp.Support;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Data.URLExtension;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Listener.LoginListener;
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
public class OkHttpAsyncHandler extends AsyncTask<Void,Void,Void> {
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String url;
    private String postBody;
    private LoginListener listener;
    private Response response;
    private String method;

    public static void performLogin(String username, String password, LoginListener listener) {
        new OkHttpAsyncHandler(JSONConverter.userToJsonLogin(new User(username, password)),
                listener, URLExtension.AUTHENTICATION).execute();
    }

    public static void performSessionCheck(LoginListener listener) {
        new OkHttpAsyncHandler("", listener, URLExtension.AUTHENTICATION + URLExtension.CHECK).execute();
    }

    private OkHttpAsyncHandler(String postBody, LoginListener listener, String method) {
        this.method = method;
        System.out.println(method);
        this.url = (Database.getInstance().getUrl() + method);
        this.listener = listener;
        this.postBody = postBody;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        if (!URLExtension.AUTHENTICATION.equals(method)) {
            String sessionKey;
            if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
            }
        }

        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
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
                    if (method == URLExtension.AUTHENTICATION) {
                        Database.getInstance().saveSessionKey(response.body().string());
                    }
                } else {
                    listener.loginFailure(JSONConverter.jsonToError(response.body().string()).getMessage());
                }
            } else {
                listener.loginFailure("server is not reachable");
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


}
