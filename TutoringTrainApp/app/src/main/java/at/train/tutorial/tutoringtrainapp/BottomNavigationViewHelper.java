package at.train.tutorial.tutoringtrainapp;

/**
 * Created by Moe on 16.11.2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Field;

import at.train.tutorial.tutoringtrainapp.Data.MenuEntry;

public class BottomNavigationViewHelper implements BottomNavigationView.OnNavigationItemSelectedListener{
    //test
    private Context context;
    private MenuEntry entry;

    private BottomNavigationViewHelper(Context context, MenuEntry entry){
        this.context = context;
        this.entry = entry;
    }

    public static void setupNavigationBar(BottomNavigationView view, Context context, MenuEntry entry) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        Menu menu = view.getMenu();
        MenuItem menuItem = menu.getItem(0);
        switch (entry){
            case MAIN:
                menuItem = menu.getItem(0);
                break;
            case USERS:
                menuItem = menu.getItem(1);
                break;
            case USER:
                menuItem = menu.getItem(2);
                break;
            case CHAT:
                menuItem = menu.getItem(3);
                break;
        }

        menuItem.setChecked(true);
        try {
            view.setOnNavigationItemSelectedListener(new BottomNavigationViewHelper(context,entry));
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.ic_main:
                if(entry != MenuEntry.MAIN) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
                break;

            case R.id.ic_users:
                if(entry != MenuEntry.USERS) {
                    Intent intent1 = new Intent(context, UserActivity.class);
                    context.startActivity(intent1);
                }
                break;

            case R.id.ic_user:
               //Intent intent2 = new Intent(context, ActivityTwo.class);
               //startActivity(intent2);
               break;
            case R.id.ic_chat:
                if(entry != MenuEntry.CHAT) {
                    Intent intent3 = new Intent(context, xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.MainActivity.class);
                    context.startActivity(intent3);
                }
                break;
        }

        return false;
    }
}
