package at.train.tutorial.tutoringtrainapp.Listener;

/**
 * @author moserr
 */

public interface LoginListener {
    void loginFailure(String errorMessage);
    void loginSuccess();
}
