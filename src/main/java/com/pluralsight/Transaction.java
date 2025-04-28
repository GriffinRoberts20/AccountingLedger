package com.pluralsight;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Comparable<Transaction>{

    private String date;
    private String time;
    private String description;
    private String vendor;
    private double amount;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Transaction(String date, String time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public Transaction(String description, String vendor, double amount) {
        this.date= LocalDate.now().format(DATE_FORMAT);
        this.time= LocalTime.now().format(TIME_FORMAT);
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    public int compareTo(Transaction other){
        int dateCompare = this.date.compareTo(other.date);
        return (dateCompare != 0) ? dateCompare : this.time.compareTo(other.time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
