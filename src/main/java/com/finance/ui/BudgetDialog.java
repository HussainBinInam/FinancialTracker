package com.finance.ui;

import com.finance.model.Category;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import com.finance.model.Budget;

/**
 * Dialog for creating or editing a budget
 */
public class BudgetDialog extends JDialog {
    // UI Components
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Data
    private Budget budget;
    private List<Category> categories;
    private int month;
    private int year;
    private boolean isNewBudget;
    
    /**
     * Constructor
     * 
     * @param parent The parent window
     * @param title Dialog title
     * @param budget Budget to edit, or null for a new budget
     * @param categories List of available categories
     * @param month Selected month
     * @param year Selected year
     */
    public BudgetDialog(Window parent, String title, Budget budget, 
                        List<Category> categories, int month, int year) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        
        this.budget = budget;
        this.categories = categories;
        this.month = month;
        this.year = year;
        this.isNewBudget = (budget == null);
        
        setupUI();
        loadData();
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        setSize(350, 220);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Month/Year label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        Month monthEnum = Month.of(month);
        String monthYearText = monthEnum.toString() + " " + year;
        JLabel monthYearLabel = new JLabel(monthYearText);
        monthYearLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        formPanel.add(monthYearLabel, gbc);
        
        // Category dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        categoryCombo = new JComboBox<>();
        for (Category category : categories) {
            categoryCombo.addItem(category.getName());
        }
        formPanel.add(categoryCombo, gbc);
        
        // Amount field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Budget Amount:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        amountField = new JTextField(10);
        formPanel.add(amountField, gbc);
        
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
        saveButton.addActionListener(e -> saveBudget());
        cancelButton.addActionListener(e -> dispose());
    }
    
    /**
     * Load data into UI components
     */
    private void loadData() {
        if (!isNewBudget) {
            categoryCombo.setSelectedItem(budget.getCategory().getName());
            amountField.setText(String.format("%.2f", budget.getAmount()));
        }
    }
    
    /**
     * Save the budget
     */
    private void saveBudget() {
        try {
            // Validate input
            String categoryName = (String)categoryCombo.getSelectedItem();
            if (categoryName == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a category.",
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
            
            // Find the category
            Category selectedCategory = null;
            for (Category category : categories) {
                if (category.getName().equals(categoryName)) {
                    selectedCategory = category;
                    break;
                }
            }
            
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this,
                    "Selected category not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if a budget already exists for this category/month/year
            if (isNewBudget) {
                for (Component component : getOwner().getComponents()) {
                    if (component instanceof BudgetPanel) {
                        BudgetPanel budgetPanel = (BudgetPanel)component;
                        for (Budget existingBudget : budgetPanel.getBudgets()) {
                            if (existingBudget.getCategory().getId()== selectedCategory.getId() &&
                                existingBudget.getMonth() == month &&
                                existingBudget.getYear() == year) {
                                
                                JOptionPane.showMessageDialog(this,
                                    "A budget for this category already exists for " + 
                                    Month.of(month).toString() + " " + year,
                                    "Duplicate Budget",
                                    JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                }
            }
            
            // Create or update budget
            if (isNewBudget) {
                budget = new Budget(
                    UUID.randomUUID().toString(),
                    month,
                    year,
                    selectedCategory,
                    amount
                );
            } else {
                budget = new Budget(
                    budget.getId(),
                    month,
                    year,
                    selectedCategory,
                    amount
                );
            }
            
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
     * Get the budget
     * 
     * @return The budget, or null if cancelled
     */
    public Budget getBudget() {
        return budget;
    }
}
