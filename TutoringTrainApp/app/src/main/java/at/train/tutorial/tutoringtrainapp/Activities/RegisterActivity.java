package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;

import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Data.Gender;
import at.train.tutorial.tutoringtrainapp.Data.GenderLong;
import at.train.tutorial.tutoringtrainapp.Data.URLExtension;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Listener.okHttpHandlerListener;
import at.train.tutorial.tutoringtrainapp.R;
import at.train.tutorial.tutoringtrainapp.Support.Encrypter;
import at.train.tutorial.tutoringtrainapp.Support.JSONConverter;
import at.train.tutorial.tutoringtrainapp.Support.OkHttpHandler;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, okHttpHandlerListener {
    private String [] genderType = new String[3];
    private Spinner gender;
    private String username;
    private String name;
    private String education;
    private String mail;
    private String genderString;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        genderType[0] = GenderLong.FEMALE.toString();
        genderType[1] = GenderLong.MALE.toString();
        genderType[2] = GenderLong.NEUTER.toString();

        gender = findViewById(R.id.sp_gender);
        ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,genderType);
        gender.setAdapter(spAdapter);

        findViewById(R.id.btn_register).setOnClickListener(this);
    }

    private void getTextFromForm(){
        username = ((EditText)findViewById(R.id.edt_username)).getText().toString();
        mail = ((EditText)findViewById(R.id.edt_email)).getText().toString();
        name = ((EditText)findViewById(R.id.edt_name)).getText().toString();
        password = ((EditText)findViewById(R.id.password)).getText().toString();
        education= ((EditText)findViewById(R.id.edt_education)).getText().toString();
        genderString = gender.getSelectedItem().toString();
    }

    @Override
    public void onClick(View view) {
        getTextFromForm();
        Gender g = Gender.N;
        switch (GenderLong.valueOf(genderString)){
            case MALE:
                g= Gender.M;
                break;
            case FEMALE:
                g= Gender.F;
                break;
            case NEUTER:
                g = Gender.N;
                break;
        }
        boolean b = false;
        if(password != null && !password.isEmpty()){
            b = true;
            password = Encrypter.md5(password);
        }
        User user = new User(username,mail,name,education,password,g);

        try{
            System.out.println("------------------" + JSONConverter.UserNoPasswordToJson(user));
            OkHttpHandler.RegisterUser(this,user,b);
        }
        catch(Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                    showMessage("We sent you an email to verify your account!");
                    Intent myIntent = new Intent(this, StartActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    this.startActivity(myIntent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myIntent = new Intent(this, StartActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
        this.finish();
    }
}
