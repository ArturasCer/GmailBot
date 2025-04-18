package org.example;

import org.example.EmailExtractor.EmailExtractor;
import org.example.GmailBot.GmailController;
import org.example.data.Company;
import org.example.fileIO.FileIO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        FileIO io = new FileIO();
        //Show number of all companies and the current ones index that I applied to
        List<Company> companiesFullInfoList = io.readFileFullList();

        //Extract from txt file
        List<String> emails = EmailExtractor.run();
        emails.stream().forEach(e->System.out.println(e));
        System.out.println("Total companies: " + emails.size());
        //First time use o then use filter with the last email sent
        int appliedIndex = 0;
//        appliedIndex = IntStream.range(0, emails.size())
//                .filter(i -> emails.get(i).equals("info@itvision.lt"))
//                .findFirst()
//                .orElse(-1);
        System.out.println("Before this applied to: " + appliedIndex);


        GmailController gmail = new GmailController();
        String lastEmailSent = null; // Variable to store the last sent email
        AtomicReference<String> lastSuccessfulEmail = new AtomicReference<>(); // Thread-safe reference for tracking last success

        /* before sending remove StoredCredentials from tokens folder*/
        //sending from non json file
        if (appliedIndex != -1 && appliedIndex < emails.size()) {
            emails.stream()
                    //.skip(0)  // Start after the applied company *remove skip if sending from the start
                    //50 companies at a time!!!!!!
                    .limit(50)               // Take the next 50 companies *if sending from start because the further ones have been contacted already write (last number - 1)
                    .forEach(company -> { // Process each company
                        System.out.println(company);
                        try {
                            Company company1 = new Company();
                            company1.setEmail(company);
                            gmail.sendMail(company1);
                            // Only update last successful email if send was successful and email is not "remove"
                            if (!"remove".equalsIgnoreCase(company1.getEmail())) {
                                lastSuccessfulEmail.set(company1.getEmail());
                                System.out.println("Successfully sent email to: " + company1.getEmail());
                            }
                        } catch (GeneralSecurityException | IOException e) {
                            // Log the error but continue processing other companies
                            System.err.println("Failed to send email to " + company + ": " + e.getMessage());
                        }
                    });

            // Update lastEmailSent with the final successful email
            lastEmailSent = lastSuccessfulEmail.get();
        }



        System.out.println("After this applied to: " + (appliedIndex + 50));

// Print out the last successful email sent
        if (lastEmailSent != null) {
            System.out.println("Last successful email sent to: " + lastEmailSent);
        } else {
            System.out.println("No emails were successfully sent (or all were marked as 'remove').");
        }

    }
}