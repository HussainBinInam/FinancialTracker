package com.finance.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manager class for predefined and custom categories
 * Demonstrates static methods and singleton pattern
 */
public class CategoryManager {
    // Singleton pattern with static instance
    private static CategoryManager instance;
    
    // ArrayList of categories (ArrayList usage)
    private final List<Category> categories;
    
    // Private constructor for singleton pattern
    private CategoryManager() {
        // Initialize with predefined categories
        categories = new ArrayList<>(Arrays.asList(
            // Income categories
            new Category("Salary", "Regular employment income", new Color(76, 175, 80), TransactionType.INCOME),
            new Category("Investment", "Income from investments", new Color(0, 150, 136), TransactionType.INCOME),
            new Category("Gifts", "Money received as gifts", new Color(121, 85, 72), TransactionType.INCOME),
            new Category("Bonus", "Work bonuses or incentives", new Color(255, 152, 0), TransactionType.INCOME),
            
            // Expense categories
            new Category("Housing", "Rent, mortgage, repairs", new Color(233, 30, 99), TransactionType.EXPENSE),
            new Category("Food", "Groceries and dining out", new Color(156, 39, 176), TransactionType.EXPENSE),
            new Category("Transportation", "Car, public transit, ride sharing", new Color(33, 150, 243), TransactionType.EXPENSE),
            new Category("Utilities", "Electricity, water, internet", new Color(3, 169, 244), TransactionType.EXPENSE),
            new Category("Entertainment", "Movies, games, hobbies", new Color(255, 87, 34), TransactionType.EXPENSE),
            new Category("Healthcare", "Doctor visits, medicine", new Color(96, 125, 139), TransactionType.EXPENSE),
            new Category("Education", "Tuition, books, courses", new Color(63, 81, 181), TransactionType.EXPENSE),
            new Category("Shopping", "Clothing, electronics", new Color(205, 220, 57), TransactionType.EXPENSE),
            
            // General category
            Category.DEFAULT
        ));
    }
    
    // Static method to get instance (Static keyword)
    public static synchronized CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }
    
    // Get all categories
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }
    
    // Get categories by transaction type
    public List<Category> getCategoriesByType(TransactionType type) {
        List<Category> filteredCategories = new ArrayList<>();
        
        for (Category category : categories) {
            if (category.isApplicableTo(type)) {
                filteredCategories.add(category);
            }
        }
        
        return filteredCategories;
    }
    
    // Add a new category
    public void addCategory(Category category) {
        categories.add(category);
    }
    
    // Remove category
    public boolean removeCategory(Category category) {
        if (category == Category.DEFAULT) {
            return false; // Can't remove default category
        }
        return categories.remove(category);
    }
    
    // Find category by name
    public Category findCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return Category.DEFAULT;
    }
}
