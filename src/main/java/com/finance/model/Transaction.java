package com.finance.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Abstract base class for all financial transactions.
 * Demonstrates encapsulation through private fields and public getters/setters.
 */
public abstract class Transaction implements Serializable {
    // Encapsulation with private fields
    private final String id;
    private double amount;
    private String description;
    private LocalDate date;
    private Category category;
    private String notes;
    
    // Constructor
    public Transaction(double amount, String description, LocalDate date, Category category) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
        this.notes = "";
    }

    // Abstract method (polymorphism)
    public abstract TransactionType getType();

    // Getters and setters (encapsulation)
    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCategoryId() {
        return category.getId();
    }

    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return String.format("%s - $%.2f - %s - %s",
                date, amount, category.getName(), description);
    }
}
