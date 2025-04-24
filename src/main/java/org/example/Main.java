package org.example;

import org.example.EmailExtractor.EmailExtractor;
import org.example.GmailBot.GmailController;
import org.example.data.Company;
import org.example.fileIO.FileIO;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) throws GeneralSecurityException, IOException {
        FileIO io = new FileIO();

        //Show number of all companies and the current ones index that I applied to
        List<Company> companiesFullInfoList = io.readFileFullList();

        //Test emails
//        List<Company> companiesFullInfoList = new ArrayList<>();
//        Company egidijus = new Company();
//        egidijus.setName("Egidijus Notas");
//        egidijus.setEmail("e.jonas@notas-it.com");
//        Company arturas = new Company();
//        arturas.setName("Arturas Notas");
//        arturas.setEmail("a.cerniauskas@notas-it.com");
//        companiesFullInfoList.add(egidijus);
//        companiesFullInfoList.add(arturas);

        System.out.println("Total companyList size: " + companiesFullInfoList.size());

        GmailController gmail = new GmailController();
//50 companies at a time!!!!!!
        String lastEmailSent = null; // Variable to store the last sent email
        AtomicReference<String> lastSuccessfulEmail = new AtomicReference<>(); // Thread-safe reference for tracking last success


        //change email to last email sent
        int appliedIndex = IntStream.range(0, companiesFullInfoList.size())
                .filter(i -> "info@fortas.lt".equalsIgnoreCase(companiesFullInfoList.get(i).getEmail()))
                .findFirst()
                .orElse(-1); // returns -1 if not found

// Ensure appliedIndex is valid and within range
        //Last sent from 200 *step of 200
        //int appliedIndex = -1;
        /* before sending remove StoredCredentials from tokens folder*/
        if (appliedIndex != -1 && appliedIndex < companiesFullInfoList.size() - 1) {
            companiesFullInfoList.stream()
                    .skip(appliedIndex + 1)  // Start after the applied company *remove skip if sending from the start
                    .limit(165)               // Take the next 50 companies *if sending from start because the further ones have been contacted already write (last number - 1)
                    .forEach(company -> {// Process each company
                        try {
                        System.out.println(company.getEmail());

                            gmail.sendMail(company);
                            // Only update last successful email if send was successful and email is not "remove"
                            if (!"remove".equalsIgnoreCase(company.getEmail())) {
                                lastSuccessfulEmail.set(company.getEmail());
                                System.out.println("Successfully sent email to: " + company.getEmail());
                            }
                        } catch (GeneralSecurityException | IOException e) {
                            // Log the error but continue processing other companies
                            System.err.println("Failed to send email to " + company.getEmail() + ": " + e.getMessage());
                        } catch (NullPointerException e) {
                            // Log the error but continue processing other companies
                            System.err.println("Failed on null company");
                        }
                    });

            // Update lastEmailSent with the final successful email
            lastEmailSent = lastSuccessfulEmail.get();
        }

        System.out.println("After this applied to: " + (appliedIndex + 200));

// Print out the last successful email sent
        if (lastEmailSent != null) {
            System.out.println("Last successful email sent to: " + lastEmailSent);
        } else {
            System.out.println("No emails were successfully sent (or all were marked as 'remove').");
        }
    }
}