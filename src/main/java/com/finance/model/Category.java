package com.finance.model;


import java.awt.Color;
import java.io.Serializable;



/**
 * Category class for transaction categorization
 * Demonstrates encapsulation
 */
public class Category implements Serializable {
    // --- extracted constants to remove magic literals ---
    private static final String DEFAULT_NAME = "Uncategorized";
    private static final String DEFAULT_DESCRIPTION = "Default category";
    private static final Color DEFAULT_COLOR = Color.GRAY;

    /** Shared default category instance. */
    public static final Category DEFAULT_CATEGORY =
            new Category(0, DEFAULT_NAME, TransactionType.EXPENSE);

    /**
     * @deprecated Use {@link #DEFAULT_CATEGORY} instead.  
     * Kept only for backward-compatibility while migrating existing code.
     */
    @Deprecated
    public static final Category DEFAULT = DEFAULT_CATEGORY;

    private int id;
    private String name;
    private TransactionType type;
    private String description;
    private Color color;
    
    public Category(int id, String name, TransactionType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = "";
        this.color = DEFAULT_COLOR;
    }
    
    public Category(String name, String description, Color color) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.color = color;
        this.type = null; // Applicable to both income and expense
    }

    public Category(String name, String description, Color color, TransactionType type) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.color = color;
        this.type = type;
    }
    public Category(String name, String description, TransactionType type) {
        this(name, description, DEFAULT_COLOR, type);
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public TransactionType getApplicableType() {
        return type;
    }

    public void setApplicableType(TransactionType applicableType) {
        this.type = applicableType;
    }

    public boolean isApplicableTo(TransactionType transactionType) {
        return type == null || type == transactionType;
    }
    
    @Override
    public String toString() {
        return name;
    }
}