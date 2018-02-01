package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import android.os.AsyncTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.Jid;

import java.io.IOException;

/**
 * Created by Elias on 01.02.2018.
 */

public class Connector extends AsyncTask<Void, Void, Boolean> {

    private XMPPTCPConnection connection;

    public Connector(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    @Override
    protected synchronized Boolean doInBackground(Void... arg0) {
        if (connection.isConnected())
            return false;

        try {
            connection.connect();
            DeliveryReceiptManager dm = DeliveryReceiptManager
                    .getInstanceFor(connection);
            dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
            dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                @Override
                public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {

                }
            });

        } catch (IOException | SmackException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
