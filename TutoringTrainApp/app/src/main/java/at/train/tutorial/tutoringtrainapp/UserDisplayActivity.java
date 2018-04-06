package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import at.train.tutorial.tutoringtrainapp.Data.User;

public class UserDisplayActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_chat;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        btn_chat = findViewById(R.id.btn_ChatRequest);
        btn_chat.setOnClickListener(this);

        Toast.makeText(this, user.getUsername(),Toast.LENGTH_LONG).show();
            }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_ChatRequest){
            //
            // TODO: 06.04.2018 place code here
            //
            Toast.makeText(this, "lolololol" ,Toast.LENGTH_LONG).show();
        }
    }
}
