package com.finance.data;

import com.finance.model.*;
import java.awt.Color;
import java.io.*;
import java.util.*;
import java.awt.Color;



import com.finance.model.*;

import java.io.*;
import java.util.*;

/**
 * File-based implementation of DataManager
 * Demonstrates interface implementation and file I/O
 */
public class FileDataManager implements DataManager {
    // ArrayList to store transactions (ArrayList usage)
    private List<Transaction> transactions;
    private List<Budget> budgets;
    private List<Category> customCategories;
    private UserPreferences userPreferences;
    
    private final String DATA_DIR = System.getProperty("user.home") + File.separator + "FinanceTracker";
    private final String TRANSACTIONS_FILE = DATA_DIR + File.separator + "transactions.dat";
    private final String BUDGETS_FILE = DATA_DIR + File.separator + "budgets.dat";
    private final String CATEGORIES_FILE = DATA_DIR + File.separator + "categories.dat";
    private final String PREFERENCES_FILE = DATA_DIR + File.separator + "preferences.dat";
    
    // Singleton pattern (Static keyword)
    private static FileDataManager instance;
    
    /**
     * Private constructor for singleton pattern
     */
    private FileDataManager() {
        this.transactions = new ArrayList<>();
        this.budgets = new ArrayList<>();
        this.customCategories = new ArrayList<>();
        this.userPreferences = new UserPreferences();
        
        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        System.out.println(dataDir.getAbsolutePath());
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Load data from files
        loadData();
    }
    
    /**
     * Get singleton instance
     * @return the singleton instance
     */
    public static synchronized FileDataManager getInstance() {
        if (instance == null) {
            instance = new FileDataManager();
        }
        return instance;
    }
    
