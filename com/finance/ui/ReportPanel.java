package com.finance.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.SimpleDateFormat;

import com.finance.data.DataManager;
import com.finance.model.Category;
import com.finance.model.Transaction;

/**
 * Panel for displaying financial reports and statistics
 */
public class ReportPanel extends JPanel {
    private DataManager dataManager;
    private List<Transaction> transactions;
    private List<Category> categories;
    
    private JPanel summaryPanel;
    private JPanel categoryPanel;
    private JPanel timePanel;
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> timePeriodComboBox;
    
    /**
     * Constructs a new ReportPanel
     * 
     * @param dataManager the data manager to use
     */
    public ReportPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        this.transactions = new ArrayList<>();
        this.categories = new ArrayList<>();
        
        initializeUI();
        refreshData();
    }
    
    /**
     * Initialize UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Control panel at the top
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel reportTypeLabel = new JLabel("Report Type:");
        String[] reportTypes = {"Category Summary", "Monthly Summary", "Income vs Expenses"};
        reportTypeComboBox = new JComboBox<>(reportTypes);
        
        JLabel periodLabel = new JLabel("Time Period:");
        String[] periods = {"This Month", "Last Month", "Last 3 Months", "This Year", "All Time"};
        timePeriodComboBox = new JComboBox<>(periods);
        
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        
        controlPanel.add(reportTypeLabel);
        controlPanel.add(reportTypeComboBox);
        controlPanel.add(periodLabel);
        controlPanel.add(timePeriodComboBox);
        controlPanel.add(generateButton);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Main content panels
        JTabbedPane tabbedPane = new JTabbedPane();
        
        summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));
        
        timePanel = new JPanel(new BorderLayout());
        timePanel.setBorder(BorderFactory.createTitledBorder("Time Analysis"));
        
        tabbedPane.addTab("Summary", summaryPanel);
        tabbedPane.addTab("Categories", categoryPanel);
        tabbedPane.addTab("Timeline", timePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Generate report based on selected options
     */
    private void generateReport() {
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No transaction data available to generate reports.", 
                "No Data", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String timePeriod = (String) timePeriodComboBox.getSelectedItem();
        
        // Clear previous report data
        summaryPanel.removeAll();
        categoryPanel.removeAll();
        timePanel.removeAll();
        
        // Generate appropriate report based on selection
        if ("Category Summary".equals(reportType)) {
            generateCategorySummary();
        } else if ("Monthly Summary".equals(reportType)) {
            generateMonthlySummary();
        } else if ("Income vs Expenses".equals(reportType)) {
            generateIncomeVsExpenses();
        }
        
        // Refresh UI
        summaryPanel.revalidate();
        summaryPanel.repaint();
        categoryPanel.revalidate();
        categoryPanel.repaint();
        timePanel.revalidate();
        timePanel.repaint();
    }
    
    /**
     * Generate category summary report
     */
    private void generateCategorySummary() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Category statistics would go here
        JLabel titleLabel = new JLabel("Category Summary Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Create a table for category summary
        String[] columnNames = {"Category", "Total Amount", "Transaction Count", "Average"};
        Object[][] data = calculateCategoryStats();
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
        
        summaryPanel.add(panel, BorderLayout.CENTER);
    }
    
    /**
     * Generate monthly summary report
     */
    private void generateMonthlySummary() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Monthly Summary Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Monthly data would be added here
        String[] columnNames = {"Month", "Income", "Expenses", "Net"};
        Object[][] data = calculateMonthlyStats();
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
        
        summaryPanel.add(panel, BorderLayout.CENTER);
    }
    
    /**
     * Generate income vs expenses report
     */
    private void generateIncomeVsExpenses() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Income vs Expenses Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Placeholder for actual chart/graph that would go here
        JLabel placeholderLabel = new JLabel("Income vs Expenses Chart would display here");
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(placeholderLabel);
        
        // Summary statistics
        double totalIncome = calculateTotalIncome();
        double totalExpenses = calculateTotalExpenses();
        double netAmount = totalIncome - totalExpenses;
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 2));
        statsPanel.add(new JLabel("Total Income:"));
        statsPanel.add(new JLabel(String.format("$%.2f", totalIncome)));
        statsPanel.add(new JLabel("Total Expenses:"));
        statsPanel.add(new JLabel(String.format("$%.2f", totalExpenses)));
        statsPanel.add(new JLabel("Net Amount:"));
        statsPanel.add(new JLabel(String.format("$%.2f", netAmount)));
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(statsPanel);
        
        summaryPanel.add(panel, BorderLayout.CENTER);
    }
    
    /**
     * Calculate statistics for each category
     * 
     * @return data for the category statistics table
     */
    private Object[][] calculateCategoryStats() {
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String categoryName = getCategoryName(transaction.getCategoryId());
            double amount = transaction.getAmount();
            
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + amount);
            categoryCounts.put(categoryName, categoryCounts.getOrDefault(categoryName, 0) + 1);
        }
        
        Object[][] data = new Object[categoryTotals.size()][4];
        int i = 0;
        for (String category : categoryTotals.keySet()) {
            double total = categoryTotals.get(category);
            int count = categoryCounts.get(category);
            double average = total / count;
            
            data[i][0] = category;
            data[i][1] = String.format("$%.2f", total);
            data[i][2] = count;
            data[i][3] = String.format("$%.2f", average);
            i++;
        }
        
        return data;
    }
    
    /**
     * Calculate monthly statistics
     * 
     * @return data for the monthly statistics table
     */
    private Object[][] calculateMonthlyStats() {
        // This would normally calculate real statistics from the transactions
        // For now, return placeholder data
        return new Object[][] {
            {"January", "$1,200.00", "$950.00", "$250.00"},
            {"February", "$1,150.00", "$875.00", "$275.00"},
            {"March", "$1,350.00", "$1,025.00", "$325.00"}
        };
    }
    
    /**
     * Calculate total income
     * 
     * @return total income amount
     */
    private double calculateTotalIncome() {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                total += transaction.getAmount();
            }
        }
        return total;
    }
    
    /**
     * Calculate total expenses
     * 
     * @return total expenses amount (as a positive number)
     */
    private double calculateTotalExpenses() {
        double total = 0.0;
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                total += Math.abs(transaction.getAmount());
            }
        }
        return total;
    }
    
    /**
     * Get category name by ID
     * 
     * @param categoryId the ID of the category
     * @return the name of the category
     */
    private String getCategoryName(int categoryId) {
        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category.getName();
            }
        }
        return "Unknown";
    }
    
    /**
     * Set transactions data
     * 
     * @param transactions the list of transactions to set
     */
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }
    
    /**
     * Set categories data
     * 
     * @param categories the list of categories to set
     */
    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }
    
    /**
     * Refresh data from data manager
     */
    public void refreshData() {
        if (dataManager != null) {
            setTransactions(dataManager.getTransactions());
            setCategories(dataManager.getCategories());
        }
    }
}
