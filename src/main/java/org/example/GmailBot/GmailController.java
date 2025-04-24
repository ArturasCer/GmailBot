package org.example.GmailBot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.example.data.Company;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.*;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.Set;


public class GmailController {

    private static final String SENDER_EMAIL = "e.jonas@notas-it.com";
    private final Gmail service;

    public GmailController() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("GmailAutomations")
                .build();
    }

    public void sendMail(Company company) throws GeneralSecurityException, IOException {
        if(company.getEmail().equals("remove")){
            return;
        }
        //String filePath = "C:\\Users\\cerni\\IdeaProjects\\JobBot\\src\\main\\resources\\file";

        // Validate email address
        if (company.getEmail() == null || company.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email address cannot be empty");
        }

        // Sanitize email address
        String recipientEmail = company.getEmail().trim().toLowerCase();

        // Set up the email content
        String messageSubject = "Galimybė optimizuoti IT sprendimus";
        String bodyText = "Sveiki,\n" +
                "\n" +
                "Pastebėjome Jūsų įmonę ir susidomėjome, kaip šiuo metu valdote vidinius procesus – pavyzdžiui, užduočių paskirstymą komandai, patikrinimų vykdymą ar ataskaitų ruošimą.\n" +
                "\n" +
                "Specializuojamės IT sprendimuose verslui ir padedame įmonėms supaprastinti bei automatizuoti kasdienes veiklas. Dirbame su įvairiais sektoriais – nuo paslaugų iki gamybos – ir kuriame sprendimus tokioms įmonėms kaip „Mars Lietuva“.\n" +
                "\n" +
                "Jei būtų aktualu, mielai atsiųsčiau pirminį pasiūlymą ar idėjų, kaip galėtume pagerinti Jūsų įmonės procesų valdymą.\n" +
                "\n" +
                "\n" +
                "Pagarbiai,\n" +
                "Egidijus Jonas\n" +
                "IT Projektų Vadovas,\n" +
                "Tel. Nr.: +37066464906\n" +
                "https://notas-it.com/\n";

        try {
            // Create the email session
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);

            // Set email headers with proper error handling
            try {
                email.setFrom(new InternetAddress(SENDER_EMAIL, "Egidijus Jonas"));
                email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
                email.setSubject(messageSubject, "UTF-8");
            } catch (AddressException e) {
                throw new IllegalArgumentException("Invalid email address: " + recipientEmail, e);
            }

            // Create the message body part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText, "UTF-8");

            // Create the attachment part with error handling
//            File attachmentFile = new File(filePath);
//            if (!attachmentFile.exists()) {
//                throw new FileNotFoundException("Resume file not found: " + filePath);
//            }

            //Attachement part
            //MimeBodyPart attachmentPart = new MimeBodyPart();
            //DataSource source = new FileDataSource(attachmentFile);
            //attachmentPart.setDataHandler(new DataHandler(source));
            //attachmentPart.setFileName(MimeUtility.encodeText(attachmentFile.getName()));

            // Combine parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            //multipart.addBodyPart(attachmentPart);
            email.setContent(multipart);

            // Encode and send the message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            Message message = new Message().setRaw(encodedEmail);

            // Send the email
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message sent successfully. ID: " + message.getId());

        } catch (MessagingException | IOException e) {
            throw new RuntimeException("Failed to send email to " + recipientEmail, e);
        }
    }

    private static Credential getCredentials(final NetHttpTransport httpTransport,GsonFactory jsonFactory)
            throws IOException {
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(jsonFactory, new InputStreamReader(GmailController.class.getResourceAsStream("/Egidijusclient_secret_664684276365-ikg3uvvm9qi80nuj84k9vtkol4nbog6r.apps.googleusercontent.com.json")));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


}
