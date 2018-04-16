package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppHandler;

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

        Contact c = DataStore.getInstance().getContactByUsername(user.getUsername());
        System.out.println("c.getType(): " + ((c != null) ? c.getType() : "null"));
        if (c != null && c.getType() != Contact.Type.NONE) {
                //throw new Exception("User already in some sort of contact relation");
                btn_chat.setEnabled(false);
        }

           // NONE ... no relation
           // APPROVED ... already added
           // REQUESTED_BY_ME ... I have already sent a request
           // REQUESTED_BY_OTHER ... Other user has already requested my friendship


        Toast.makeText(this, user.getUsername(),Toast.LENGTH_LONG).show();
        }

    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.btn_ChatRequest) {
                if (this.user.getUsername().equals(Database.getInstance().getCurrentUser().getUsername())) {
                    throw new Exception("Cannot add own user");
                }

                if (this.user != null && XmppHandler.getInstance().isConnected()) {
                    Contact c = DataStore.getInstance().getContactByUsername(user.getUsername());
                    if (c != null && c.getType() != Contact.Type.NONE) {
                        throw new Exception("User already in some sort of contact relation");
                    }
                    XmppHandler.getInstance().addToRoster(this.user.getUsername());
                }
                else {
                    System.out.println("NOT CONNECTED");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
