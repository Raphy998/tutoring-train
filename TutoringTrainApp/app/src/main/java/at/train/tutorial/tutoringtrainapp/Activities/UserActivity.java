package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Support.BottomNavigationViewHelper;
import at.train.tutorial.tutoringtrainapp.Adapter.CustomUserAdapter;
import at.train.tutorial.tutoringtrainapp.Listener.DatabaseListener;
import at.train.tutorial.tutoringtrainapp.Data.Error;
import at.train.tutorial.tutoringtrainapp.Data.MenuEntry;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.R;

public class UserActivity extends AppCompatActivity implements DatabaseListener, View.OnClickListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> users =new ArrayList();
    private RecyclerView recView;
    private Spinner entryTyp;
    private Database db;
    String [] entryType = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBottom);
        BottomNavigationViewHelper.setupNavigationBar(bottomNavigationView,this, MenuEntry.USERS,this);

        recView = (RecyclerView) findViewById(R.id.lv_test);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        Spinner spinner = (Spinner) findViewById(R.id.sp_entryTyp);
        spinner.setVisibility(View.GONE);

        try{
            layoutManager = new LinearLayoutManager(this);
            recView.setLayoutManager(layoutManager);

            db = Database.getInstance();
            users = db.getUsers();
            db.loadUsers();
            db.setListener(this);

            adapter = new CustomUserAdapter(this.users,getApplicationContext(),this);

            recView.setAdapter(adapter);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(Error e) {
        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        users = Database.getInstance().getUsers();
        System.out.println("lel2 ---------------- " + users.size());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int itemPosition = recView.getChildLayoutPosition(view);
        User item = users.get(itemPosition);

        Intent myIntent = new Intent(this, UserDisplayActivity.class);
        myIntent.putExtra("User",item); //Optional parameters
        this.startActivity(myIntent);
    }
}
