package xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.xmpp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.forward.packet.Forwarded;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.Action;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.MainActivity;
import at.train.tutorial.tutoringtrainapp.R;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.chat.ChatMessage;
import xmpp.tutoringtrainapp.tutorial.train.at.xmppdemo.roster.Contact;

public class XmppHandler extends Application {

    public static final String PREFS_NAME = "TutoringTrainXMPP";
    private static XmppHandler instance = null;

    private static boolean isToasted = true;
    private static boolean chatCreated;
    private static Boolean activityVisible;
    private static boolean loadingMessages = false;

    private XMPPTCPConnection connection;
    private String username;
    private String password;
    private XmppService context;
    private DataStore ds;
    private static String DOMAIN;
    private static String HOST;

    private org.jivesoftware.smack.chat2.Chat chat;

    private XmppHandler(XmppService context, String username,
                        String password) {
        this.username = username;
        this.password = password;
        this.context = context;
        this.ds = DataStore.getInstance();

        DOMAIN = context.getString(R.string.xmpp_domain);
        HOST = context.getString(R.string.xmpp_host);

        ds.setMsgRoster("Loading ...");
        init();
    }

    public static XmppHandler getInstance() {
        return instance;
    }

    public static XmppHandler getInstance(XmppService context, String user, String pass) {

        if (instance == null) {
            instance = new XmppHandler(context, user, pass);
        }
        else if (!instance.getUsername().equals(user) || !instance.getPassword().equals(pass)) {
            if (instance.getConnection().isConnected())
                instance.getConnection().disconnect();
            instance = new XmppHandler(context, user, pass);
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
            initialiseConnection();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    public RosterEntry getRosterEntry(BareJid jid) {
        Roster roster = Roster.getInstanceFor(connection);
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return roster.getEntry(jid);
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
                ds.addContact(getContact(entry));
            }
            if (entries.isEmpty()) {
                ds.setMsgRoster("No Contacts yet");
            }
            else {
                ds.setMsgRoster("");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log("Error loading ROSTER");
        }
    }

    private Contact getContact(RosterEntry entry) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
        VCard vCardOfUser = getVCard(entry.getJid());
        String fullName = (vCardOfUser != null && vCardOfUser.getFirstName() != null) ?
                vCardOfUser.getFirstName() :
                entry.getJid().getLocalpartOrNull().toString();


        Contact.Type type = Contact.Type.APPROVED;
        if (entry.isSubscriptionPending()) {
            type = Contact.Type.REQUESTED_BY_ME;
        }

        return new Contact(
                entry.getJid().getLocalpartOrNull().toString(),
                fullName,
                type);
    }

