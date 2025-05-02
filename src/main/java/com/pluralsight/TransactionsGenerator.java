package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TransactionsGenerator {

    // Constants for randomly selecting transaction descriptions and vendors
    private static final String[] DEBITDESCRIPTIONS = {
            "Payment", "Purchase"
    };

    private static final String[] DEPOSITTDESCRIPTIONS = {
            "Deposit"
    };

    private static final String[] DEBITVENDORS = {
            "Amazon", "Starbucks", "Walmart", "Target", "McDonald's", "Uber Eats", "Netflix", "Spotify",
            "Apple", "Google", "Shell Gas", "BP Gas Station", "Costco", "CVS Pharmacy", "DoorDash",
            "Best Buy", "Subway", "Domino's Pizza", "Walgreens", "Lyft", "Home Depot", "eBay",
            "Panera Bread", "Chipotle", "Airbnb"
    };

    private static final String[] DEPOSITVENDORS = {
            "Work", "PayPal", "Venmo"
    };

    // Date and time formatters for generating consistent output
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // Set the path for the transactions file
        String filePath = "src/main/resources/transactions.csv";

        // Define how many fake transactions to generate
        int numberOfTransactions = 1000;

        // Create a list to hold the generated transactions
        ArrayList<Transaction> transactions = new ArrayList<>();

        // Generate fake transactions
        for (int i = 0; i < numberOfTransactions; i++) {
            // Random date within the past 365 days
            String date = LocalDate.now().minusDays(new Random().nextInt(365)).format(DATE_FORMAT);

            // Random time within the past 24 hours
            String time = LocalTime.now().minusSeconds(new Random().nextInt(86400)).format(TIME_FORMAT);

            String description;
            String vendor;

            // Generate a random amount (positive for deposit, negative for debit)
            double amount = generateAmount();

            // Set description and vendor based on type
            if (amount > 0) {
                description = getRandom(DEPOSITTDESCRIPTIONS);
                vendor = getRandom(DEPOSITVENDORS);
            } else {
                description = getRandom(DEBITDESCRIPTIONS);
                vendor = getRandom(DEBITVENDORS);
            }

            // Add the transaction to the list
            transactions.add(new Transaction(date, time, description, vendor, amount));
        }

        // Sort transactions by date/time (assuming Comparable implemented)
        Collections.sort(transactions);

        // Write the transactions to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the header line
            String header = "date|time|description|vendor|amount\n";
            writer.write(header);

            // Write each transaction as a line
            for (Transaction transaction : transactions) {
                String line = String.format("%s|%s|%s|%s|%.2f\n",
                        transaction.getDate(), transaction.getTime(),
                        transaction.getDescription(), transaction.getVendor(),
                        transaction.getAmount());
                writer.write(line);
            }

            writer.close(); // Close the writer
            System.out.println("Transactions successfully written to " + filePath);

        } catch (IOException e) {
            // Handle any file I/O exceptions
            e.printStackTrace();
        }
    }

    // Utility method to randomly select a string from an array
    private static String getRandom(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    // Generate a random amount between 10 and 1000, positive for deposit, negative for debit
    private static double generateAmount() {
        Random rand = new Random();
        boolean isDeposit = rand.nextBoolean(); // Randomly decide if it's a deposit
        double amount = 10 + (1000 - 10) * rand.nextDouble(); // Amount between 10 and 1000
        return isDeposit ? amount : -amount;
    }

}
