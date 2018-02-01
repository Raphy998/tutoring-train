package at.train.tutorial.tutoringtrainapp.Data;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by Moe on 01.02.2018.
 *
 */

public class Error {
    @JsonView({Views.Error.class})
    private int code;
    @JsonView({Views.Error.class})
    private String message;

    public Error(){

    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
