package at.train.tutorial.tutoringtrainapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @author moserr
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginListener {

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private ScrollView mLoginFormView;
    private Button bttnLogin;
    private OkHttpAsyncHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();

        //set status bar color
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));


        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = (ScrollView) findViewById(R.id.login_form);

        bttnLogin = (Button) findViewById(R.id.email_sign_in_button);
        bttnLogin.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(handler != null){
            handler.cancel(true);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == bttnLogin){
            if(checkUsersInput()) {
                changeStatusOfLoginForm(false);
                String username = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                OkHttpAsyncHandler.performLogin(username,Encrypter.md5(password), this);
                mProgressView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void loginFailure(String errorMessage) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
        changeStatusOfLoginForm(true);
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void loginSuccess() {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
        this.finish();
    }

    private void changeStatusOfLoginForm(boolean active) {
        mLoginFormView.setFocusable(active);
    }

    private boolean checkUsersInput(){
        if(mEmailView.getText().toString().length() <= 0){
            Toast.makeText(this,"Please insert username",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mPasswordView.getText().toString().length() <= 0){
            Toast.makeText(this,"Please insert password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}