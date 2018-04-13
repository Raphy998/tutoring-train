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
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * @author moserr
 */

public class StartActivity extends AppCompatActivity implements OnClickListener,LoginListener{

    // UI references.
    private Button btnRegister;
    private Button btnLogin;
    private ProgressBar progress;
    private boolean loginFailed = false;

    // TODO: 19.11.2017 change Output to res/values
    //private String url = "http://tutoringtrain.zapto.org:8080/TutoringTrainWebservice/services";
    private String url = "http://192.168.194.21:8080/TutoringTrainWebservice/services";
    //private String url = "http://10.0.0.140:8080/TutoringTrainWebservice/services";
    //private String url = "http://192.168.:8080/TutoringTrainWebservice/services";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        //if an session key is already stored, the main activity will be started directly
        Database db = Database.getInstance();
        db.setUrl(url);
        db.initSharedPrefs(this);

       String sessionKey = db.getSessionKey();
        if(sessionKey != null && !sessionKey.isEmpty()){
            OkHttpAsyncHandler.performSessionCheck(this);
        }
        else{
            setupActivity();
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

    private void setupActivity(){
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        btnRegister.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        Window window = this.getWindow();
        // changes status bar to wished color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorAccent));
    }

    @Override
    public void loginFailure(String errorMessage) {
        setupActivity();
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        Intent myIntent = new Intent(this, MainActivity.class);
        this.startActivity(myIntent);
        this.finish();
    }
}

