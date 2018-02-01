package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class XmppService extends Service {
    public static final String PREFS_NAME = "TutoringTrainXMPP";

    private XmppHandler xmpp;
    private String username;
    private String password;

    public void login(String username, String password) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

        if (xmpp == null || !xmpp.getUsername().equals(username) || !xmpp.getPassword().equals(password)) {
            disconnectXmpp();
            xmpp = null;
            connectXmpp(username, password);
        }
    }

    private void connectXmpp(String username, String password) {
        this.xmpp = XmppHandler.getInstance(this, username, password);
        this.xmpp.connect("onStartCommand");
    }

    private void disconnectXmpp() {
        if (this.xmpp != null) {
            this.xmpp.disconnect();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {

        System.out.println(" ------- ON START COMMAND CALLED");

        if (intent != null && intent.getExtras() != null) {
            Bundle credentials = intent.getExtras();
            this.username = credentials.getString("username");
            this.password = credentials.getString("password");
        }

        //if no credentials are passed (e.g. service is restarted by BroadcastReceiver, try to recover them from SharedPreferences)
        if (this.username == null || this.password == null) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            this.username = settings.getString("username", null);
            this.password = settings.getString("password", null);
        }

        login(this.username, this.password);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        AsyncTask<Void, Void, Boolean> disconnectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (xmpp.getConnection().isConnected())
                    xmpp.getConnection().disconnect();
                return false;
            }
        };
        disconnectionThread.execute();

        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isActivityVisible() {
        return XmppHandler.isActivityVisible();
    }

    public static void activityResumed() {
        XmppHandler.activityResumed();
    }

    public static void activityPaused() {
        XmppHandler.activityPaused();
    }
}
