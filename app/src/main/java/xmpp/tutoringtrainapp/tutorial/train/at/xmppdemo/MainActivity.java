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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.Chats;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.FragmentInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Roster;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppHandler;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppService;

public class MainActivity extends AppCompatActivity implements FragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private Chats chats;
    private Roster roster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            DataStore.getInstance().setCtx(this);
            getViews();

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //display fragment
            if (savedInstanceState != null) {
                Boolean isChatVisible = savedInstanceState.getBoolean("chatVisible");

                if (isChatVisible) {
                    showChat();
                }
                else {
                    showRoster();
                }
            }
            else {
                showRoster();
            }

            System.out.println(" ------------ RUNNING: " + isMyServiceRunning(XmppService.class));
            Intent serviceIntent = new Intent(this, XmppService.class);
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
            Bundle extras = intentWithExtras.getExtras();
            if (extras != null) {
                String action = extras.getString("action");
                if (action != null) {
                    switch (Action.valueOf(action)) {
                        case OPEN_CHAT:
                            String[] withUser = extras.getStringArray("withUser");
                            if (withUser != null)
                                openChatWithUser(this, new Contact(withUser[0], withUser[1], Contact.Type.APPROVED));
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
    public void openChatWithUser(Object sender, Contact otherUser) {
        try {
            chats.setUsers(
                    new Contact(getString(R.string.username), "myFullName"),
                    otherUser);
            showChat();
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

    private void showChat() throws Exception {
        this.setTitle("Chats: " + chats.getWith().getFullName());
        setFragmentVisible(roster, false);
        setFragmentVisible(chats, true);
    }

    private void showRoster() throws Exception {
        this.setTitle("Contacts");
        setFragmentVisible(roster, true);
        setFragmentVisible(chats, false);
    }

    @Override
    public void onBackPressed() {
        try {
            if (!chats.isHidden()) {
                showRoster();
            }
            else {
                finish();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            XmppHandler.getInstance().addToRoster("admin");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
