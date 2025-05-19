package com.finance.data;


import com.finance.model.*;
import java.util.List;




import com.finance.model.*;
import java.util.List;

/**
 * Interface defining data persistence operations
 * Demonstrates interface usage
 */
public interface DataManager {
    // Transaction operations
    void addTransaction(Transaction transaction);
    void saveTransaction(Transaction transaction);
    void updateTransaction(Transaction transaction);
    void deleteTransaction(String transactionId);
    List<Transaction> getAllTransactions();
    List<Transaction> getTransactions();
    List<Transaction> loadTransactions();
    Transaction getTransactionById(String id);
    
    // Budget operations
    void saveBudget(Budget budget);
    void updateBudget(Budget budget);
    void deleteBudget(Budget budget);
    List<Budget> getAllBudgets();
    List<Budget> loadBudgets();

    // Category operations
    void addCategory(Category category);
    void saveCategory(Category category);
    void updateCategory(Category category);
    void deleteCategory(Category category);
    void deleteCategory(int categoryId);
    List<Category> getAllCategories();
    List<Category> getCategories();
    List<Category> loadCategories();
    
    // Other operations
    void saveUserPreferences(UserPreferences preferences);
    UserPreferences loadUserPreferences();
    
    // Data management
    void saveData();
    void loadData();
    
    // Data backup and restore
    boolean exportData(String filePath);
    boolean importData(String filePath);
}
