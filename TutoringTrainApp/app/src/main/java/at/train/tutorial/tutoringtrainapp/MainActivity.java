package at.train.tutorial.tutoringtrainapp;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

    }





}
