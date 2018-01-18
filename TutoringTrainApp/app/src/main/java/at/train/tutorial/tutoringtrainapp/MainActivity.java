package at.train.tutorial.tutoringtrainapp;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> planetList=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBottom);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        RecyclerView recView = (RecyclerView) findViewById(R.id.lv_test);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));


        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.

        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(this.planetList,getApplicationContext());

        planetList.add("foo");
        planetList.add("bar");
        planetList.add("test");
        planetList.add("test");
        planetList.add("lol");
        planetList.add("foo");
        planetList.add("bar");
        planetList.add("test");
        planetList.add("test");
        planetList.add("lol");
        planetList.add("foo");
        planetList.add("bar");
        planetList.add("test");
        planetList.add("test");
        planetList.add("lol");
        planetList.add("foo");
        planetList.add("bar");
        planetList.add("test");
        planetList.add("test");
        planetList.add("lol");
        planetList.add("foo");
        planetList.add("bar");
        planetList.add("test");
        planetList.add("test");
        planetList.add("lol");

        recView.setAdapter(adapter);

        //try{
        //    //OkHttpHandler.loadEntries();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //    Toast.makeText(this,"failed",Toast.LENGTH_LONG).show();
        //}
        try{
            Database.getInstance().getEntries();
        }
        catch(Exception e){
            e.printStackTrace();
        }
//
    }







}
