/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.XMPPCredentials;
import edu.tutoringtrain.data.error.ErrorBuilder;
import edu.tutoringtrain.entities.User;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.javax.SASLDigestMD5Mechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

/**
 *
 * @author Elias
 * This service is used to communicate with the XMPP Chat Server.
 */
@ApplicationScoped
public class XMPPService extends AbstractService {
    private static final String DOMAIN = "tutoringtrain.hopto.org";     //The domain set in XMPP Server
    private static final String HOST = "tutoringtrain.hopto.org";             //The IP Address of the XMPP Server 
    private static final int PORT = 5222;                       //The Port for XMPP Connections (Default: 5222)
    private static final String ADMIN_USERNAME = "admin";       //The Username of the XNPP Server Admin (for creating new users)
    private static final String ADMIN_PASSWORD = "tutoringtrain";       //The Password of the XNPP Server Admin (for creating new users)

    public XMPPService() {
    }
    
    /**
     * This method sets the vCard attributes required to display the user's name and avatar correctly.
     * @param user The vCard name is set according to the user's 'name' property.
     * @throws Exception 
     */
    private void setAttributesInVCard(String username, String password, String newName) throws Exception {
        AbstractXMPPConnection conn = getXMPPConnection(username.toLowerCase(), getXMPPPassword(password));
        conn.login(username.toLowerCase(), getXMPPPassword(password));
        
        VCardManager vCardManager = VCardManager.getInstanceFor(conn);
        VCard vCard;
        vCard = vCardManager.loadVCard();
        //set first name
        vCard.setFirstName(newName);
        vCardManager.saveVCard(vCard);
        
        conn.disconnect();
    }
    
    public void setAvatarImage(String username, String password, byte[] imgData) throws edu.tutoringtrain.data.exceptions.XMPPException {
        try {
            AbstractXMPPConnection conn = getXMPPConnection(username.toLowerCase(), getXMPPPassword(password));
            conn.login(username.toLowerCase(), getXMPPPassword(password));

            VCardManager vCardManager = VCardManager.getInstanceFor(conn);
            VCard vCard;
            vCard = vCardManager.loadVCard();
            //set first name
            vCard.setAvatar(imgData);
            vCardManager.saveVCard(vCard);

            conn.disconnect();
        }
        catch (IOException | NoSuchAlgorithmException | SmackException | XMPPException ex) {
            throw new edu.tutoringtrain.data.exceptions.XMPPException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.XMPP_ERROR));
        }
    }
    
    /**
     * This method creates a new User on the XMPP server with the passed users username (toLowerCase) 
     * and the MD5 hash of his (already encrypted) password.
     * The vCard attributes required to display the user's name correctly are also set.
     * @param user
     * @throws edu.tutoringtrain.data.exceptions.XMPPException 
     */
    @Transactional
    public void createUser(User user) throws edu.tutoringtrain.data.exceptions.XMPPException {
        try {
            AbstractXMPPConnection conn = getXMPPConnection(ADMIN_USERNAME, ADMIN_PASSWORD);

            // Registering the user
            AccountManager accountManager = AccountManager.getInstance(conn);
            accountManager.sensitiveOperationOverInsecureConnection(true);

            Map<String, String> attributes = new HashMap<>();
            attributes.put("name", user.getName());

            accountManager.createAccount(user.getUsername().toLowerCase(), getXMPPPassword(user.getPassword()), attributes);
            conn.disconnect();

            //adding given name to vCard so his name is displayed in converse.js
            setAttributesInVCard(user.getUsername(), user.getPassword(), ((user.getName() != null) ? user.getName() : user.getUsername()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new edu.tutoringtrain.data.exceptions.XMPPException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.XMPP_ERROR));
        }
    }
    /**
     * This method updates the XMPP user according to the new attributes.
     * @param username The username of the useer to update. (must not be null)
     * @param password The current password of the user to update. (must not be null)
     * @param newPassword The new password of the user. (set to null if not updated)
     * @param newName The new name of the user (set to null if not updated)
     * @throws edu.tutoringtrain.data.exceptions.XMPPException 
     */
    public void updateUser(String username, String password, String newPassword, String newName) throws edu.tutoringtrain.data.exceptions.XMPPException {
        try {
            if (newName != null) {
                setAttributesInVCard(username, password, newName);
            }

            if (newPassword != null) {
                AbstractXMPPConnection conn = getXMPPConnection(username.toLowerCase(), getXMPPPassword(password));
                conn.login(username.toLowerCase(), getXMPPPassword(password));

                // Updating password
                AccountManager accountManager = AccountManager.getInstance(conn);
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.changePassword(getXMPPPassword(newPassword));
 
                conn.disconnect();
            }
        }
        catch (Exception ex) {
            throw new edu.tutoringtrain.data.exceptions.XMPPException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.XMPP_ERROR));
        }
    }
    
    public void deleteUser(String username, String password) throws edu.tutoringtrain.data.exceptions.XMPPException {
        try {
            AbstractXMPPConnection conn = getXMPPConnection(username.toLowerCase(), getXMPPPassword(password));
            conn.login(username.toLowerCase(), getXMPPPassword(password));

            //Delete user
            AccountManager accountManager = AccountManager.getInstance(conn);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.deleteAccount();

            conn.disconnect();
        }
        catch (Exception ex) {
            throw new edu.tutoringtrain.data.exceptions.XMPPException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.XMPP_ERROR));
        }
    }
    
    private String getXMPPPassword(String stringToEncrypt) throws NoSuchAlgorithmException {
        String key = null;

        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(stringToEncrypt.getBytes(), 0, stringToEncrypt.length());
        key = new BigInteger(1, m.digest()).toString(16);
        
        return key;
    }
    
    private AbstractXMPPConnection getXMPPConnection(String username, String password) throws SmackException, IOException, XMPPException {
        AbstractXMPPConnection connection;
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        //this is server name in Open fire
        config.setServiceName(DOMAIN);
        config.setUsernameAndPassword(username, password);

        //this is host name in Open fire
        config.setHost(HOST);
        config.setPort(PORT);

        SASLMechanism mechanism = new SASLDigestMD5Mechanism();
        SASLAuthentication.registerSASLMechanism(mechanism);
        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
        SASLAuthentication.unBlacklistSASLMechanism("DIGEST-MD5");
        
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connection = new XMPPTCPConnection(config.build());
        connection.setPacketReplyTimeout(10000);
        connection.connect();
        
        return connection;
    }
    
    public XMPPCredentials getCredentials(String username, String password) throws NoSuchAlgorithmException {
        return new XMPPCredentials(username.toLowerCase()+ "@" + DOMAIN, getXMPPPassword(password));
    }
    
    public void addToRoster(String username, String password, String usernameToAdd) throws edu.tutoringtrain.data.exceptions.XMPPException {
        try {
            AbstractXMPPConnection conn = getXMPPConnection(username.toLowerCase(), getXMPPPassword(password));
            conn.login(username.toLowerCase(), getXMPPPassword(password));

            Presence subscribe = new Presence(Presence.Type.subscribe);
            subscribe.setTo(usernameToAdd + "@" + DOMAIN);
            conn.sendPacket(subscribe);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new edu.tutoringtrain.data.exceptions.XMPPException(new ErrorBuilder(edu.tutoringtrain.data.error.Error.XMPP_ERROR));
        }
    }
}