package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInstaller;

/**
 * @author moserr
 */

public class Database {
    private static Database instance;
    private SharedPreferences prefs;
    private String sharedPrefsSessionKey = "tutoring.train.session.key";
    private String sessionKey = null;

    private Database(){

    }

    public static Database getInstance(){
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    public void initSharedPrefs(Context context) {
        prefs = context.getSharedPreferences
                (context.getResources().getString(R.string.sharedPref_key), Context.MODE_PRIVATE);
    }

    public void saveSessionKey(String sessionKey){
        this.sessionKey = sessionKey;
        prefs.edit().putString(sharedPrefsSessionKey,sessionKey).apply();
    }

    public String getSessionKey(){
        if(sessionKey == null){
            sessionKey = prefs.getString(sharedPrefsSessionKey,null);
        }
        return sessionKey;
    }


}