package at.train.tutorial.tutoringtrainapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import at.train.tutorial.tutoringtrainapp.Data.DatabaseListener;
import at.train.tutorial.tutoringtrainapp.Data.Entry;
import at.train.tutorial.tutoringtrainapp.Data.EntryType;
import at.train.tutorial.tutoringtrainapp.Data.Error;
import at.train.tutorial.tutoringtrainapp.Data.User;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListener;
import at.train.tutorial.tutoringtrainapp.Data.okHttpHandlerListenerUser;
import okhttp3.Response;
import okhttp3.internal.http.HttpMethod;

/**
 * @author moserr
 */

public class Database implements okHttpHandlerListener, okHttpHandlerListenerUser {
    private static Database instance;
    private SharedPreferences prefs;
    private ArrayList<Entry> entries;
    private ArrayList<User> users;
    private String sharedPrefsSessionKey = "tutoring.train.session.key";
    private String sessionKey = null;
    private String url = null;
    private DatabaseListener listener = null;

    private Database(){
        entries = new ArrayList<>();
        users = new ArrayList<>();
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

    public void setListener(DatabaseListener listener){
        this.listener = listener;
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

    public void loadOffer() throws Exception{
        entries.clear();
       OkHttpHandler.loadEntries(EntryType.OFFER,this,0,5);
    }

    public void loadUsers() throws Exception{
        users.clear();
        OkHttpHandler.loadUsers(this,0,5);
        System.out.println("----------------- load User --------------------");
    }

    public void loadRequest() throws Exception{
        entries.clear();
        OkHttpHandler.loadEntries(EntryType.REQUEST,this,0,5);
    }

    public ArrayList<Entry> getEntries(){
        return entries;
    }

    public ArrayList<User> getUsers() {return users;}

    private void notifySuccessToListener(){
        if(listener != null){
            listener.onSuccess();
        }
    }

    private void notifyFailureToListener(Error e){
        if(listener != null){
            listener.onFailure(e);
        }
    }

    @Override
    public void onFailure(String response) {

    }

    @Override
    public void onSuccess(Response response) {
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                entries.addAll(JSONConverter.JsonToEntry(response.body().string()));
                notifySuccessToListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                notifyFailureToListener(JSONConverter.jsonToError(response.body().string()));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailureUser(String response) {

    }

    @Override
    public void onSuccessUser(Response response) {
        System.out.println("++++++++++++++++++++++++ load User end --------------------");
        if(response.code() == HttpURLConnection.HTTP_OK){
            try {
                users.addAll(JSONConverter.JsonToUser(response.body().string()));
                notifySuccessToListener();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                notifyFailureToListener(JSONConverter.jsonToError(response.body().string()));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
