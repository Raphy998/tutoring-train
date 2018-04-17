package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import at.train.tutorial.tutoringtrainapp.Data.GenderLong;
import at.train.tutorial.tutoringtrainapp.Data.Rating;
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

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("User");

        btn_chat = findViewById(R.id.btn_ChatRequest);
        btn_chat.setOnClickListener(this);

        findViewById(R.id.rate_User).setOnClickListener(this);

        TextView title = findViewById(R.id.txt_title);
        title.setText(user.getUsername());

        TextView name = findViewById(R.id.txt_name);
        if (user.getName() != null && !user.getName().isEmpty()) {
            name.setText(user.getName());
        }

        TextView education = findViewById(R.id.txt_education);
        if (user.getEducation() != null && !user.getEducation().isEmpty()) {
            education.setText(user.getEducation());
        }

        TextView gender = findViewById(R.id.txt_gender);

        switch (user.getGender()) {
            case F:
                gender.setText(GenderLong.FEMALE.toString());
                break;
            case M:
                gender.setText(GenderLong.MALE.toString());
                break;
            case N:
                gender.setText(GenderLong.NEUTER.toString());
                break;
        }

        TextView rating = findViewById(R.id.txt_rating);
        rating.setText(""+ user.getAveragerating());


        Contact c = DataStore.getInstance().getContactByUsername(user.getUsername());
        System.out.println("c.getType(): " + ((c != null) ? c.getType() : "null"));
        if (c != null && c.getType() != Contact.Type.NONE) {
            //throw new Exception("User already in some sort of contact relation");
            btn_chat.setEnabled(false);
        }
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
            else if(view.getId() == R.id.rate_User){
                Intent myIntent = new Intent(this, RateActivity.class);
                myIntent.putExtra("User",user); //Optional parameters
                this.startActivity(myIntent);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
