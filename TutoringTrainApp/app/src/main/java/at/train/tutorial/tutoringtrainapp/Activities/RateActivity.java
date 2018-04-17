package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;

import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Data.Rating;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.Views;
import at.train.tutorial.tutoringtrainapp.Listener.okHttpHandlerListener;
import at.train.tutorial.tutoringtrainapp.R;
import at.train.tutorial.tutoringtrainapp.Support.JSONConverter;
import at.train.tutorial.tutoringtrainapp.Support.OkHttpHandler;
import okhttp3.Response;

public class RateActivity extends AppCompatActivity implements View.OnClickListener, okHttpHandlerListener {
    private User ratedUser;
    private NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        np = findViewById(R.id.np_stars);

        np.setMinValue(1);
        np.setMaxValue(5);

        Button bt = findViewById(R.id.send_rating);
        bt.setOnClickListener(this);

        Intent intent = getIntent();
        ratedUser = (User) intent.getSerializableExtra("User");
    }

    @Override
    public void onClick(View view) {
        EditText eText = findViewById(R.id.edt_txt);
        String text = eText.getText().toString();
        int stars = np.getValue();

        Rating r = new Rating(text,stars);
        try {
            OkHttpHandler.sendRating(this, ratedUser, r);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String response) {
        try {
            showMessage(JSONConverter.jsonToError(response).getMessage());
        }
        catch(Exception e){
            showMessage(response);
        }
    }

    @Override
    public void onSuccess(Response response) {
        String responseString;
        try {
            if (response != null) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    this.finish();
                } else {
                    responseString = response.body().string();
                    showMessage(JSONConverter.jsonToError(responseString).getMessage());
                }
            } else {
                responseString = response.body().string();
                showMessage(JSONConverter.jsonToError(responseString).getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
