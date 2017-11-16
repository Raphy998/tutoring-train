package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
        adapter = new PlanetAdapter(this.planetList,getApplicationContext());

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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>{

        private LayoutInflater inflater;
        private Context context;
        private ArrayList<String> test;

        public CustomAdapter(Context context,ArrayList<String> test) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.test=test;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.planet_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.serial_number.setText(test.get(position));
        }

        @Override
        public int getItemCount() {
            return test.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView serial_number;

            public MyViewHolder(View itemView) {
                super(itemView);
                serial_number = (TextView)itemView.findViewById(R.id.text_id);
            }
        }
    }



}
