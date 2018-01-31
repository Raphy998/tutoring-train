package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.MainActivity;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.ChatMessage;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

public class XMPPHandler extends Application {

    public static final String PREFS_NAME = "TutoringTrainXMPP";
    private static XMPPHandler instance = null;

    private static boolean isToasted = true;
    private static boolean chat_created;
    private static Boolean activityVisible;

    private XMPPTCPConnection connection;
    private String username;
    private String password;
    private XmppService context;
    private DataStore ds;
    private static String DOMAIN;
    private static String HOST;

    private org.jivesoftware.smack.chat2.Chat chat;
    private IncomingChatMessageListenerImpl mChatManagerListener;

    private XMPPHandler(XmppService context, String username,
                        String password) {
        this.username = username;
        this.password = password;
        this.context = context;
        this.ds = DataStore.getInstance();

        DOMAIN = context.getString(R.string.domain);
        HOST = context.getString(R.string.host);
        init();
    }

    public static XMPPHandler getInstance() {
        return instance;
    }

    public static XMPPHandler getInstance(XmppService context, String user, String pass) {

        if (instance == null) {
            instance = new XMPPHandler(context, user, pass);
        }
        else if (!instance.getUsername().equals(user) || !instance.getPassword().equals(pass)) {
            if (instance.getConnection().isConnected())
                instance.getConnection().disconnect();
            instance = new XMPPHandler(context, user, pass);
        }

        return instance;
    }

    static {
        try {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex) {
            // problem loading reconnection manager
        }
    }

