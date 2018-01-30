package at.train.tutorial.tutoringtrainapp;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Data.DatabaseListener;
import at.train.tutorial.tutoringtrainapp.Data.Entry;

public class MainActivity extends AppCompatActivity implements DatabaseListener, AdapterView.OnItemSelectedListener, View.OnClickListener {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Entry> entries =new ArrayList();
    private RecyclerView recView;
    private Spinner entryTyp;
    private Database db;
    String [] entryType = new String[2];

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

            //entryType = getResources().getStringArray(R.array.entry_types);
            entryType[0] = getResources().getString(R.string.offer);
            entryType[1] = getResources().getString(R.string.request);
            Spinner spinner = (Spinner) findViewById(R.id.sp_entryTyp);
            spinner.setOnItemSelectedListener(this);
            ArrayAdapter<String> spAdapter = new ArrayAdapter<>(this, R.layout.spinner_item,entryType);
            spinner.setAdapter(spAdapter);
            entryTyp = spinner;


            db = Database.getInstance();
            db.setListener(this);
            if(spinner.getSelectedItem() == getResources().getString(R.string.offer)) {
                //db.loadOffer();
            }
            else if(spinner.getSelectedItem() == getResources().getString(R.string.request)){
                //db.loadRequest();
            }

            entries = db.getEntries();
            adapter = new CustomEntryAdapter(this.entries,getApplicationContext(),this);

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        try {
            if (entryTyp.getSelectedItem() == getResources().getString(R.string.offer)) {
                db.loadOffer();
            } else if (entryTyp.getSelectedItem() == getResources().getString(R.string.request)) {
                db.loadRequest();
            }

            entries = db.getEntries();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        int itemPosition = recView.getChildLayoutPosition(view);
        Entry item = entries.get(itemPosition);

        Intent myIntent = new Intent(this, EntryActivity.class);
        myIntent.putExtra("EntryId",item.getId()); //Optional parameters
        this.startActivity(myIntent);
    }
}
