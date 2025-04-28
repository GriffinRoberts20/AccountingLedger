package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class AccountingLedgerApp {

    static String transactionsFilePath="src/main/resources/transactions.csv";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        ArrayList<Transaction> transactions=readTransactions();
        homeMenu(transactions);
        updateTransactions(transactions);
    }

    public static void homeMenu(ArrayList<Transaction> transactions){
        boolean menuRunning=true;
        while(menuRunning) {
            MyUtils.printDivider(50);
            System.out.println("Home Menu");
            System.out.println("What would you like to do today?\n   D-Add Deposit\n   P-Make Payment(Debit)\n   L-Ledger\n   X-Quit the application");
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()){
                case "D":{
                    addDeposit(transactions);
                    break;
                }
                case "P":{
                    addPayment(transactions);
                    break;
                }
                case "L":{
                    ledgerMenu(transactions);
                    break;
                }
                case "X":{
                    menuRunning=false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }
            }
        }
    }

    public static void addDeposit(ArrayList<Transaction> transactions){
        boolean addingPayment=true;
        while(addingPayment){
            MyUtils.printDivider(50);
            System.out.println("You're trying to add a deposit.");
            System.out.println("Would you like to backdate the deposit?\n   Y-Backdate Transaction\n   N-Use Current Date and Time\n   H-Cancel and Return to Home Menu");
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "Y": {
                    transactions.add(backdateTransaction(true));
                    break;
                }
                case "N": {
                    transactions.add(currentTransaction(true));
                    break;
                }
                case "H": {
                    addingPayment = false;
                    continue;
                }
                default: {
                    System.out.println("Error:Invalid Input");
                }
            }
            System.out.println("Successfully added deposit.");
            if(MyUtils.askQuestionGetString("Enter Y to add another deposit: ").trim().equalsIgnoreCase("Y")){
                continue;
            }
            addingPayment=false;
        }
    }

    public static void addPayment(ArrayList<Transaction> transactions){
        boolean addingPayment=true;
        while(addingPayment){
            MyUtils.printDivider(50);
            System.out.println("You're trying to add a payment.");
            System.out.println("Would you like to backdate the payment?\n   Y-Backdate Transaction\n   N-Use Current Date and Time\n   H-Cancel and Return to Home Menu");
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()) {
                case "Y": {
                    transactions.add(backdateTransaction(false));
                    break;
                }
                case "N": {
                    transactions.add(currentTransaction(false));
                    break;
                }
                case "H": {
                    addingPayment = false;
                    continue;
                }
                default: {
                    System.out.println("Error:Invalid Input");
                }
            }
            System.out.println("Successfully added payment.");
            if(MyUtils.askQuestionGetString("Enter Y to add another payment: ").trim().equalsIgnoreCase("Y")){
                continue;
            }
            Collections.sort(transactions);
            addingPayment=false;
        }
    }

    public static Transaction backdateTransaction(boolean isDeposit){
        String date;
        String time;
        String description;
        String vendor;
        double amount;
        while(true){
            try {
                date = LocalDate.parse(MyUtils.askQuestionGetString("Enter transaction date(yyyy-MM-dd): "),DATE_FORMAT).format(DATE_FORMAT);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date.");
            }
        }
        while(true){
            try {
                time = LocalTime.parse(MyUtils.askQuestionGetString("Enter transaction time(HH:mm:ss): "),TIME_FORMAT).format(TIME_FORMAT);
                break;
            } catch (Exception e) {
                System.out.println("Invalid time.");
            }
        }
        description=MyUtils.askQuestionGetString("Enter transaction description: ").trim();
        vendor=MyUtils.askQuestionGetString("Enter transaction vendor: ").trim();
        amount=Math.abs(MyUtils.askQuestionGetDouble("Enter transaction amount: "));
        if(!isDeposit){
            amount*=-1;
        }
        return new Transaction(date,time,description,vendor,amount);
    }

    public static Transaction currentTransaction(boolean isDeposit){
        String description=MyUtils.askQuestionGetString("Enter transaction description: ").trim();
        String vendor=MyUtils.askQuestionGetString("Enter transaction vendor: ").trim();
        double amount=Math.abs(MyUtils.askQuestionGetDouble("Enter transaction amount: "));
        if(!isDeposit){
            amount*=-1;
        }
        return new Transaction(description,vendor,amount);
    }

    public static void ledgerMenu(ArrayList<Transaction> transactions){
        boolean ledgerRunning =true;
        while(ledgerRunning) {
            MyUtils.printDivider(50);
            System.out.println("Ledger Menu");
            System.out.println("What would you like to look at?\n   A-All\n   D-Deposits\n   P-Payments\n   R-Reports\n   H-Home Menu");
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()){
                case "A":{
                    showAll(transactions);
                    break;
                }
                case "D":{
                    showDeposits(transactions);
                    break;
                }
                case "P":{
                    showPayments(transactions);
                    break;
                }
                case "R":{
                    reportsMenu(transactions);
                    break;
                }
                case "H":{
                    ledgerRunning =false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }
            }
        }
    }

    public static void reportsMenu(ArrayList<Transaction> transactions){
        boolean reportsRunning =true;
        while(reportsRunning) {
            MyUtils.printDivider(50);
            System.out.println("Reports Menu");
            System.out.println("Which report would you like to look at?");
            System.out.println("   1-Month To Date\n   2-Previous Month\n   3-Year To Date\n   4-Previous Year\n   5-Search By Vendor\n   6-Custom Search\n   0-Back");
            switch (MyUtils.askQuestionGetString("Enter command: ").toUpperCase().trim()){
                case "1":{ //Month To Date
                    MyUtils.printDivider(50);
                    for(Transaction transaction:transactions){
                        if(LocalDate.parse(transaction.getDate(),DATE_FORMAT).getMonth().equals(LocalDate.now().getMonth())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",transaction.getDate(),transaction.getTime(),transaction.getDescription(),transaction.getVendor(),transaction.getAmount());
                        }
                    }
                    break;
                }
                case "2":{ //Previous Month
                    MyUtils.printDivider(50);
                    for(Transaction transaction:transactions){
                        if(LocalDate.parse(transaction.getDate(),DATE_FORMAT).getMonth().equals(LocalDate.now().minusMonths(1).getMonth())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",transaction.getDate(),transaction.getTime(),transaction.getDescription(),transaction.getVendor(),transaction.getAmount());
                        }
                    }
                    break;
                }
                case "3":{ //Year To Date
                    MyUtils.printDivider(50);
                    for(Transaction transaction:transactions){
                        if(LocalDate.parse(transaction.getDate(),DATE_FORMAT).getYear()==(LocalDate.now().getYear())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",transaction.getDate(),transaction.getTime(),transaction.getDescription(),transaction.getVendor(),transaction.getAmount());
                        }
                    }
                    break;
                }
                case "4":{ //Previous Year
                    MyUtils.printDivider(50);
                    for(Transaction transaction:transactions){
                        if(LocalDate.parse(transaction.getDate(),DATE_FORMAT).getYear()==(LocalDate.now().minusYears(1).getYear())){
                            System.out.printf("%s|%s|%s|%s|%.2f\n",transaction.getDate(),transaction.getTime(),transaction.getDescription(),transaction.getVendor(),transaction.getAmount());
                        }
                    }
                    break;
                }
                case "5": { //Search by Vendor
                    String vendor = MyUtils.askQuestionGetString("Enter vendor to search for: ").toLowerCase().trim();
                    ArrayList<Transaction> vendorSearch = transactions.stream()
                            .filter(transaction -> transaction.getVendor().toLowerCase().contains(vendor))
                            .collect(Collectors.toCollection(ArrayList::new));
                    MyUtils.printDivider(50);
                    for (Transaction transaction : vendorSearch) {
                        System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                    }
                    break;
                }
                case "6":{
                    customSearch(transactions);
                    break;
                }
                case "0":{ //Back to Home Menu
                    reportsRunning =false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }
            }
        }
    }

    public static void customSearch(ArrayList<Transaction> transactions){
        String startDate;
        String endDate;
        MyUtils.printDivider(50);
        System.out.println("Choose how you'd like to filter our selection. Leave option blank to not filter by that option.");
        while(true) {
            try {
                startDate = MyUtils.askQuestionGetString("Enter the earliest date you'd like to filter(yyyy-MM-dd): ").trim();
                if(!startDate.isEmpty()) {
                    startDate = LocalDate.parse(startDate, DATE_FORMAT).format(DATE_FORMAT);
                }
                else{
                    startDate = LocalDate.parse("1900-01-01",DATE_FORMAT).format(DATE_FORMAT);
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }
        while(true) {
            try {
                endDate = MyUtils.askQuestionGetString("Enter the latest date you'd like to filter(yyyy-MM-dd): ").trim();
                if(!endDate.isEmpty()) {
                    endDate = LocalDate.parse(endDate, DATE_FORMAT).format(DATE_FORMAT);
                }
                else{
                    endDate = LocalDate.now().format(DATE_FORMAT);
                }
                break;
            } catch (Exception e) {
                System.out.println("Invalid input. Leave blank to not filter.");
            }
        }
        String description=MyUtils.askQuestionGetString("Description: ").toLowerCase().trim();
        String vendor=MyUtils.askQuestionGetString("Vendor: ").toLowerCase().trim();
        double minAmount;
        Double maxAmount;
        try{
            minAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Minimum Amount: ").trim()));
        } catch (Exception e){
            minAmount=0d;
        }
        try{
            maxAmount = Math.abs(Double.parseDouble(MyUtils.askQuestionGetString("Maximum Amount: ").trim()));
        } catch (Exception e){
            maxAmount=null;
        }

        ArrayList<Transaction> filteredTransactions=filterTransactions(transactions,startDate,endDate,description,vendor,minAmount,maxAmount);
        if(filteredTransactions.isEmpty()){
            System.out.println("No items match search.");
        }
        showAll(filteredTransactions);
    }

    public static ArrayList<Transaction> filterTransactions(ArrayList<Transaction> transactions,String startDate,String endDate,String description,String vendor,double minAmount,Double maxAmount){
        return transactions.stream()
                .filter(transaction -> LocalDate.parse(transaction.getDate(),DATE_FORMAT).isAfter(LocalDate.parse(startDate,DATE_FORMAT)))
                .filter(transaction -> LocalDate.parse(transaction.getDate(),DATE_FORMAT).isBefore(LocalDate.parse(endDate,DATE_FORMAT)))
                .filter(transaction -> description==null||description.isEmpty()||transaction.getDescription().contains(description))
                .filter(transaction -> vendor==null||vendor.isEmpty()||transaction.getVendor().toLowerCase().contains(vendor))
                .filter(transaction -> Math.abs(transaction.getAmount())>=minAmount)
                .filter(transaction -> maxAmount==null||Math.abs(transaction.getAmount())<=maxAmount)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void showAll(ArrayList<Transaction> transactions){
        MyUtils.printDivider(50);
        for(Transaction transaction:transactions){
            System.out.printf("%s|%s|%s|%s|%.2f\n",transaction.getDate(),transaction.getTime(),transaction.getDescription(),transaction.getVendor(),transaction.getAmount());
        }
    }

    public static void showDeposits(ArrayList<Transaction> transactions){
        MyUtils.printDivider(50);
        for(Transaction transaction:transactions){
            if(transaction.getAmount()>0) {
                System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    public static void showPayments(ArrayList<Transaction> transactions){
        MyUtils.printDivider(50);
        for(Transaction transaction:transactions){
            if(transaction.getAmount()<0) {
                System.out.printf("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        }
    }

    public static void updateTransactions(ArrayList<Transaction> transactions){
        Collections.sort(transactions);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsFilePath))) {
            String header="date|time|description|vendor|amount";
            writer.write(header+"/n");

            for(Transaction transaction:transactions){
                String line = String.format("%s|%s|%s|%s|%.2f\n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
                writer.write(line);
            }

            writer.close();
            System.out.println("Transactions successfully written to " + transactionsFilePath);

        } catch(Exception e){
            throw new RuntimeException(e);        }
    }

    public static ArrayList<Transaction> readTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        try{
            FileReader fr = new FileReader(transactionsFilePath);
            BufferedReader readTransactions = new BufferedReader(fr);
            String line;
            readTransactions.readLine();
            while ((line = readTransactions.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] splitLine = line.split("\\|");
                Transaction transaction = new Transaction(splitLine[0], splitLine[1], splitLine[2], splitLine[3],Double.parseDouble(splitLine[4]));
                transactions.add(transaction);
            }
            readTransactions.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }
}
