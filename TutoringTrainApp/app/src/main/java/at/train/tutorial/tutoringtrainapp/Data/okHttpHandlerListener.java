package at.train.tutorial.tutoringtrainapp.Data;

import okhttp3.Response;

/**
 * Created by Moe on 12.01.2018.
 */

public interface okHttpHandlerListener {
    void onFailure(String response);
    void onSuccess(Response response);

}
