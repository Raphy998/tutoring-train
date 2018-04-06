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
    private String username;
    private String password;
    private static XmppService instance;

    public void login(String username, String password) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

        XmppHandler xmppHandler = XmppHandler.getInstance(instance, username, password);

        xmppHandler.connect("login");

        if (!xmppHandler.getUsername().equals(username) ||
                !xmppHandler.getPassword().equals(password)) {
            disconnectXmpp();
            connectXmpp(username, password);
        }
    }

    public static void connectXmpp(String username, String password) {
        XmppHandler.getInstance(instance, username, password).connect("onStartCommand");
    }

    public static void connectXmpp() {
        XmppHandler.getInstance(instance, instance.username, instance.password).connect("onStartCommand");
    }

    private void disconnectXmpp() {
        XmppHandler.getInstance().disconnect();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {

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
                if (XmppHandler.getInstance().getConnection().isConnected())
                    XmppHandler.getInstance().getConnection().disconnect();
                return false;
            }
        };
        disconnectionThread.execute();

        Intent broadcastIntent = new Intent("xmpp.tutoringtrainapp.tutorial.train.at.RestartService");
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
