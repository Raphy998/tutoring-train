package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author moserr
 */
public class StartActivity extends AppCompatActivity implements OnClickListener{

    // UI references.
    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Window window = this.getWindow();

        // changes status bar to wished color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        //if an session key is already stored, the main activity will be started directly
        // TODO: 19.11.2017  Check for valid session key
        Database db = Database.getInstance();
        db.initSharedPrefs(this);
        String sessionKey = db.getSessionKey();
        if(sessionKey != null && !sessionKey.isEmpty()){
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            Intent myIntent = new Intent(this, LoginActivity.class);
            this.startActivity(myIntent);
        }
        else if (view == btnRegister){
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }
    }
}

