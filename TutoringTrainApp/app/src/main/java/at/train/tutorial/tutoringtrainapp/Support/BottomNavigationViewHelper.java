package at.train.tutorial.tutoringtrainapp.Support;

/**
 * Created by Moe on 16.11.2017.
 */

import android.app.Activity;
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

import at.train.tutorial.tutoringtrainapp.Activities.MainActivity;
import at.train.tutorial.tutoringtrainapp.Activities.SettingActivity;
import at.train.tutorial.tutoringtrainapp.Activities.UserActivity;
import at.train.tutorial.tutoringtrainapp.Data.MenuEntry;
import at.train.tutorial.tutoringtrainapp.R;

public class BottomNavigationViewHelper implements BottomNavigationView.OnNavigationItemSelectedListener {
    //test
    private Context context;
    private MenuEntry entry;
    private Activity activity;

    private BottomNavigationViewHelper(Context context, MenuEntry entry, Activity activity) {
        this.context = context;
        this.entry = entry;
        this.activity = activity;
    }

    public static void setupNavigationBar(BottomNavigationView view, Context context, MenuEntry entry, Activity activity) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        Menu menu = view.getMenu();
        MenuItem menuItem = menu.getItem(0);
        switch (entry) {
            case MAIN:
                menuItem = menu.getItem(0);
                break;
            case USERS:
                menuItem = menu.getItem(1);
                break;
            case CHAT:
                menuItem = menu.getItem(2);
                break;
            case SETTINGS:
                menuItem = menu.getItem(3);
                break;
        }

        menuItem.setChecked(true);
        try {
            view.setOnNavigationItemSelectedListener(new BottomNavigationViewHelper(context, entry, activity));
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

        switch (item.getItemId()) {
            case R.id.ic_main:
                if (entry != MenuEntry.MAIN) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    activity.finish();
                }
                break;

            case R.id.ic_users:
                if (entry != MenuEntry.USERS) {
                    Intent intent1 = new Intent(context, UserActivity.class);
                    context.startActivity(intent1);
                    activity.finish();
                }
                break;

            case R.id.ic_settings:
                Intent intent2 = new Intent(context, SettingActivity.class);
                context.startActivity(intent2);
                activity.finish();
                break;
            case R.id.ic_chat:
                if (entry != MenuEntry.CHAT) {
                    Intent intent3 = new Intent(context, xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.MainActivity.class);
                    context.startActivity(intent3);
                    activity.finish();
                }
                break;
        }

        return false;
    }
}
