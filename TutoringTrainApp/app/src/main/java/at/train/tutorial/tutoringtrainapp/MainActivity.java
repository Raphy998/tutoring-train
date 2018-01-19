package at.train.tutorial.tutoringtrainapp;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Data.DatabaseListener;
import at.train.tutorial.tutoringtrainapp.Data.Entry;

public class MainActivity extends AppCompatActivity implements DatabaseListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Entry> entries =new ArrayList();
    private RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBottom);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        recView = (RecyclerView) findViewById(R.id.lv_test);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));

        try{
            layoutManager = new LinearLayoutManager(this);
            recView.setLayoutManager(layoutManager);
            Database db = Database.getInstance();
            db.setListener(this);
            db.loadEntries();

            entries = db.getEntries();
            adapter = new CustomEntryAdapter(this.entries,getApplicationContext());

            recView.setAdapter(adapter);
            Database.getInstance().getEntries();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onFailure() {

    }

    @Override
    public void onSuccess() {
        entries = Database.getInstance().getEntries();
        System.out.println("lel2");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
