package com.finance.ui;

import com.finance.model.Category;
import com.finance.model.Transaction;
import com.finance.model.TransactionType;
import com.finance.model.IncomeTransaction;
import com.finance.model.ExpenseTransaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.UUID;

/**
 * Dialog for adding or editing transactions
 */
public class TransactionDialog extends JDialog {
    // UI Components
    private JTextField dateField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    private JTextField descriptionField;
    private JTextField amountField;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Data
    private Transaction transaction;
    private List<Category> categories;
    private boolean isNewTransaction;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param title Dialog title
     * @param transaction Transaction to edit, or null for a new transaction
     * @param categories List of available categories
     */
    public TransactionDialog(Window parent, String title, Transaction transaction, List<Category> categories) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        
        this.transaction = transaction;
        this.categories = categories;
        this.isNewTransaction = (transaction == null);
        
        setupUI();
        loadData();
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        setSize(400, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Date field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Date (MM/DD/YYYY):"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        dateField = new JTextField(12);
        formPanel.add(dateField, gbc);
        
        // Type combobox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        typeCombo = new JComboBox<>(new String[] {"Income", "Expense"});
        formPanel.add(typeCombo, gbc);
        
        // Category combobox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        categoryCombo = new JComboBox<>();
        formPanel.add(categoryCombo, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);
        
        // Amount field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Amount:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);
        
        // Notes area
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Notes:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        formPanel.add(notesScrollPane, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to main panel
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to dialog
        add(panel);
        
        // Add action listeners
        saveButton.addActionListener(e -> saveTransaction());
        cancelButton.addActionListener(e -> dispose());
        
        // Update categories when transaction type changes
        typeCombo.addActionListener(e -> updateCategoryCombo());
    }
    
    /**
     * Load data into UI components
     */
    private void loadData() {
        // Set today's date as default for new transactions
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        if (isNewTransaction) {
            dateField.setText(LocalDate.now().format(formatter));
            typeCombo.setSelectedIndex(0); // Default to Income
        } else {
            // Load existing transaction data
            dateField.setText(transaction.getDate().format(formatter));
            typeCombo.setSelectedItem(transaction.getType().getDisplayName());
            descriptionField.setText(transaction.getDescription());
            amountField.setText(String.format("%.2f", transaction.getAmount()));
            notesArea.setText(transaction.getNotes());
        }
        
        // Update category combo based on selected type
        updateCategoryCombo();
        
        // Set the category if editing an existing transaction
        if (!isNewTransaction) {
            categoryCombo.setSelectedItem(transaction.getCategory().getName());
        }
    }
    
    /**
     * Update the category combo box based on the selected transaction type
     */
    private void updateCategoryCombo() {
        categoryCombo.removeAllItems();
        
        TransactionType selectedType = 
            "Income".equals(typeCombo.getSelectedItem()) ? 
            TransactionType.INCOME : TransactionType.EXPENSE;
            
        for (Category category : categories) {
            if (category.getType() == selectedType) {
                categoryCombo.addItem(category.getName());
            }
        }
    }
    
    /**
     * Save the transaction
     */
    private void saveTransaction() {
        try {
            // Parse and validate input
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date;
            try {
                date = LocalDate.parse(dateField.getText(), formatter);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use MM/DD/YYYY format.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            TransactionType type = 
                "Income".equals(typeCombo.getSelectedItem()) ? 
                TransactionType.INCOME : TransactionType.EXPENSE;
                
            String categoryName = (String)categoryCombo.getSelectedItem();
            if (categoryName == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a category.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Category category = findCategoryByName(categoryName);
            
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a description.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive amount.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String notes = notesArea.getText().trim();
            
            // Create or update transaction
            Category selectedCategory = findCategoryByName((String) categoryCombo.getSelectedItem());
            type = "Income".equals(typeCombo.getSelectedItem()) ? 
                TransactionType.INCOME : TransactionType.EXPENSE;
                
            // Create the appropriate transaction type
            if (type == TransactionType.INCOME) {
                // Create a new IncomeTransaction
                this.transaction = new IncomeTransaction(
                    amount,
                    description,
                    date,
                    selectedCategory
                );
            } else {
                // Create a new ExpenseTransaction
                this.transaction = new ExpenseTransaction(
                    amount,
                    description,
                    date,
                    selectedCategory
                );
            }
            
            // Set notes for the transaction
            this.transaction.setNotes(notes);
            
            // Close dialog
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Find a category by name
     */
    private Category findCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }
    
    /**
     * Get the transaction from the dialog
     * 
     * @return The transaction, or null if cancelled
     */
    public Transaction getTransaction() {
        return transaction;
    }
}
