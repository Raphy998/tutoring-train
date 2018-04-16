package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import at.train.tutorial.tutoringtrainapp.Support.BottomNavigationViewHelper;
import at.train.tutorial.tutoringtrainapp.Data.MenuEntry;
import at.train.tutorial.tutoringtrainapp.Data.Database;
import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.Chats;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.listener.FragmentInteractionListener;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Roster;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.DataStore;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp.XmppService;

public class MainActivity extends AppCompatActivity implements FragmentInteractionListener {

    private Chats chats;
    private Roster roster;
    private RelativeLayout bottomNavigationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        try {
            DataStore.getInstance().setCtx(this);
            getViews();

            Toolbar toolbar = findViewById(R.id.toolbar);
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

            bottomNavigationLayout = findViewById(R.id.bottomBar);
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navViewBottom);
            BottomNavigationViewHelper.setupNavigationBar(bottomNavigationView,this, MenuEntry.CHAT,this);

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
                    new Contact(Database.getInstance().getCurrentUser().getUsername(), "myFullName"),
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
        bottomNavigationLayout.setVisibility(View.GONE);
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
                chats.getWith().setCountNewMsgs(0);
                bottomNavigationLayout.setVisibility(View.VISIBLE);
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
}
