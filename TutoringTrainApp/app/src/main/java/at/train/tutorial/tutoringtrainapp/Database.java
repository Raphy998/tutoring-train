package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;

import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListener;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

/**
 * @author moserr
 */

public class Database implements okHttpHandlerListener {
    private static Database instance;
    private SharedPreferences prefs;
    private String sharedPrefsSessionKey = "tutoring.train.session.key";
    private String sessionKey = null;
    private String url = null;

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

    public void setUrl(String url){
        if(this.url == null) {
            this.url = url;
        }
    }

    public String getUrl() {
        return url;
    }

    public void getEntries() throws Exception{
       OkHttpHandler.loadEntries(EntryType.OFFER,this,0,5);
    }

    @Override
    public void onFailure(String response) {

    }

    @Override
    public void onSuccess(Response response) {
        System.out.println("Success from database");
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                String s = response.body().string();
                System.out.println(s);
                for(Entry e : JSONConverter.JsonToEntry(s)){
                    System.out.println(e.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
