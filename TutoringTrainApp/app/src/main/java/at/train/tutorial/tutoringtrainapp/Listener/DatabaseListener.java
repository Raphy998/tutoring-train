package at.train.tutorial.tutoringtrainapp.Listener;

import at.train.tutorial.tutoringtrainapp.Data.Error;

/**
 * Created by Moe on 12.01.2018.
 *
 */

public interface DatabaseListener {
    void onFailure(Error e);
    void onSuccess();
}