    public void removeFromRoster(Contact c) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        Presence subscribe = new Presence(Presence.Type.unsubscribed);
        subscribe.setTo(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
        connection.sendStanza(subscribe);

        Roster roster = Roster.getInstanceFor(connection);
        roster.removeEntry(roster.getEntry(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN)));
    }

    public void addToRoster(Contact c) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        if (c.getType().equals(Contact.Type.REQUESTED_BY_OTHER)) {
            Presence subscribe = new Presence(Presence.Type.subscribed);
            subscribe.setTo(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
            connection.sendStanza(subscribe);
        }
        else if (c.getType().equals(Contact.Type.NONE)) {
            Presence subRequest = new Presence(Presence.Type.subscribe);
            subRequest.setTo(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
            connection.sendStanza(subRequest);

            RosterEntry re = getRosterEntry(JidCreate.bareFrom(c.getUsername() + "@" + DOMAIN));
            c = getContact(re);
            c.setType(Contact.Type.REQUESTED_BY_ME);
            ds.addContact(c);
        }
    }

    public void addToRoster(String username) throws SmackException.NotConnectedException, XmppStringprepException, InterruptedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        addToRoster(new Contact(username, null, Contact.Type.NONE));
    }

    protected void intiRoster() throws SmackException.NotLoggedInException, InterruptedException, SmackException.NotConnectedException {
        loadRoster();

        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(new RosterListenerImpl());
    }

    public synchronized void loadArchivedMsgs(String username, MamManager.MamQueryResult last) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException, SmackException.NotLoggedInException, XmppStringprepException {
        MamManager mamManager = MamManager.getInstanceFor(connection);
        boolean isSupported = mamManager.isSupported();

        final Jid to = JidCreate.entityBareFrom(username + "@" + DOMAIN);

        if (isSupported && !loadingMessages) {

            loadingMessages = true;
            MamManager.MamQueryResult mamQueryResult = getArchivedMessages(to.toString(), Integer.MAX_VALUE, last);
            log("MAM query successful! (" + username + ", " + mamQueryResult.forwardedMessages.size() + ")");

            if (ds.getChatMessageCount(username) < mamQueryResult.mamFin.getRSMSet().getCount()) {

                //ds.setLastQueryResult(mamQueryResult);

                Message message;
                for (Forwarded fm: mamQueryResult.forwardedMessages) {
                    message = (Message) fm.getForwardedStanza();

                    ChatMessage chatMessage = new ChatMessage(
                            message.getFrom().toString(),
                            message.getTo().toString(),
                            message.getBody(),
                            message.getStanzaId(),
                            true);
                    chatMessage.setDateTime(fm.getDelayInformation().getStamp());

                    if (message.getFrom().getLocalpartOrNull().toString().equals(username)) {
                        chatMessage.setMine(false);
                    }

                    ds.addChatMessage(chatMessage, username);
                }
            }


            loadingMessages = false;
        }
    }

    private MamManager.MamQueryResult getArchivedMessages(String jid, int maxResults, MamManager.MamQueryResult last) {

        MamManager mamManager = MamManager.getInstanceFor(connection);
        try {
            MamManager.MamQueryResult mamQueryResult;

            if (last == null) {
                DataForm form = new DataForm(DataForm.Type.submit);
                FormField field = new FormField(FormField.FORM_TYPE);
                field.setType(FormField.Type.hidden);
                field.addValue(MamElements.NAMESPACE);
                form.addField(field);

                FormField formField = new FormField("with");
                formField.addValue(jid);
                form.addField(formField);

                RSMSet rsmSet = new RSMSet(maxResults, "", RSMSet.PageDirection.before);
                mamQueryResult = mamManager.page(form, rsmSet);
            }
            else {
                mamQueryResult = mamManager.pagePrevious(last, maxResults);
            }

            return mamQueryResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void log(final String msg) {
        Logger.getLogger("XmppHandler").log(Level.INFO, msg);
    }

    public void log(final String msg, boolean showToast) {
        Logger.getLogger("XmppHandler").log(Level.INFO, msg);
        if (showToast) {
            if (isToasted) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
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

        addConnectionListener();
        addIncomingChatListener();
        addStanzaListener();

        ReconnectionManager manager = ReconnectionManager.getInstanceFor(connection);
        manager.enableAutomaticReconnection();
        manager.setFixedDelay(5);
    }

    private void addStanzaListener() {
        connection.addAsyncStanzaListener(new StanzaListener() {
          @Override
          public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {

              try {
                  Presence p = (Presence) packet;

                  if (p.getType().equals(Presence.Type.subscribe)) {
                      RosterEntry rosterEntry = getRosterEntry(p.getFrom().asBareJid());
                      Contact.Type cType;

                      //if I have already subscribed to the user, immediately add him
                      if (rosterEntry != null && rosterEntry.getType().equals(RosterPacket.ItemType.to)) {
                          Presence subscribe = new Presence(Presence.Type.subscribed);
                          subscribe.setTo(p.getFrom().asBareJid());
                          connection.sendStanza(subscribe);
                          cType = Contact.Type.APPROVED;
                      }
                      //otherwise display in contact list
                      else {
                          cType = Contact.Type.REQUESTED_BY_OTHER;
                      }

                      VCard vCardOfUser = getVCard(p.getFrom());
                      String fullName = (vCardOfUser != null && vCardOfUser.getFirstName() != null) ?
                              vCardOfUser.getFirstName() :
                              p.getFrom().getLocalpartOrNull().toString();

                      Contact newContact = new Contact(
                              p.getFrom().getLocalpartOrNull().toString(),
                              fullName,
                              cType);

                      ds.addContact(newContact);
                  }
                  else if (p.getType().equals(Presence.Type.unsubscribed) || p.getType().equals(Presence.Type.unsubscribe)) {
                      Contact contactToRemove = new Contact(
                              p.getFrom().getLocalpartOrNull().toString(),
                              null,
                              Contact.Type.NONE);

                      ds.removeContact(contactToRemove);
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

    private void addIncomingChatListener() {
        ChatManager.getInstanceFor(connection).addIncomingListener(
                new IncomingChatMessageListenerImpl(this));
    }

    private void addConnectionListener() {
        connection.addConnectionListener(new ConnectionListenerImpl(this));
    }

    public VCard getVCard(Jid user) throws XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, SmackException.NoResponseException {
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
        Connector connector = new Connector(connection);
        connector.execute();
    }

    protected void login() {
        try {
            connection.login(username, password);

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }
    }

    public void sendMessage(ChatMessage chatMessage) throws XmppStringprepException {
        if (!chatCreated) {
            final Jid to = JidCreate.entityBareFrom(chatMessage.getReceiver() + "@"
                    + DOMAIN);

            chat = ChatManager.getInstanceFor(connection).chatWith(to.asEntityBareJidOrThrow());
            setChatCreated(true);
        }

        try {
            if (connection.isAuthenticated()) {

                Message msg = new Message();
                msg.setBody(chatMessage.getBody());
                msg.setType(Message.Type.chat);
                msg.setTo(JidCreate.entityBareFrom(chatMessage.getReceiver() + "@" + DOMAIN));
                chat.send(msg);

                chatMessage.setDateTime(new Date());        //now
                ds.addChatMessage(chatMessage, chatMessage.getReceiver());

            } else {
                log("No Connection to Server", true);
                login();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        reconnectIfRequired();
    }

    private static void reconnectIfRequired() {
        Runnable reconnector = new Runnable() {
            @Override
            public void run() {
            while (true) {
                try {
                    XmppHandler xmppHandler = XmppHandler.getInstance();
                    if (xmppHandler.getUsername() != null && xmppHandler.getPassword() != null && !(xmppHandler.isConnected())) {
                        System.out.println("------------------ RECONNECT");
                        XmppService.connectXmpp();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            }
        };
        Thread reconnThread = new Thread(reconnector);
        reconnThread.setDaemon(true);
        reconnThread.start();
    }

    private boolean isConnected() {
        boolean isConnected;
        try {
            ping();
            isConnected = true;
        }
        catch (Exception ex) {
            isConnected = false;
        }
        return isConnected;
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
        Contact sender = ds.getContactByUsername(msg.getSender());

        Notification.Builder m_notificationBuilder = new Notification.Builder(context)
                .setContentTitle("New Message from " + sender.getFullName())
                .setContentText(msg.getBody())
                .setSmallIcon(R.drawable.send_button)
                .setAutoCancel(true);

        // create the pending intent and add to the notification
        Intent intent = new Intent(context, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("action", Action.OPEN_CHAT.name());
        extras.putStringArray("withUser", new String[] { sender.getUsername(), sender.getFullName() });
        intent.putExtras(extras);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        m_notificationBuilder.setContentIntent(pendingIntent);

        // send the notification
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, m_notificationBuilder.build());
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static boolean isChatCreated() {
        return chatCreated;
    }

    public static void setChatCreated(boolean chatCreated) {
        XmppHandler.chatCreated = chatCreated;
    }

    public boolean ping() throws SmackException.NotConnectedException, InterruptedException {
        PingManager pinger = PingManager.getInstanceFor(connection);
        return pinger.pingMyServer();
    }
}