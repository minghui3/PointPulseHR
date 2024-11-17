package utils;

import javax.mail.*;
import javax.mail.internet.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {
    public static void sendEmail(String subject, String body) {
        String[] reports = {
            "target/cucumber-reports/chrome-html-report.html",
            "target/cucumber-reports/firefox-html-report.html",
            "target/cucumber-reports/edge-html-report.html"
        };

        // Load environment variables from .env file
        String to = "quahminghui@gmail.com";
        String from = "chummycomb0205@gmail.com";
        String password = "qlbl stqm xhux ivyv";
        String host = "smtp.gmail.com";  // Gmail SMTP server

        // Setup properties for Gmail SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");  // TLS port
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");  // Start TLS for secure connection

        // Create session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create MimeMessage object
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            // Create MimeMultipart to add body and attachments
            MimeMultipart multipart = new MimeMultipart();

            // Add HTML body content
            for (String path : reports) {
                File reportFile = new File(path);
                if (reportFile.exists()) {
                    try {
                        MimeBodyPart temp =  new MimeBodyPart();
                        temp.attachFile(reportFile);
                        multipart.addBodyPart(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Set the content of the email
            message.setContent(multipart);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        } 
    }
}
