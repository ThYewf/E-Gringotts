import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class emailsender {

    private Properties properties;
    private Session session;

    public emailsender() {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/email.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(properties.getProperty("mail.username"), properties.getProperty("mail.password"));
            }
        });
    }

    public void sendEmail(String toAddress, String subject, String message) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(properties.getProperty("mail.username")));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        msg.setSubject(subject);
        msg.setText(message);
        Transport.send(msg);
    }
}
