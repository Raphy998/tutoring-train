package at.bsd.tutoringtrain.io.email;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author Marco Wilscher marco.wilscher@edu.htl-villach.at
 */
@Deprecated
public class EmailService {
    private final static String HOST_NAME = "smtp.googlemail.com";
    private final static String MAIL_ADDRESS = "noreply.tutoringtrain@gmail.com";
    private final static String MAIL_PASSWORD = "TutoringTrain";
    private final static int SMTP_PORT = 465;
    
    /**
     * Sends a simple mail using the noreply.tutorialtrain@gmail.com address
     * @param to recipients
     * @param subject subject
     * @param message message
     * @throws EmailException 
     */
    public static void sendMail(String subject, String message, String... to) throws EmailException {
        Email email;
        DefaultAuthenticator authenticator;
        email = new SimpleEmail();
        authenticator = new DefaultAuthenticator(MAIL_ADDRESS, MAIL_PASSWORD);
        email.setHostName(HOST_NAME);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(authenticator);
        email.setSSLOnConnect(true);
        email.setFrom(MAIL_ADDRESS);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to);
        email.send();
    }
}
