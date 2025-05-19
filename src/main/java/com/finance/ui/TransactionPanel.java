package com.finance.ui;

import com.finance.data.DataManager;
import com.finance.model.*;
import com.finance.service.FinancialCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Panel for managing transactions
 * Demonstrates MVC pattern and data binding
 */
public class TransactionPanel extends JPanel {
    private final DataManager dataManager;
    private List<Transaction> transactions;
    private List<Category> categories;
    
    // UI Components
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton filterButton;
    private JComboBox<String> typeFilterCombo;
    
    // Change notification
    private DataChangeListener dataChangeListener;
    
    /**
     * Constructor
     */
    public TransactionPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        this.transactions = new ArrayList<>();
        this.categories = new ArrayList<>();
        
        setupUI();
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create table model with columns
        String[] columnNames = {"Date", "Type", "Category", "Description", "Amount", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells read-only
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        // Create toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        searchField = new JTextField(20);
        searchField.setToolTipText("Search transactions");
        
        typeFilterCombo = new JComboBox<>(new String[] {"All Types", "Income", "Expense"});
        
        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        filterButton = new JButton("Apply Filter");
        
        toolbar.add(new JLabel("Search: "));
        toolbar.add(searchField);
        toolbar.add(Box.createHorizontalStrut(10));
        toolbar.add(new JLabel("Type: "));
        toolbar.add(typeFilterCombo);
        toolbar.add(filterButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setLayout(new GridLayout(1, 3, 10, 0));
        
        JLabel totalIncomeLabel = new JLabel("Total Income: $0.00");
        JLabel totalExpenseLabel = new JLabel("Total Expenses: $0.00");
        JLabel balanceLabel = new JLabel("Balance: $0.00");
        
        summaryPanel.add(totalIncomeLabel);
        summaryPanel.add(totalExpenseLabel);
        summaryPanel.add(balanceLabel);
        
        // Add components to panel
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        filterButton.addActionListener(e -> applyFilter());
        
        // Initial state - disable buttons until transactions are loaded
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Add selection listener to enable/disable buttons
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = transactionTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
    }
    
    /**
     * Set transactions data and refresh the UI
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        refreshTable();
        updateSummary();
    }
    
    /**
     * Set categories data
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    /**
     * Set data change listener to notify when transactions change
     */
    public void setDataChangeListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }
    
    /**
     * Refresh table with current transaction data
     */
    private void refreshTable() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Add rows for each transaction
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        for (Transaction transaction : transactions) {
            Object[] row = new Object[6];
            row[0] = transaction.getDate().format(dateFormatter);
            row[1] = transaction.getType().getDisplayName();
            row[2] = transaction.getCategory().getName();
            row[3] = transaction.getDescription();
            row[4] = String.format("$%.2f", transaction.getAmount());
            row[5] = transaction.getNotes();
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Update summary information
     */
    private void updateSummary() {
        // Get components
        JPanel summaryPanel = (JPanel)getComponent(2);
        JLabel totalIncomeLabel = (JLabel)summaryPanel.getComponent(0);
        JLabel totalExpenseLabel = (JLabel)summaryPanel.getComponent(1);
        JLabel balanceLabel = (JLabel)summaryPanel.getComponent(2);
        
        // Calculate totals
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        LocalDate maxDate = LocalDate.of(2100, 12, 31);
        
        double totalIncome = FinancialCalculator.calculateTotalIncome(
            transactions, minDate, maxDate);
        double totalExpenses = FinancialCalculator.calculateTotalExpenses(
            transactions, minDate, maxDate);
        double balance = totalIncome - totalExpenses;
        
        // Update labels
        totalIncomeLabel.setText(String.format("Total Income: $%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("Total Expenses: $%.2f", totalExpenses));
        balanceLabel.setText(String.format("Balance: $%.2f", balance));
    }
    
    /**
     * Add a new transaction
     */
    private void addTransaction() {
        TransactionDialog dialog = new TransactionDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Add Transaction", 
            null,  // No existing transaction to edit
            categories
        );
        
        dialog.setVisible(true);
        
        if (dialog.getTransaction() != null) {
            // Add new transaction
            Transaction newTransaction = dialog.getTransaction();
            transactions.add(newTransaction);
            
            // Save to data manager
            dataManager.saveTransaction(newTransaction);
            
            // Refresh UI
            refreshTable();
            updateSummary();
            
            // Notify listeners of data change
            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged();
            }
            
            // Notify listeners of data change
            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged();
            }
        }
    }
    
    /**
     * Edit the selected transaction
     */
    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Transaction selectedTransaction = transactions.get(selectedRow);
        
        TransactionDialog dialog = new TransactionDialog(
            SwingUtilities.getWindowAncestor(this), 
            "Edit Transaction", 
            selectedTransaction,
            categories
        );
        
        dialog.setVisible(true);
        
        if (dialog.getTransaction() != null) {
            // Update transaction
            Transaction updatedTransaction = dialog.getTransaction();
            transactions.set(selectedRow, updatedTransaction);
            
            // Save to data manager
            dataManager.updateTransaction(updatedTransaction);
            
            // Refresh UI
            refreshTable();
            updateSummary();
            
            // Notify listeners of data change
            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged();
            }
        }
    }
    
    /**
     * Delete the selected transaction
     */
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Transaction selectedTransaction = transactions.get(selectedRow);
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this transaction?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // Remove from list
            transactions.remove(selectedRow);
            
            // Remove from data manager
            dataManager.deleteTransaction(selectedTransaction.getId());
            
            // Refresh UI
            refreshTable();
            updateSummary();
        }
    }
    
    /**
     * Apply search and filter criteria
     */
    private void applyFilter() {
        String searchText = searchField.getText().trim().toLowerCase();
        String typeFilter = (String)typeFilterCombo.getSelectedItem();
        
        List<Transaction> filteredTransactions = new ArrayList<>(transactions);
        
        // Apply search text filter if provided
        if (!searchText.isEmpty()) {
            filteredTransactions = filteredTransactions.stream()
                .filter(t -> 
                    t.getDescription().toLowerCase().contains(searchText) ||
                    t.getCategory().getName().toLowerCase().contains(searchText) ||
                    t.getNotes().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        }
        
        // Apply transaction type filter if not "All Types"
        if (!"All Types".equals(typeFilter)) {
            TransactionType type = "Income".equals(typeFilter) ? 
                TransactionType.INCOME : TransactionType.EXPENSE;
                
            filteredTransactions = filteredTransactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.toList());
        }
        
        // Update table with filtered transactions
        tableModel.setRowCount(0);
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        
        for (Transaction transaction : filteredTransactions) {
            Object[] row = new Object[6];
            row[0] = transaction.getDate().format(dateFormatter);
            row[1] = transaction.getType().getDisplayName();
            row[2] = transaction.getCategory().getName();
            row[3] = transaction.getDescription();
            row[4] = String.format("$%.2f", transaction.getAmount());
            row[5] = transaction.getNotes();
            
            tableModel.addRow(row);
        }
    }
}
