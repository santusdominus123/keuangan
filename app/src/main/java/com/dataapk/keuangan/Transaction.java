package com.dataapk.keuangan;

import java.io.Serializable;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private double amount;
    private String type;
    private String category;
    private String date;

    // Constructors
    public Transaction() {}

    public Transaction(String title, double amount, String type, String category, String date) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}