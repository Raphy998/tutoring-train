package at.train.tutorial.tutoringtrainapp;

import java.io.IOException;

import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Data.URLExtension;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
                //final String myResponse = response.body().string();
//
                //System.out.println(response.code() + ": " + myResponse);

            }
        });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
