package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EntryActivity extends AppCompatActivity implements okHttpHandlerListener, View.OnClickListener {
    private ArrayList<Comment> comments = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lv;
    private ArrayList<String> values;
    private EditText txtMessage;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Intent intent = getIntent();

        Window window = this.getWindow();
        // changes status bar to wished color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        id= intent.getIntExtra("EntryId",0); //if it's a string you stored.
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
        txtMessage = (EditText) findViewById(R.id.txt_message);
        Button btnSend = (Button) findViewById(R.id.btn_send);

        txtHeadline.setText(entry.getHeadline());
        txtUser.setText(entry.getUser().getUsername());
        txtSubject.setText(entry.getSubject().getName());
        txtDate.setText(formatDate(entry.getPostedOn()));
        txtDesc.setText(entry.getDescription());
        lv = (ListView) findViewById(R.id.lv);

        values = new ArrayList<>();
        btnSend.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                values);
        lv.setAdapter(adapter);

        try {
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
    public void onFailure(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EntryActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccess(final Response response) {
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                comments.addAll(JSONConverter.JsonToComment(response.body().string()));
                for(final Comment c : comments){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            values.add(c.getText());
                            txtMessage.setText("");
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (IOException e) {
                //e.printStackTrace();
                //todo wenn hier, dann versuchen json to comment zu machen, sonst fehler ausgeben
            }
        }
        else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(EntryActivity.this,JSONConverter.jsonToError(response.body().string()).getMessage(),Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }

    @Override
    public void onClick(View view) {
        String message = txtMessage.getText().toString();
        Comment c = new Comment(message);
        try {
            OkHttpHandler.sendComment(EntryType.OFFER,this,id,c.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