    @Override
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }
    
    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
    
    @Override
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
            saveData();
        }
    }
    
    @Override
    public void saveTransaction(Transaction transaction) {
        addTransaction(transaction);
    }
    
    @Override
    public void updateTransaction(Transaction transaction) {
        if (transaction == null) return;
        
        for (int i = 0; i < transactions.size(); i++) {
            // Transaction ID is always a String as defined in the Transaction class
            if (transactions.get(i).getId().equals(transaction.getId())) {
                transactions.set(i, transaction);
                saveData();
                return;
            }
        }
    }
    
    @Override
    public void deleteTransaction(String transactionId) {
        if (transactionId != null) {
            transactions.removeIf(t -> transactionId.equals(t.getId()));
            saveData();
        }
    }
    
    @Override
    public Transaction getTransactionById(String id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }
    
    /**
     * Load transactions from storage
     * 
     * @return List of all transactions
     */
    @Override
    public List<Transaction> loadTransactions() {
        // Check if transactions list is already loaded
        if (transactions.isEmpty()) {
            try {
                File file = new File(TRANSACTIONS_FILE);
                if (file.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        @SuppressWarnings("unchecked")
                        List<Transaction> loadedTransactions = (List<Transaction>) ois.readObject();
                        transactions = loadedTransactions;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading transactions: " + e.getMessage());
                // Initialize with empty list if there's an error
                transactions = new ArrayList<>();
            }
        }
        
        // Return a defensive copy of the transactions list
        return new ArrayList<>(transactions);
    }
    
    @Override
    public List<Category> getCategories() {
        return new ArrayList<>(customCategories);
    }
    
    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(customCategories);
    }
    
    @Override
    public void addCategory(Category category) {
        if (category != null) {
            customCategories.add(category);
            saveData();
        }
    }
    
    @Override
    public void saveCategory(Category category) {
        addCategory(category);
    }
    
    @Override
    public void updateCategory(Category category) {
        if (category == null) return;
        
        for (int i = 0; i < customCategories.size(); i++) {
            if (customCategories.get(i).getId() == category.getId()) {
                customCategories.set(i, category);
                saveData();
                return;
            } else if (customCategories.get(i).getName().equals(category.getName())) {
                customCategories.set(i, category);
                saveData();
                return;
            }
        }
    }
    

    @Override
    public void deleteCategory(int categoryId) {
        customCategories.removeIf(c -> c.getId() == categoryId);
        saveData();
    }
    
    @Override
    public void deleteCategory(Category category) {
        customCategories.removeIf(c -> c.getName().equals(category.getName()));
        saveData();
    }
    
    /**
     * Load categories from storage
     * 
     * @return List of all categories
     */
    @Override
    public List<Category> loadCategories() {
        // Check if categories list is already loaded
        if (customCategories.isEmpty()) {
            try {
                File file = new File(CATEGORIES_FILE);
                if (file.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        @SuppressWarnings("unchecked")
                        List<Category> loadedCategories = (List<Category>) ois.readObject();
                        customCategories = loadedCategories;
                    }
                } else {
                    // Initialize with default categories if file doesn't exist
                    initializeDefaultCategories();
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading categories: " + e.getMessage());
                // Initialize with default categories if there's an error
                initializeDefaultCategories();
            }
        }
        
        // Return a defensive copy of the categories list
        return new ArrayList<>(customCategories);
    }
    
    @Override
    public void saveBudget(Budget budget) {
        budgets.add(budget);
        saveData();
    }
    
    @Override
    public void updateBudget(Budget budget) {
        for (int i = 0; i < budgets.size(); i++) {
            Budget existing = budgets.get(i);
            if (existing.getPeriod().equals(budget.getPeriod()) && 
                existing.getCategory().equals(budget.getCategory())) {
                budgets.set(i, budget);
                saveData();
                return;
            }
        }
    }
    
    @Override
    public void deleteBudget(Budget budget) {
        budgets.removeIf(b -> 
            b.getPeriod().equals(budget.getPeriod()) && 
            b.getCategory().equals(budget.getCategory()));
        saveData();
    }
    
    @Override
    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }
    
    /**
     * Load budgets from storage
     * 
     * @return List of all budgets
     */
    @Override
    public List<Budget> loadBudgets() {
        // Check if budgets list is already loaded
        if (budgets.isEmpty()) {
            try {
                File file = new File(BUDGETS_FILE);
                if (file.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        @SuppressWarnings("unchecked")
                        List<Budget> loadedBudgets = (List<Budget>) ois.readObject();
                        budgets = loadedBudgets;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading budgets: " + e.getMessage());
                // Initialize with empty list if there's an error
                budgets = new ArrayList<>();
            }
        }
        
        // Return a defensive copy of the budgets list
        return new ArrayList<>(budgets);
    }
    
    @Override
    public void saveUserPreferences(UserPreferences preferences) {
        this.userPreferences = preferences;
        saveData();
    }
    
    @Override
    public UserPreferences loadUserPreferences() {
        return userPreferences;
    }
    


    @Override
    public void saveData() {
        try {
            // Save transactions
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(TRANSACTIONS_FILE))) {
                oos.writeObject(transactions);
            }
            
            // Save budgets
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(BUDGETS_FILE))) {
                oos.writeObject(budgets);
            }
            
            // Save categories
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(CATEGORIES_FILE))) {
                oos.writeObject(customCategories);
            }
            
            // Save preferences
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(PREFERENCES_FILE))) {
                oos.writeObject(userPreferences);
            }
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void loadData() {
        // Load transactions
        try {
            File file = new File(TRANSACTIONS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file))) {
                    transactions = (List<Transaction>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
            transactions = new ArrayList<>();
        }
        
        // Load budgets
        try {
            File file = new File(BUDGETS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file))) {
                    budgets = (List<Budget>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
            budgets = new ArrayList<>();
        }
        
        // Load categories
        try {
            File file = new File(CATEGORIES_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file))) {
                    customCategories = (List<Category>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading categories: " + e.getMessage());
            customCategories = new ArrayList<>();
        }
        
        // Load preferences
        try {
            File file = new File(PREFERENCES_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(file))) {
                    userPreferences = (UserPreferences) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading preferences: " + e.getMessage());
            userPreferences = new UserPreferences();
        }
        
        // Initialize with default categories if none exist
        if (customCategories.isEmpty()) {
            initializeDefaultCategories();
        }
    }
    
    /**
     * Initialize default categories
     */
    private void initializeDefaultCategories() {
        customCategories.add(new Category("Salary", "Regular employment income", TransactionType.INCOME));
        customCategories.add(new Category("Investments", "Income from investments", TransactionType.INCOME));
        customCategories.add(new Category("Gifts", "Money received as gifts", TransactionType.INCOME));
        
        customCategories.add(new Category("Food", "Groceries and dining", TransactionType.EXPENSE));
        customCategories.add(new Category("Housing", "Rent or mortgage payments", TransactionType.EXPENSE));
        customCategories.add(new Category("Transportation", "Car, public transit, etc.", TransactionType.EXPENSE));
        customCategories.add(new Category("Utilities", "Electricity, water, etc.", TransactionType.EXPENSE));
        customCategories.add(new Category("Entertainment", "Movies, games, etc.", TransactionType.EXPENSE));
        customCategories.add(new Category("Healthcare", "Medical expenses", TransactionType.EXPENSE));
        
        saveData();
    }
    
    @Override
    public boolean exportData(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactions", transactions);
            data.put("budgets", budgets);
            data.put("categories", customCategories);
            data.put("preferences", userPreferences);
            
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Map<String, Object> data = (Map<String, Object>) ois.readObject();
            
            transactions = (List<Transaction>) data.get("transactions");
            budgets = (List<Budget>) data.get("budgets");
            customCategories = (List<Category>) data.get("categories");
            userPreferences = (UserPreferences) data.get("preferences");
            
            // Save imported data to files
            saveData();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