    private void init() {
        try {
            mChatManagerListener = new IncomingChatMessageListenerImpl();
            initialiseConnection();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    private void loadRoster() {
        try {
            Roster roster = Roster.getInstanceFor(connection);
            //important to make server resent unaccepted requests over & over on re-login
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);

            if (!roster.isLoaded()) {
                try {
                    roster.reloadAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ds.clearRoster();
            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                VCard vCardOfUser = getVCard(entry.getJid());
                String fullName = (vCardOfUser != null && vCardOfUser.getFirstName() != null) ?
                        vCardOfUser.getFirstName() :
                        entry.getJid().getLocalpartOrNull().toString();

                Contact newContact = new Contact(
                        entry.getJid().getLocalpartOrNull().toString(),
                        fullName,
                        Contact.Type.APPROVED);

                ds.addContact(newContact);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            showToast("Error loading ROSTER");
        }
    }

    public void removeFromRoster(Contact c) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        Presence subscribe = new Presence(Presence.Type.unsubscribed);
        subscribe.setTo(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
        connection.sendStanza(subscribe);
    }

    public void addToRoster(Contact c) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        if (c.getType().equals(Contact.Type.REQUESTED)) {
            Presence subscribe = new Presence(Presence.Type.subscribed);
            subscribe.setTo(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
            connection.sendStanza(subscribe);
        }
    }

    private void intiRoster() throws SmackException.NotLoggedInException, InterruptedException, SmackException.NotConnectedException {
        loadRoster();

        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {
                System.out.println("---------------- ENTRIES ADDED: " + addresses);
                //loadRoster();
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
                System.out.println("---------------- ENTRIES UPDATED: " + addresses);
                //loadRoster();
            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
                System.out.println("---------------- ENTRIES DELETED: " + addresses);
                //loadRoster();
            }

            @Override
            public void presenceChanged(Presence presence) {
                System.out.println("---------------- PRESENCE CHANGED: " + presence);
                //loadRoster();
            }
        });
    }

    public void loadArchivedMsgs(String username) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException, SmackException.NotLoggedInException, XmppStringprepException {
        MamManager mamManager = MamManager.getInstanceFor(connection);
        boolean isSupported = mamManager.isSupported();

        final Jid to = JidCreate.entityBareFrom(username + "@" + DOMAIN);

        if (isSupported) {

            MamManager.MamQueryResult mamQueryResult = getArchivedMessages(to.toString(), 20);

            showToast("MAM query successful!");

            ds.clearChatMessages();
            Message message;
            for (Forwarded fm: mamQueryResult.forwardedMessages) {
                message = (Message) fm.getForwardedStanza();

                ChatMessage chatMessage = new ChatMessage(
                        message.getFrom().toString(),
                        message.getTo().toString(),
                        message.getBody(),
                        message.getStanzaId(),
                        false);
                chatMessage.setDateTime(fm.getDelayInformation().getStamp());        //now

                if (message.getFrom().getLocalpartOrNull().toString().equals(username)) {
                    chatMessage.setMine(true);
                }

                ds.addChatMessage(chatMessage);
            }
        }
    }

    private MamManager.MamQueryResult getArchivedMessages(String jid, int maxResults) {

        MamManager mamManager = MamManager.getInstanceFor(connection);
        try {
            DataForm form = new DataForm(DataForm.Type.submit);
            FormField field = new FormField(FormField.FORM_TYPE);
            field.setType(FormField.Type.hidden);
            field.addValue(MamElements.NAMESPACE);
            form.addField(field);

            FormField formField = new FormField("with");
            formField.addValue(jid);
            form.addField(formField);

            // "" empty string for before
            RSMSet rsmSet = new RSMSet(maxResults, "", RSMSet.PageDirection.before);
            MamManager.MamQueryResult mamQueryResult = mamManager.page(form, rsmSet);

            return mamQueryResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void showToast(final String msg) {
        if (isToasted)
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialiseConnection() throws XmppStringprepException {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setXmppDomain(DOMAIN);
        config.setHost(HOST);
        config.setPort(5222);
        XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        config.setSendPresence(true);

        try {
            TLSUtils.acceptAllCertificates(config);
            TLSUtils.disableHostnameVerificationForTlsCertificates(config);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        SASLMechanism mechanism = new SASLDigestMD5Mechanism();
        SASLAuthentication.registerSASLMechanism(mechanism);
        SASLAuthentication.unBlacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN");

        connection = new XMPPTCPConnection(config.build());
        connection.setReplyTimeout(15000);
        connection.setPacketReplyTimeout(15000);

        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
        addStanzaListener();
        addIncomingChatListener();

        ReconnectionManager manager = ReconnectionManager.getInstanceFor(connection);
        manager.enableAutomaticReconnection();
    }

    private void addIncomingChatListener() {
        ChatManager.getInstanceFor(connection).addIncomingListener(
                mChatManagerListener);
    }

    private void addStanzaListener() {
        connection.addAsyncStanzaListener(new StanzaListener() {
          @Override
          public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {

              try {
                  Presence p = (Presence) packet;
                  System.out.println("---------------- STANZA RECEIVED: " + p.getType());

                  if (p.getType().equals(Presence.Type.subscribe)) {
                      VCard vCardOfUser = getVCard(p.getFrom());
                      String fullName = (vCardOfUser != null && vCardOfUser.getFirstName() != null) ?
                              vCardOfUser.getFirstName() :
                              p.getFrom().getLocalpartOrNull().toString();

                      Contact newContact = new Contact(
                              p.getFrom().getLocalpartOrNull().toString(),
                              fullName,
                              Contact.Type.REQUESTED);

                      ds.addContact(newContact);
                  }
              }
              catch (Exception ex) {
                  ex.printStackTrace();
              }
            }
        },
        new StanzaFilter() {
            @Override
            public boolean accept(Stanza stanza) {
                boolean accept = false;

                if (stanza instanceof Presence)
                    accept = true;

                return accept;
            }
        });
    }

    private VCard getVCard(Jid user) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
        VCard vCard = null;

        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        boolean isSupported = vCardManager.isSupported(user);
        if (isSupported)  // return true
            vCard = vCardManager.loadVCard(user.asEntityBareJidIfPossible());

        return vCard;
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.disconnect();
            }
        }).start();
    }

    void connect(final String caller) {
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;

                showToast(caller + "=>connecting....");

                try {
                    connection.connect();
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {

                        }
                    });

                } catch (IOException | SmackException | XMPPException | InterruptedException ex) {
                    ex.printStackTrace();
                    showToast("Error: " + ex.getMessage());
                }
                return false;
            }
        };
        connectionThread.execute();
    }

    private void login() {
        try {
            connection.login(username, password);

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }
    }

    private class IncomingChatMessageListenerImpl implements IncomingChatMessageListener {
        @Override
        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
            processMessage(chat, message);
        }

        private void processMessage(final org.jivesoftware.smack.chat2.Chat chat,
                                   final Message message) {
            if (message.getType() == Message.Type.chat && message.getBody() != null) {
                final ChatMessage chatMessage = new ChatMessage(message.getFrom().toString(), message.getTo().toString(), message.getBody(),
                        message.getStanzaId(), false);
                chatMessage.setDateTime(new Date());        //now

                processMessage(chatMessage);
            }
        }

        private void processMessage(final ChatMessage chatMessage) {

            chatMessage.setMine(false);
            ds.addChatMessage(chatMessage);

            if (!isActivityVisible()) {
                showNewMessageNotification(chatMessage);
            }
        }
    }

    public void sendMessage(ChatMessage chatMessage) throws XmppStringprepException {
        if (!chat_created) {
            final Jid to = JidCreate.entityBareFrom(chatMessage.getReceiver() + "@"
                    + DOMAIN);

            chat = ChatManager.getInstanceFor(connection).chatWith(to.asEntityBareJidOrThrow());
            chat_created = true;
        }

        try {
            if (connection.isAuthenticated()) {

                Message msg = new Message();
                msg.setBody(chatMessage.getBody());
                msg.setType(Message.Type.chat);
                msg.setTo(JidCreate.entityBareFrom(chatMessage.getReceiver() + "@"
                        + DOMAIN));
                chat.send(msg);

                chatMessage.setDateTime(new Date());        //now
                ds.addChatMessage(chatMessage);

            } else {

                login();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            showToast("Connection Closed");
            chat_created = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            showToast("Connection Closed On Error");
            chat_created = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            showToast("Reconnection Failed");
            chat_created = false;
        }

        @Override
        public void reconnectionSuccessful() {
            showToast("Reconnected");
            chat_created = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            try {
                intiRoster();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            chat_created = false;
            showToast("Connected");
        }
    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public static boolean isActivityVisible() {

        if (activityVisible == null) {
            SharedPreferences settings = instance.context.getSharedPreferences(PREFS_NAME, 0);
            activityVisible = settings.getBoolean("activityVisible", false);
        }

        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
        saveActivityState(true);
    }

    public static void activityPaused() {
        activityVisible = false;
        saveActivityState(false);
    }

    private static void saveActivityState(boolean state) {
        if (instance != null) {
            SharedPreferences settings = instance.context.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("activityVisible", state);
            editor.apply();
        }
    }

    public void showNewMessageNotification(ChatMessage msg) {
        Notification.Builder m_notificationBuilder = new Notification.Builder(context)
                .setContentTitle("New Message from " + msg.getSenderName())
                .setContentText(msg.getBody())
                .setSmallIcon(R.drawable.send_button)
                .setAutoCancel(true);

        // create the pending intent and add to the notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        m_notificationBuilder.setContentIntent(pendingIntent);

        // send the notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, m_notificationBuilder.build());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}