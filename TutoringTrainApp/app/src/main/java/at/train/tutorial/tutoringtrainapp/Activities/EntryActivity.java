package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import at.train.tutorial.tutoringtrainapp.Adapter.CommentListAdapter;
import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Listener.okHttpHandlerListener;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Support.JSONConverter;
import at.train.tutorial.tutoringtrainapp.Support.OkHttpHandler;
import at.train.tutorial.tutoringtrainapp.R;
import okhttp3.Response;

public class EntryActivity extends AppCompatActivity implements okHttpHandlerListener, View.OnClickListener {
    private ArrayList<Comment> comments = new ArrayList<>();
    //private ArrayAdapter<String> adapter;
    private CommentListAdapter commentAdapter;
    private ListView lv;
    private ArrayList<Comment> values;
    private EditText txtMessage;
    private int id;
    private SwipeRefreshLayout refreshLayout;


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

        TextView txtUser = (TextView) findViewById(R.id.txt_user);
        TextView txtSubject = (TextView) findViewById(R.id.txt_subject);
        TextView txtDate = (TextView) findViewById(R.id.txt_date);
        TextView txtDesc = (TextView) findViewById(R.id.txt_desc);
        TextView txtTitle = findViewById(R.id.txt_title);
        txtMessage = (EditText) findViewById(R.id.txt_message);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        refreshLayout = findViewById(R.id.swiperefresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadComments();
            }
        });

        txtTitle.setText(entry.getHeadline());

        txtUser.setText(entry.getUser().getUsername());
        txtSubject.setText(entry.getSubject().getName());
        txtDate.setText(formatDate(entry.getPostedOn()));

        android.text.format.DateFormat df = new android.text.format.DateFormat();

        txtDate.setText("Posted on: " + df.format("dd.MM.yyyy", entry.getPostedOn()) + " at "+ df.format("hh:mm:ss", entry.getPostedOn()));
        txtDesc.setText(entry.getDescription());
        lv = (ListView) findViewById(R.id.lv);

        values = new ArrayList<>();
        btnSend.setOnClickListener(this);

        commentAdapter = new CommentListAdapter(this,R.layout.commen_list_row,values);

       lv.setAdapter(commentAdapter);

       loadComments();
    }

    private String formatDate(Date date){
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
        return dateFormat.format(date);
    }

    private void loadComments(){
        try {
            values.clear();
            OkHttpHandler.loadComments(EntryType.OFFER,this,id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFailure(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                Toast.makeText(EntryActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccess(final Response response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        });

        int oldComments = values.size();
        String json = "";
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                json = response.body().string();
                comments.clear();
                comments.addAll(JSONConverter.JsonToComments(json));
                for(final Comment c : comments){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                values.add(c);
                                commentAdapter.notifyDataSetChanged();
                            }
                        });
                }
            } catch (Exception e) {
                try{
                    final Comment comm = JSONConverter.JsonToComment(json);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            values.add(comm);
                            txtMessage.setText("");
                            txtMessage.setEnabled(true);
                            commentAdapter.notifyDataSetChanged();
                        }
                    });
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
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
        if(!txtMessage.getText().toString().isEmpty()) {
            String message = txtMessage.getText().toString();
            Comment c = new Comment(message);
            txtMessage.setEnabled(false);
            try {
                OkHttpHandler.sendComment(EntryType.OFFER, this, id, c.getText());
            } catch (IOException e) {
                e.printStackTrace();
                txtMessage.setEnabled(true);
            }
        }
    }


}
