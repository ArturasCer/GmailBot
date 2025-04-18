package org.example.EmailExtractor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExtractor {
    public static List<String> run() {
        String inputFilePath = "companiesFullList.json"; // Path to your input file

        try {
            List<String> emails = extractEmails(inputFilePath);
            return emails;

        } catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extracts emails from the input file and removes asterisks.
     * @param filePath Path to the input file
     * @return List of email strings without asterisks
     */
    public static List<String> extractEmails(String filePath) throws IOException {
        List<String> emails = new ArrayList<>();
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\*?");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = emailPattern.matcher(line);
                if (matcher.find()) {
                    String email = matcher.group();
                    // Remove asterisk if present
                    if (email.endsWith("*")) {
                        email = email.substring(0, email.length() - 1);
                    }
                    emails.add(email);
                }
            }
        }

        return emails;
    }
}