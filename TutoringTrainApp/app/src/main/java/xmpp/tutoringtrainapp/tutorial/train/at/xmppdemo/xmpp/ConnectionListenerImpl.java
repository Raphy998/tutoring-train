package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by Elias on 23.02.2018.
 */

public class ConnectionListenerImpl implements ConnectionListener {

    private XmppHandler handler;
    private DataStore ds;

    public ConnectionListenerImpl(XmppHandler handler) {
        this.handler = handler;
        this.ds = DataStore.getInstance();
    }

    @Override
    public void connected(final XMPPConnection connection) {
        handler.log("Connected");
        if (!connection.isAuthenticated()) {
            handler.log("Logging in");
            handler.login();
        }
    }

    @Override
    public void connectionClosed() {
        XmppHandler.setChatCreated(false);
        handler.log("Connection Closed");
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        XmppHandler.setChatCreated(false);
        handler.log("Connection Closed on Error");
    }

    @Override
    public void reconnectingIn(int arg0) {
        handler.log("Reconnecting in: " + arg0);
    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        XmppHandler.setChatCreated(false);
        handler.log("Reconnection Failed");
    }

    @Override
    public void reconnectionSuccessful() {
        XmppHandler.setChatCreated(false);
        handler.log("Reconnection Successful");
    }

    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        XmppHandler.setChatCreated(false);
        handler.log("Logged In");

        try {
            handler.intiRoster();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
