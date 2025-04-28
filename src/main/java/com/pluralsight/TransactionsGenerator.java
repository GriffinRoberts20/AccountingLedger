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

    private static final String[] DEBITDESCRIPTIONS = {
            "Payment", "Purchase"
    };
    private static final String[] DEPOSITTDESCRIPTIONS = {
            "Deposit"
    };
    private static final String[] DEBITVENDORS = {
            "Amazon",
            "Starbucks",
            "Walmart",
            "Target",
            "McDonald's",
            "Uber Eats",
            "Netflix",
            "Spotify",
            "Apple",
            "Google",
            "Shell Gas",
            "BP Gas Station",
            "Costco",
            "CVS Pharmacy",
            "DoorDash",
            "Best Buy",
            "Subway",
            "Domino's Pizza",
            "Walgreens",
            "Lyft",
            "Home Depot",
            "eBay",
            "Panera Bread",
            "Chipotle",
            "Airbnb"
    };
    private static final String[] DEPOSITVENDORS = {
            "Work", "PayPal", "Venmo"
    };

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        String filePath = "src/main/resources/transactions.csv";
        int numberOfTransactions = 1000;
        ArrayList<Transaction> transactions=new ArrayList<>();

        for (int i = 0; i < numberOfTransactions; i++) {
            String date = LocalDate.now().minusDays(new Random().nextInt(365)).format(DATE_FORMAT);
            String time = LocalTime.now().minusSeconds(new Random().nextInt(86400)).format(TIME_FORMAT);
            String description;
            String vendor;
            double amount = generateAmount();
            if(amount>0) {
                description = getRandom(DEPOSITTDESCRIPTIONS);
                vendor = getRandom(DEPOSITVENDORS);
            }
            else {
                description = getRandom(DEBITDESCRIPTIONS);
                vendor = getRandom(DEBITVENDORS);
            }
            transactions.add(new Transaction(date,time,description,vendor,amount));


        }
        Collections.sort(transactions);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            String header="date|time|description|vendor|amount";
            writer.write(header+"/n");

            for(Transaction transaction:transactions){
                String line = String.format("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                writer.write(line);
            }

            writer.close();
            System.out.println("Transactions successfully written to " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRandom(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    private static double generateAmount() {
        Random rand = new Random();
        boolean isDeposit = rand.nextBoolean();
        double amount = 10 + (1000 - 10) * rand.nextDouble(); // Between 10 and 1000
        return isDeposit ? amount : -amount;
    }
}
