package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.Chats;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.FragmentInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Roster;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppService;

public class MainActivity extends AppCompatActivity implements FragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private Context ctx;
    private Chats chats;
    private Roster roster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            this.ctx = this;
            getViews();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //display fragment
            if (savedInstanceState != null) {
                Boolean isChatVisible = savedInstanceState.getBoolean("chatVisible");

                if (isChatVisible != null && isChatVisible) {
                    setFragmentVisible(chats, true);
                    setFragmentVisible(roster, false);
                }
                else {
                    setFragmentVisible(chats, false);
                    setFragmentVisible(roster, true);
                }
            }
            else {
                setFragmentVisible(chats, false);
                setFragmentVisible(roster, true);
            }

            System.out.println(" ------------ RUNNING: " + isMyServiceRunning(XmppService.class));
            Intent serviceIntent = new Intent(ctx, XmppService.class);
            Bundle credentials = new Bundle();
            credentials.putString("username", getString(R.string.username));
            credentials.putString("password", getString(R.string.password));
            serviceIntent.putExtras(credentials);
            startService(serviceIntent);

            handleIntent(getIntent());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleIntent(Intent intentWithExtras) {
        if (intentWithExtras != null) {
            System.out.println("++++++++++++++++++ " + intentWithExtras.getExtras());
            Bundle extras = intentWithExtras.getExtras();
            if (extras != null) {
                String action = extras.getString("action");
                if (action != null) {
                    switch (Action.valueOf(action)) {
                        case OPEN_CHAT:
                            String withUser = extras.getString("withUser");
                            openChatWithUser(this, withUser);
                            break;
                    }
                }
            }
        }
    }

    private void getViews() {
        chats = (Chats) getSupportFragmentManager().findFragmentById(R.id.frag_chat);
        roster = (Roster) getSupportFragmentManager().findFragmentById(R.id.frag_roster);
    }

    private void setFragmentVisible(Fragment frag, boolean visible) throws Exception {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (visible)
            ft.show(frag);
        else
            ft.hide(frag);

        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        XmppService.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        XmppService.activityPaused();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void openChatWithUser(Object sender, String otherUsername) {
        try {
            chats.setUsers(getString(R.string.username), otherUsername);
            setFragmentVisible(roster, false);
            setFragmentVisible(chats, true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void showRoster(Fragment sender) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("chatVisible", !chats.isHidden());
    }

    @Override
    public void onBackPressed() {
        try {
            if (!chats.isHidden()) {
                setFragmentVisible(roster, true);
                setFragmentVisible(chats, false);
            }
            else {
                finish();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
