/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.dao;

import edu.tutoringtrain.entities.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class EmailService extends AbstractService {
    
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final String SMTP_CRED_EMAIL = "noreply.tutoringtrain@gmail.com";
    private static final String SMTP_CRED_PASS = "TutoringTrain";
    
    private static final String VER_SUBJECT = "TutoringTrain - Account Verification";
    private static final String VER_CONTENT = "Hello %s!" + LINE_SEP + "Please click the following Link to verify your TutoringTrain account!" + LINE_SEP + LINE_SEP + "%s";
    private static final String VER_EMAIL = "http://localhost:51246/TutoringTrainWS/services/user/verify?key=%s";
    
    private static final String WELCOME_SUBJECT = "Welcome to TutoringTrain";
    private static final String WELCOME_CONTENT = "Welcome %s!";
    private static final String WELCOME_CONTENT_WITH_PASSWORD = "Welcome %s!" + LINE_SEP + LINE_SEP + "Your credentials are:" + LINE_SEP + "Username: %s" + LINE_SEP + "Password: %s";
    
    private static final String NEWS_SUBJECT = "TutoringTrain Newsletter";
        
    private ExecutorService executor;
    
    public EmailService() {
        executor = Executors.newFixedThreadPool(10); // Max 10 threads.
    }
    
    public void sendWelcomeEmail(final User user, final boolean errorIfNotSend) {
        sendWelcomeEmail(user, errorIfNotSend, null);
    }
    
    public void sendWelcomeEmail(final User user, final boolean errorIfNotSend, final String password) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (errorIfNotSend && (user == null || user.getEmail() == null)) {
                        throw new IllegalArgumentException("cannot send email (user or email null)");
                    }

                    Message message = new MimeMessage(getSMTPSession());
                    setWelcomeEmailContents(message, user, password);
                    Transport.send(message);

                } catch (MessagingException e) {
                    if (errorIfNotSend) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
    
    private void setWelcomeEmailContents(Message message, User user, String password) throws AddressException, MessagingException {
        message.setFrom(new InternetAddress("noreply.tutoringtrain@gmail.com"));		//seems to do nothing...
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
        message.setSubject(WELCOME_SUBJECT);
        
        if (password == null) {
            message.setText(String.format(WELCOME_CONTENT, user.getUsername()));
        }
        else {
            message.setText(String.format(WELCOME_CONTENT_WITH_PASSWORD, user.getUsername(), user.getUsername(), password));
        }
    }
    
    public void sendVerificationEmail(User user, String key) {
        try {
            Message message = new MimeMessage(getSMTPSession());
            setVerificationEmailContents(message, user, key);
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Session getSMTPSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SMTP_CRED_EMAIL, SMTP_CRED_PASS);
                }
          });
        
        return session;
    }
    
    private void setVerificationEmailContents(Message message, User user, String key) throws AddressException, MessagingException {
        message.setFrom(new InternetAddress("noreply.tutoringtrain@gmail.com"));		//seems to do nothing...
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
        message.setSubject(VER_SUBJECT);
        message.setText(String.format(VER_CONTENT, user.getUsername(), String.format(VER_EMAIL, key)));
    }
    
    public void sendNewsletter(final User user, final boolean errorIfNotSend) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (errorIfNotSend && (user == null || user.getEmail() == null)) {
                        throw new IllegalArgumentException("cannot send email (user or email null)");
                    }

                    Message message = new MimeMessage(getSMTPSession());
                    setNewsletterEmailContents(message, user);
                    Transport.send(message);
                    logger.info("Send newsletter to: " + user.getEmail());

                } catch (MessagingException e) {
                    e.printStackTrace();
                    if (errorIfNotSend) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    private void setNewsletterEmailContents(Message message, User user) throws AddressException, MessagingException, IOException {
        message.setFrom(new InternetAddress("noreply.tutoringtrain@gmail.com"));		//seems to do nothing...
        message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(user.getEmail()));
        message.setSubject(NEWS_SUBJECT);
        
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("NewsLetter/newsletter.html").getFile());
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String firstName = user.getName().substring(0, user.getName().indexOf(' '));
        message.setContent(in.lines().collect(Collectors.joining()).replaceAll("\\{name\\}", firstName), "text/html; charset=utf-8");
    }
}