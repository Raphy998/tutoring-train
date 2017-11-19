package at.train.tutorial.tutoringtrainapp;

/**
 * @author moserr
 */

public interface LoginListener {
    void loginFailure(String errorMessage);
    void loginSuccess();
}
