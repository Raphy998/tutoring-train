package at.train.tutorial.tutoringtrainapp.Support;

import java.io.IOException;

import at.train.tutorial.tutoringtrainapp.Data.Comment;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Data.Rating;
import at.train.tutorial.tutoringtrainapp.Data.URLExtension;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Listener.okHttpHandlerListener;
import at.train.tutorial.tutoringtrainapp.Listener.okHttpHandlerListenerUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Moe on 12.01.2018
 *
 */

public class OkHttpHandler {
    private static String sessionKey;


    public static void loadEntries(EntryType type, final okHttpHandlerListener listener, int start, int pageSize) throws IOException {
        System.out.println("load Entries");
        OkHttpClient client = new OkHttpClient();
        String urlExtension = "";

        if(type == EntryType.OFFER){
            urlExtension = URLExtension.OFFER;
        }
        else if (type == EntryType.REQUEST){
            urlExtension = URLExtension.REQUEST;
        }
        urlExtension = urlExtension.concat(URLExtension.NEW);

        try{
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
        urlBuilder.addQueryParameter("start", Integer.toString(start));
        urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
            request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                listener.onSuccess(response);
            }
        });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadComments(EntryType type, final okHttpHandlerListener listener,int entryId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlExtension = "";

        if(type == EntryType.OFFER){
            urlExtension = URLExtension.OFFER;
        }
        else if (type == EntryType.REQUEST){
            urlExtension = URLExtension.REQUEST;
        }

        urlExtension = urlExtension.concat("/" + entryId);
        urlExtension = urlExtension.concat(URLExtension.COMMENTS);

        try{
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    listener.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendComment(EntryType type, final okHttpHandlerListener listener,int entryId,String commentText) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlExtension = "";

        if(type == EntryType.OFFER){
            urlExtension = URLExtension.OFFER;
        }
        else if (type == EntryType.REQUEST){
            urlExtension = URLExtension.REQUEST;
        }

        urlExtension = urlExtension.concat("/" + entryId);
        urlExtension = urlExtension.concat(URLExtension.COMMENTS);

        try{

            HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
            String url = urlBuilder.build().toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONConverter.commentToJson(new Comment(commentText)));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    listener.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void RegisterUser(final okHttpHandlerListener listener,User user,boolean password) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlExtension = "";

        urlExtension = urlExtension.concat(URLExtension.REGISTER_USER);

        try{
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
            String url = urlBuilder.build().toString();
            RequestBody body = null;

            if(password) {
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONConverter.UserToJson(user));
            }
            else {
                body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONConverter.UserNoPasswordToJson(user));
            }

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    listener.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void sendRating(final okHttpHandlerListener listener,User user,Rating rating) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlExtension = URLExtension.USER_RATING;


        urlExtension = urlExtension.concat("/" + user.getUsername());

        try{

            HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
            String url = urlBuilder.build().toString();
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONConverter.RatingToJson(rating));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    listener.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccess(response);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadUsers(final okHttpHandlerListenerUser listener, int start, int pageSize) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String urlExtension = "";

        urlExtension = urlExtension.concat(URLExtension.ALL_USERS);

        try{
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Database.getInstance().getUrl() + urlExtension).newBuilder();
            urlBuilder.addQueryParameter("start", Integer.toString(start));
            urlBuilder.addQueryParameter("pageSize", Integer.toString(pageSize));
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            if ((sessionKey = Database.getInstance().getSessionKey()) != null) {
                request = request.newBuilder().addHeader("Authorization", "Bearer " + sessionKey).build();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    listener.onFailureUser(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    listener.onSuccessUser(response);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
