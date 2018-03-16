package at.train.tutorial.tutoringtrainapp.Data;

import okhttp3.Response;


public interface okHttpHandlerListenerUser {
    void onFailureUser(String response);
    void onSuccessUser(Response response);

}
