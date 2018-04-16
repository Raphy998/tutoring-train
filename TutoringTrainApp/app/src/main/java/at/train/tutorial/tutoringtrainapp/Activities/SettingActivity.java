package at.train.tutorial.tutoringtrainapp.Activities;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import at.train.tutorial.tutoringtrainapp.Support.BottomNavigationViewHelper;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.Data.MenuEntry;
import at.train.tutorial.tutoringtrainapp.R;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBottom);
        BottomNavigationViewHelper.setupNavigationBar(bottomNavigationView,this, MenuEntry.SETTINGS,this);

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_logout.getId()){
            performLogout();
        }
        else{
            Toast.makeText(this,"hurentochter",Toast.LENGTH_SHORT).show();
        }
    }

    private void performLogout(){
        Database db = Database.getInstance();
        db.deleteSessionKey();
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        this.finish();
    }
}
