package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.train.tutorial.tutoringtrainapp.Data.Comment;
import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Data.Views;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListener;
import okhttp3.Response;

public class EntryActivity extends AppCompatActivity implements okHttpHandlerListener {
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private ArrayList<String> values;


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
        lv = (ListView) findViewById(R.id.lv);

        values = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                values);
        lv.setAdapter(adapter);

        try {
            //OkHttpHandler.sendComment(EntryType.OFFER,this,id,"Ein weiterer test Kommentar");
            OkHttpHandler.loadComments(EntryType.OFFER,this,id);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                comments.addAll(JSONConverter.JsonToComment(response.body().string()));
                for(final Comment c : comments){
                    System.out.println(c.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            values.add(c.getText());
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
                System.out.println("lolfdsg");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println(response.code());
            try {
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ende");
    }
}
