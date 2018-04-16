package at.train.tutorial.tutoringtrainapp.Listener;

import okhttp3.Response;

/**
 * Created by Moe on 12.01.2018.
 */

public interface okHttpHandlerListener {
    void onFailure(String response);
    void onSuccess(Response response);

}
