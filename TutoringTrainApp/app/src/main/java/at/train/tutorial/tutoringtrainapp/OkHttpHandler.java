package at.train.tutorial.tutoringtrainapp;

import java.io.IOException;

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


    public static void loadEntries() throws IOException {
        System.out.println("load Entries");
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder =
                HttpUrl.parse("http://tutoringtrain.zapto.org:8080/TutoringTrainWebservice/services/request/new")
                        .newBuilder();
        urlBuilder.addQueryParameter("start", "0");
        urlBuilder.addQueryParameter("pageSize", "5");
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                System.out.println(response.code() + ": " + myResponse);

            }
        });
    }
}
