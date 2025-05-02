package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class AccountingLedgerApp {

    // File path to the CSV file containing transaction data
    static String transactionsFilePath = "src/main/resources/transactions.csv";

    // Formatter for parsing and formatting dates in the pattern yyyy-MM-dd
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Formatter for parsing and formatting time in the pattern HH:mm:ss
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // Read transactions from the CSV file into an ArrayList
        ArrayList<Transaction> transactions = readTransactions();

        // Display the home menu and allow user to interact with the transactions
        homeMenu(transactions);

        // Save or update the transactions data back to the file or storage
        updateTransactions(transactions);
    }

    public static void homeMenu(ArrayList<Transaction> transactions){
        // Flag to control the loop of the menu
        boolean menuRunning = true;

        // Main menu loop
        while(menuRunning) {
            // Print a divider for UI clarity
            MyUtils.printDivider(50);

            // Display menu header and options
            System.out.println("Home Menu");
            System.out.println("What would you like to do today?\n   D-Add Deposit\n   P-Make Payment(Debit)\n   L-Ledger\n   X-Quit the application");

            // Get user's menu selection input, trim and convert it to uppercase
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()){
                case "D": {
                    // Handle adding a deposit transaction
                    addDeposit(transactions);
                    break;
                }
                case "P": {
                    // Handle adding a payment (debit) transaction
                    addPayment(transactions);
                    break;
                }
                case "L": {
                    // Open the ledger submenu
                    ledgerMenu(transactions);
                    break;
                }
                case "X": {
                    // Exit the menu loop to quit the application
                    menuRunning = false;
                    break;
                }
                default: {
                    // Handle invalid user input
                    System.out.println("Error: Invalid Input");
                }
            }
        }
    }

    public static void addDeposit(ArrayList<Transaction> transactions) {
        // Flag to keep the deposit-adding loop running
        boolean addingPayment = true;

        // Loop to allow multiple deposits
        while (addingPayment) {

            // Initialize boolean for checking for invalid input
            boolean gotError=false;

            // Print divider for visual separation
            MyUtils.printDivider(50);

            // Display deposit menu prompt
            System.out.println("You're trying to add a deposit.");
            System.out.println("Would you like to backdate the deposit?\n   Y-Backdate Transaction\n   N-Use Current Date and Time\n   H-Cancel and Return to Home Menu");

            // Get user's choice for deposit input method
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "Y": {
                    // Add a backdated deposit
                    transactions.add(backdateTransaction(true));
                    break;
                }
                case "N": {
                    // Add a deposit with current date/time
                    transactions.add(currentTransaction(true));
                    break;
                }
                case "H": {
                    // Cancel and return to home menu
                    addingPayment = false;
                    continue;
                }
                default: {
                    // Handle invalid input
                    gotError=true;
                    System.out.println("Error:Invalid Input");
                }
            }

            if(!gotError){
                // Confirmation message for successful deposit
                System.out.println("Successfully added deposit.");
            }

            // Ask user if they want to add another deposit
            if (MyUtils.askQuestionGetString("Enter Y to add another deposit: ").trim().equalsIgnoreCase("Y")) {
                continue;
            }

            // Update transaction list (e.g., save to file)
            updateTransactions(transactions);

            // Exit loop
            addingPayment = false;
        }
    }

    public static void addPayment(ArrayList<Transaction> transactions) {
        // Flag to keep the payment-adding loop running
        boolean addingPayment = true;

        // Loop to allow multiple payments
        while (addingPayment) {

            // Initialize boolean for checking for invalid input
            boolean gotError=false;

            // Print divider for visual separation
            MyUtils.printDivider(50);

            // Display payment menu prompt
            System.out.println("You're trying to add a payment.");
            System.out.println("Would you like to backdate the payment?\n   Y-Backdate Transaction\n   N-Use Current Date and Time\n   H-Cancel and Return to Home Menu");

            // Get user's choice for payment input method
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "Y": {
                    // Add a backdated payment
                    transactions.add(backdateTransaction(false));
                    break;
                }
                case "N": {
                    // Add a payment with current date/time
                    transactions.add(currentTransaction(false));
                    break;
                }
                case "H": {
                    // Cancel and return to home menu
                    addingPayment = false;
                    continue;
                }
                default: {
                    // Handle invalid input
                    gotError=true;
                    System.out.println("Error:Invalid Input");
                }
            }

            if(!gotError){
                // Confirmation message for successful payment
                System.out.println("Successfully added payment.");
            }
            // Ask user if they want to add another payment
            if (MyUtils.askQuestionGetString("Enter Y to add another payment: ").trim().equalsIgnoreCase("Y")) {
                continue;
            }

            // Update transaction list (e.g., save to file)
            updateTransactions(transactions);

            // Exit loop
            addingPayment = false;
        }
    }

    public static Transaction backdateTransaction(boolean isDeposit) {
        // Variables for transaction fields
        String date;
        String time;
        String description;
        String vendor;
        double amount;

        // Prompt user to enter a valid date in the required format
        while (true) {
            try {
                date = LocalDate.parse(MyUtils.askQuestionGetString("Enter transaction date(yyyy-MM-dd): "), DATE_FORMAT)
                        .format(DATE_FORMAT);
                break;
            } catch (Exception e) {
                // Handle invalid date input
                System.out.println("Invalid date.");
            }
        }

        // Prompt user to enter a valid time in the required format
        while (true) {
            try {
                time = LocalTime.parse(MyUtils.askQuestionGetString("Enter transaction time(HH:mm:ss): "), TIME_FORMAT)
                        .format(TIME_FORMAT);
                break;
            } catch (Exception e) {
                // Handle invalid time input
                System.out.println("Invalid time.");
            }
        }

        // Collect description and vendor for the transaction
        description = MyUtils.askQuestionGetString("Enter transaction description: ").trim();
        vendor = MyUtils.askQuestionGetString("Enter transaction vendor: ").trim();

        // Get the absolute amount and apply sign based on transaction type
        amount = Math.abs(MyUtils.askQuestionGetDouble("Enter transaction amount: "));
        if (!isDeposit) {
            amount *= -1; // Make it negative for payment
        }

        // Create and return a new backdated transaction
        return new Transaction(date, time, description, vendor, amount);
    }

    public static Transaction currentTransaction(boolean isDeposit) {
        // Prompt for transaction details using current date and time
        String description = MyUtils.askQuestionGetString("Enter transaction description: ").trim();
        String vendor = MyUtils.askQuestionGetString("Enter transaction vendor: ").trim();

        // Get the absolute amount and apply sign based on transaction type
        double amount = Math.abs(MyUtils.askQuestionGetDouble("Enter transaction amount: "));
        if (!isDeposit) {
            amount *= -1; // Make it negative for payment
        }

        // Create and return a new transaction using the current timestamp
        return new Transaction(description, vendor, amount);
    }

    public static void ledgerMenu(ArrayList<Transaction> transactions) {
        // Flag to keep the ledger menu loop running
        boolean ledgerRunning = true;

        // Loop to display and handle the ledger menu until the user exits
        while (ledgerRunning) {
            // Print divider for UI clarity
            MyUtils.printDivider(50);

            // Display ledger menu options
            System.out.println("Ledger Menu");
            System.out.println("What would you like to look at?\n   A-All\n   D-Deposits\n   P-Payments\n   R-Reports\n   H-Home Menu");

            // Get user's choice and convert to uppercase for consistency
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "A": {
                    // Show all transactions
                    showAll(transactions);
                    break;
                }
                case "D": {
                    // Show only deposit transactions
                    showDeposits(transactions);
                    break;
                }
                case "P": {
                    // Show only payment transactions
                    showPayments(transactions);
                    break;
                }
                case "R": {
                    // Open the reports submenu
                    reportsMenu(transactions);
                    break;
                }
                case "H": {
                    // Exit the ledger menu and return to home
                    ledgerRunning = false;
                    break;
                }
                default: {
                    // Handle invalid input
                    System.out.println("Error:Invalid Input");
                }
            }
        }
    }


    public static void reportsMenu(ArrayList<Transaction> transactions) {
        // Flag to control the reports menu loop
        boolean reportsRunning = true;

        // Loop to keep showing the reports menu until the user exits
        while (reportsRunning) {
            // Print divider for UI clarity
            MyUtils.printDivider(50);

            // Display reports menu options
            System.out.println("Reports Menu");
            System.out.println("Which report would you like to look at?");
            System.out.println("   1-Month To Date\n   2-Previous Month\n   3-Year To Date\n   4-Previous Year\n   5-Search By Vendor\n   6-Custom Search\n   0-Back");

            // Handle user input
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "1": { // Month To Date
                    MyUtils.printDivider(50);
                    for (Transaction transaction : transactions) {
                        // Show transactions from the current month
                        if (LocalDate.parse(transaction.getDate(), DATE_FORMAT).getMonth().equals(LocalDate.now().getMonth())
                                &&LocalDate.parse(transaction.getDate(), DATE_FORMAT).getYear()==LocalDate.now().getYear()) {
                            System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(),
                                    transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                        }
                    }
                    break;
                }
                case "2": { // Previous Month
                    MyUtils.printDivider(50);
                    for (Transaction transaction : transactions) {
                        // Show transactions from the previous month
                        if (LocalDate.parse(transaction.getDate(), DATE_FORMAT).getMonth().equals(LocalDate.now().minusMonths(1).getMonth())
                        &&LocalDate.parse(transaction.getDate(), DATE_FORMAT).getYear()==LocalDate.now().minusMonths(1).getYear()) {
                            System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(),
                                    transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                        }
                    }
                    break;
                }
                case "3": { // Year To Date
                    MyUtils.printDivider(50);
                    for (Transaction transaction : transactions) {
                        // Show transactions from the current year
                        if (LocalDate.parse(transaction.getDate(), DATE_FORMAT).getYear() == LocalDate.now().getYear()) {
                            System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(),
                                    transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                        }
                    }
                    break;
                }
                case "4": { // Previous Year
                    MyUtils.printDivider(50);
                    for (Transaction transaction : transactions) {
                        // Show transactions from the previous year
                        if (LocalDate.parse(transaction.getDate(), DATE_FORMAT).getYear() == LocalDate.now().minusYears(1).getYear()) {
                            System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(),
                                    transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                        }
                    }
                    break;
                }
                case "5": { // Search by Vendor
                    // Ask user for vendor search term
                    String vendor = MyUtils.askQuestionGetString("Enter vendor to search for: ").toLowerCase().trim();

                    // Filter transactions by vendor
                    ArrayList<Transaction> vendorSearch = transactions.stream()
                            .filter(transaction -> transaction.getVendor().toLowerCase().contains(vendor))
                            .collect(Collectors.toCollection(ArrayList::new));

                    MyUtils.printDivider(50);

                    // Display matching transactions
                    for (Transaction transaction : vendorSearch) {
                        System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(),
                                transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                    }
                    break;
                }
                case "6": { // Custom Search
                    // Launch custom search menu
                    customSearch(transactions);
                    break;
                }
                case "0": { // Back to Home Menu
                    // Exit the reports menu
                    reportsRunning = false;
                    break;
                }
                default: {
                    // Handle invalid user input
                    System.out.println("Error:Invalid Input");
                }
            }
        }
    }

    public static void customSearch(ArrayList<Transaction> transactions) {
        String startDate;
        String endDate;

        // UI divider and instructions
        MyUtils.printDivider(50);
        System.out.println("Choose how you'd like to filter our selection. Leave option blank to not filter by that option.");

        // Get start date filter (optional)
        while (true) {
            try {
                startDate = MyUtils.askQuestionGetString("Enter the earliest date you'd like to filter(yyyy-MM-dd): ").trim();
                if (!startDate.isEmpty()) {
                    startDate = LocalDate.parse(startDate, DATE_FORMAT).format(DATE_FORMAT);
                } else {
                    startDate = LocalDate.parse("1900-01-01", DATE_FORMAT).format(DATE_FORMAT); // Default earliest date
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }

        // Get end date filter (optional)
        while (true) {
            try {
                endDate = MyUtils.askQuestionGetString("Enter the latest date you'd like to filter(yyyy-MM-dd): ").trim();
                if (!endDate.isEmpty()) {
                    endDate = LocalDate.parse(endDate, DATE_FORMAT).format(DATE_FORMAT);
                } else {
                    endDate = LocalDate.now().format(DATE_FORMAT); // Default latest date
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }

        // Get optional description and vendor filters
        String description = MyUtils.askQuestionGetString("Description: ").toLowerCase().trim();
        String vendor = MyUtils.askQuestionGetString("Vendor: ").toLowerCase().trim();

        // Get optional minimum amount filter
        double minAmount;
        try {
            minAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Minimum Amount: ").trim()));
        } catch (Exception e) {
            minAmount = 0d;
        }

        // Get optional maximum amount filter
        Double maxAmount;
        try {
            maxAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Maximum Amount: ").trim()));
        } catch (Exception e) {
            maxAmount = null;
        }

        // Filter transactions based on all input criteria
        ArrayList<Transaction> filteredTransactions = filterTransactions(transactions, startDate, endDate, description, vendor, minAmount, maxAmount);

        // Display results or indicate no matches found
        if (filteredTransactions.isEmpty()) {
            System.out.println("No items match search.");
        }
        showAll(filteredTransactions);
    }

    public static ArrayList<Transaction> filterTransactions(ArrayList<Transaction> transactions, String startDate, String endDate, String description, String vendor, double minAmount, Double maxAmount) {
        // Return a filtered list based on input criteria using Stream API
        return transactions.stream()
                // Filter by date range
                .filter(transaction -> LocalDate.parse(transaction.getDate(), DATE_FORMAT).isAfter(LocalDate.parse(startDate, DATE_FORMAT)))
                .filter(transaction -> LocalDate.parse(transaction.getDate(), DATE_FORMAT).isBefore(LocalDate.parse(endDate, DATE_FORMAT)))
                // Filter by description if provided
                .filter(transaction -> description == null || description.isEmpty() || transaction.getDescription().toLowerCase().contains(description.toLowerCase()))
                // Filter by vendor if provided
                .filter(transaction -> vendor == null || vendor.isEmpty() || transaction.getVendor().toLowerCase().contains(vendor.toLowerCase()))
                // Filter by minimum amount
                .filter(transaction -> Math.abs(transaction.getAmount()) >= minAmount)
                // Filter by maximum amount if provided
                .filter(transaction -> maxAmount == null || Math.abs(transaction.getAmount()) <= maxAmount)
                // Collect results into a new list
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public static void showAll(ArrayList<Transaction> transactions) {
        // Print divider for UI clarity
        MyUtils.printDivider(50);

        // Display all transactions in a formatted table
        for (Transaction transaction : transactions) {
            System.out.printf("%s|%s|%s|%s|%.2f\n",
                    transaction.getDate(), transaction.getTime(),
                    transaction.getDescription(), transaction.getVendor(),
                    transaction.getAmount());
        }
    }

    public static void showDeposits(ArrayList<Transaction> transactions) {
        // Print divider for UI clarity
        MyUtils.printDivider(50);

        // Display only deposit (positive amount) transactions
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.printf("%s|%s|%s|%s|%.2f\n",
                        transaction.getDate(), transaction.getTime(),
                        transaction.getDescription(), transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void showPayments(ArrayList<Transaction> transactions) {
        // Print divider for UI clarity
        MyUtils.printDivider(50);

        // Display only payment (negative amount) transactions
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%s|%s|%s|%s|%.2f\n",
                        transaction.getDate(), transaction.getTime(),
                        transaction.getDescription(), transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    public static void updateTransactions(ArrayList<Transaction> transactions) {
        // Sort transactions (assumes Transaction class implements Comparable)
        Collections.sort(transactions,Collections.reverseOrder());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFilePath))) {
            // Write header row
            String header = "date|time|description|vendor|amount\n";
            writer.write(header);

            // Write each transaction in the correct format
            for (Transaction transaction : transactions) {
                String line = String.format("%s|%s|%s|%s|%.2f\n",
                        transaction.getDate(), transaction.getTime(),
                        transaction.getDescription(), transaction.getVendor(),
                        transaction.getAmount());
                writer.write(line);
            }

            // Close the writer and confirm update
            writer.close();
            System.out.println("Transactions successfully updated.");

        } catch (Exception e) {
            // Wrap and rethrow exceptions as runtime
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Transaction> readTransactions() {
        // Initialize the transaction list
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            // Open file and buffer the reader
            FileReader fr = new FileReader(transactionsFilePath);
            BufferedReader readTransactions = new BufferedReader(fr);

            // Read and discard the header line
            String line;
            readTransactions.readLine();

            // Read each subsequent line
            while ((line = readTransactions.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines

                // Split transaction details by pipe character
                String[] splitLine = line.split("\\|");

                // Parse line into a Transaction object and add to list
                Transaction transaction = new Transaction(
                        splitLine[0], splitLine[1], splitLine[2], splitLine[3],
                        Double.parseDouble(splitLine[4]));
                transactions.add(transaction);
            }

            // Close reader
            readTransactions.close();

        } catch (Exception e) {
            // Wrap and rethrow exceptions as runtime
            throw new RuntimeException(e);
        }

        // Return the parsed list of transactions
        return transactions;
    }
}
