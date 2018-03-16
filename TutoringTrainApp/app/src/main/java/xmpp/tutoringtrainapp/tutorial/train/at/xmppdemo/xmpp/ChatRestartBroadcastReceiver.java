package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Elias on 25.01.2018.
 */

public class ChatRestartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(" --------- RESTART SERVICE");
        context.startService(new Intent(context, XmppService.class));
    }
}
