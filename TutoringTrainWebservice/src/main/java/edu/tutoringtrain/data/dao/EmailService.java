/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.data.User;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Elias
 */
public class EmailService {
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String SMTP_CRED_EMAIL = "esantner99@gmail.com";
    private static final String SMTP_CRED_PASS = "stefen_m";
    
    private static final String VER_SUBJECT = "TutoringTrain - Account Verification";
    private static final String VER_CONTENT = "Hello %s!" + LINE_SEP + "Please click the following Link to verify your TutoringTrain account!" + LINE_SEP + LINE_SEP + "%s";
    private static final String VER_EMAIL = "http://localhost:51246/TutoringTrainWS/services/user/verify?key=%s";
    
    private static EmailService instance = null;
    
    private EmailService() {
        
    }
    
    public static EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    
    public void sendVerificationEmail(User user, String key) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_CRED_EMAIL, SMTP_CRED_PASS);
                }
          });

        try {
            Message message = new MimeMessage(session);
            setVerificationEmailContents(message, user, key);
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void setVerificationEmailContents(Message message, User user, String key) throws AddressException, MessagingException {
        message.setFrom(new InternetAddress("esantner99@gmail.com"));		//seems to do nothing...
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
        message.setSubject(VER_SUBJECT);
        message.setText(String.format(VER_CONTENT, user.getUsername(), String.format(VER_EMAIL, key)));
    }
}
