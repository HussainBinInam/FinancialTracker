package com.finance.ui;

import com.finance.data.DataManager;
import com.finance.model.Category;
import com.finance.model.Transaction;
import com.finance.model.TransactionType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Panel for managing budgets
 */
import com.finance.model.Budget;

public class BudgetPanel extends JPanel {
    private final DataManager dataManager;
    private List<Budget> budgets;
    private List<Category> categories;
    private List<Transaction> transactions;
    
    // UI Components
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    
    /**
     * Constructor
     */
    public BudgetPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        this.budgets = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
        
        setupUI();
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create table model with columns
        String[] columnNames = {"Category", "Monthly Amount", "Current Spending", "Remaining", "Progress"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells read-only
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) {
                    return JProgressBar.class; // For progress bar column
                }
                return Object.class;
            }
        };
        
        budgetTable = new JTable(tableModel);
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        budgetTable.setRowHeight(30);
        budgetTable.getColumnModel().getColumn(4).setCellRenderer(new ProgressBarRenderer());
        
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        
        // Create toolbar
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Month selector
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        // Year selector
        String[] years = new String[5];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear + i - 2);
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(String.valueOf(currentYear));
        
        JButton refreshButton = new JButton("Refresh");
        
        toolbar.add(new JLabel("Month: "));
        toolbar.add(monthCombo);
        toolbar.add(new JLabel("  Year: "));
        toolbar.add(yearCombo);
        toolbar.add(refreshButton);
        toolbar.add(Box.createHorizontalGlue());
        
        addButton = new JButton("Add Budget");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Budget Summary"));
        summaryPanel.setLayout(new GridLayout(1, 3, 10, 0));
        
        JLabel totalBudgetLabel = new JLabel("Total Budget: $0.00");
        JLabel totalSpentLabel = new JLabel("Total Spent: $0.00");
        JLabel overallProgressLabel = new JLabel("Overall: 0%");
        
        summaryPanel.add(totalBudgetLabel);
        summaryPanel.add(totalSpentLabel);
        summaryPanel.add(overallProgressLabel);
        
        // Add components to panel
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        addButton.addActionListener(e -> addBudget());
        editButton.addActionListener(e -> editBudget());
        deleteButton.addActionListener(e -> deleteBudget());
        refreshButton.addActionListener(e -> refreshBudgetData());
        
        // Set initial button state
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Add selection listener
        budgetTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = budgetTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
        });
    }
    
    /**
     * Set budget data
     */
    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        refreshTable();
    }
    
    /**
     * Set categories data
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    /**
     * Set transactions data
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        refreshTable();
    }
    
    /**
     * Refresh table with current budget data
     */
    private void refreshTable() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get selected month and year
        int monthIndex = monthCombo.getSelectedIndex();
        int year = Integer.parseInt((String)yearCombo.getSelectedItem());
        
        // Calculate spending for the month
        YearMonth yearMonth = YearMonth.of(year, monthIndex + 1);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        Map<String, Double> categorySpending = new HashMap<>();
        
        // Calculate spending for each category
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE && 
                !transaction.getDate().isBefore(startDate) && 
                !transaction.getDate().isAfter(endDate)) {
                
                String categoryId = String.valueOf(transaction.getCategory().getId());
                double amount = transaction.getAmount();
                
                if (categorySpending.containsKey(categoryId)) {
                    categorySpending.put(categoryId, categorySpending.get(categoryId) + amount);
                } else {
                    categorySpending.put(categoryId, amount);
                }
            }
        }
        
        // Add rows for each budget
        double totalBudgetAmount = 0;
        double totalSpentAmount = 0;
        
        for (Budget budget : budgets) {
            // Check if the budget is for the selected month and year
            if (budget.getYear() == year && budget.getMonth() == monthIndex + 1) {
                Object[] row = new Object[5];
                
                Category category = budget.getCategory();
                double budgetAmount = budget.getAmount();
                double spent = categorySpending.getOrDefault(category.getId(), 0.0);
                double remaining = budgetAmount - spent;
                int progressValue = (int)Math.min(100, (spent / budgetAmount) * 100);
                
                row[0] = category.getName();
                row[1] = String.format("$%.2f", budgetAmount);
                row[2] = String.format("$%.2f", spent);
                row[3] = String.format("$%.2f", remaining);
                
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue(progressValue);
                progressBar.setStringPainted(true);
                row[4] = progressBar;
                
                tableModel.addRow(row);
                
                totalBudgetAmount += budgetAmount;
                totalSpentAmount += spent;
            }
        }
        
        // Update summary panel
        JPanel summaryPanel = (JPanel)getComponent(2);
        JLabel totalBudgetLabel = (JLabel)summaryPanel.getComponent(0);
        JLabel totalSpentLabel = (JLabel)summaryPanel.getComponent(1);
        JLabel overallProgressLabel = (JLabel)summaryPanel.getComponent(2);
        
        totalBudgetLabel.setText(String.format("Total Budget: $%.2f", totalBudgetAmount));
        totalSpentLabel.setText(String.format("Total Spent: $%.2f", totalSpentAmount));
        
        int overallProgress = totalBudgetAmount > 0 ? 
            (int)Math.min(100, (totalSpentAmount / totalBudgetAmount) * 100) : 0;
        overallProgressLabel.setText(String.format("Overall: %d%%", overallProgress));
    }
    
    /**
     * Add a new budget
     */
    private void addBudget() {
        List<Category> expenseCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getType() == TransactionType.EXPENSE) {
                expenseCategories.add(category);
            }
        }
        
        if (expenseCategories.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No expense categories available. Please create some first.",
                "No Categories",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int monthIndex = monthCombo.getSelectedIndex();
        int year = Integer.parseInt((String)yearCombo.getSelectedItem());
        
        BudgetDialog dialog = new BudgetDialog(
            SwingUtilities.getWindowAncestor(this),
            "Add Budget",
            null,
            expenseCategories,
            monthIndex + 1,
            year
        );
        
        dialog.setVisible(true);
        
        if (dialog.getBudget() != null) {
            Budget newBudget = dialog.getBudget();
            budgets.add(newBudget);
            dataManager.saveBudget(newBudget);
            refreshTable();
        }
    }
    
    /**
     * Edit the selected budget
     */
    private void editBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Find the selected budget
        String categoryName = (String)tableModel.getValueAt(selectedRow, 0);
        int monthIndex = monthCombo.getSelectedIndex();
        int year = Integer.parseInt((String)yearCombo.getSelectedItem());
        
        Budget selectedBudget = null;
        for (Budget budget : budgets) {
            if (budget.getCategory().getName().equals(categoryName) &&
                budget.getMonth() == monthIndex + 1 &&
                budget.getYear() == year) {
                selectedBudget = budget;
                break;
            }
        }
        
        if (selectedBudget == null) return;
        
        List<Category> expenseCategories = new ArrayList<>();
        for (Category category : categories) {
            if (category.getType() == TransactionType.EXPENSE) {
                expenseCategories.add(category);
            }
        }
        
        BudgetDialog dialog = new BudgetDialog(
            SwingUtilities.getWindowAncestor(this),
            "Edit Budget",
            selectedBudget,
            expenseCategories,
            monthIndex + 1,
            year
        );
        
        dialog.setVisible(true);
        
        if (dialog.getBudget() != null) {
            Budget updatedBudget = dialog.getBudget();
            
            // Update the budget in the list
            for (int i = 0; i < budgets.size(); i++) {
                if (budgets.get(i).getId().equals(updatedBudget.getId())) {
                    budgets.set(i, updatedBudget);
                    break;
                }
            }
            
            dataManager.updateBudget(updatedBudget);
            refreshTable();
        }
    }
    
    /**
     * Delete the selected budget
     */
    private void deleteBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Find the selected budget
        String categoryName = (String)tableModel.getValueAt(selectedRow, 0);
        int monthIndex = monthCombo.getSelectedIndex();
        int year = Integer.parseInt((String)yearCombo.getSelectedItem());
        
        Budget selectedBudget = null;
        for (Budget budget : budgets) {
            if (budget.getCategory().getName().equals(categoryName) &&
                budget.getMonth() == monthIndex + 1 &&
                budget.getYear() == year) {
                selectedBudget = budget;
                break;
            }
        }
        
        if (selectedBudget == null) return;
        
        // Confirm deletion
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this budget?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            budgets.remove(selectedBudget);
            dataManager.deleteBudget(selectedBudget);
            refreshTable();
        }
    }
    
    /**
     * Refresh budget data for selected month and year
     */
    private void refreshBudgetData() {
        refreshTable();
    }

    public List<Budget> getBudgets() {
        return this.budgets;
    }

    /**
     * Custom renderer for progress bar column
     */
    private static class ProgressBarRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus,
                                                      int row, int column) {
            if (value instanceof JProgressBar) {
                return (JProgressBar) value;
            }
            return (Component) value;
        }
    }
}
