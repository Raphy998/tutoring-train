package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListener;
import okhttp3.Response;

public class EntryActivity extends AppCompatActivity implements okHttpHandlerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Intent intent = getIntent();
        int id = intent.getIntExtra("EntryId",0); //if it's a string you stored.
        Entry entry = new Entry();

        ArrayList <Entry> entries = Database.getInstance().getEntries();

        for(Entry e: entries){
            if(e.getId() == id){
                entry = e;
            }
        }

        TextView txtHeadline = (TextView)findViewById(R.id.txt_headline);
        TextView txtUser = (TextView) findViewById(R.id.txt_user);
        TextView txtSubject = (TextView) findViewById(R.id.txt_subject);
        TextView txtDate = (TextView) findViewById(R.id.txt_date);
        TextView txtDesc = (TextView) findViewById(R.id.txt_desc);

        txtHeadline.setText(entry.getHeadline());
        txtUser.setText(entry.getUser().getUsername());
        txtSubject.setText(entry.getSubject().getName());
        txtDate.setText(formatDate(entry.getPostedOn()));
        txtDesc.setText(entry.getDescription());
    }

    private String formatDate(Date date){
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        return dateFormat.format(date);
    }


    @Override
    public void onFailure(String response) {

    }

    @Override
    public void onSuccess(Response response) {

    }
}
