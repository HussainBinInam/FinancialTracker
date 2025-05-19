package com.finance.ui;

import com.finance.data.DataManager;
import com.finance.data.FileDataManager;
import com.finance.model.*;
import com.finance.service.FinancialCalculator;
import com.finance.service.ReportGenerator;

import  java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;
import java.util.Locale;
import java.text.NumberFormat;



import com.finance.data.DataManager;
import com.finance.data.FileDataManager;
import com.finance.model.*;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

/**
 * Main application window
 * Demonstrates Swing GUI development and application composition
 */
public class MainFrame extends JFrame implements DataChangeListener {
    // Data manager (composition)
    private final DataManager dataManager;
    
    // UI Panels
    private JPanel dashboardPanel;
    private TransactionPanel transactionPanel;
    private BudgetPanel budgetPanel;
    private ReportPanel reportPanel;
    private JTabbedPane tabbedPane;
    
    // Menu items
    private JMenuItem exportMenuItem;
    private JMenuItem importMenuItem;
    private JMenuItem preferencesMenuItem;
    private JMenuItem exitMenuItem;
    
    // User preferences
    private UserPreferences preferences;
    
    /**
     * Constructor
     */
    public MainFrame() {
        // Initialize data manager
        dataManager = FileDataManager.getInstance();
        preferences = dataManager.loadUserPreferences();
        
        setupUI();
        loadData();
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        // Frame settings
        setTitle("Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Create UI components
        tabbedPane = new JTabbedPane();
        
        // Create and add panels
        dashboardPanel = createDashboardPanel();
        transactionPanel = new TransactionPanel(dataManager);
        transactionPanel.setDataChangeListener(this); // Register as listener
        budgetPanel = new BudgetPanel(dataManager);
        reportPanel = new ReportPanel(dataManager);
        
        tabbedPane.addTab("Dashboard", new ImageIcon(), dashboardPanel, "Overview of your finances");
        tabbedPane.addTab("Transactions", new ImageIcon(), transactionPanel, "Manage your transactions");
        tabbedPane.addTab("Budgets", new ImageIcon(), budgetPanel, "Manage your budgets");
        tabbedPane.addTab("Reports", new ImageIcon(), reportPanel, "View financial reports");
        
        // Add components to main frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Set look and feel based on preferences
        updateLookAndFeel();
        this.createMenuBar();
    }
    
    /**
     * Create the application menu bar
     */

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        importMenuItem = new JMenuItem("Import Data...");
        exportMenuItem = new JMenuItem("Export Data...");
        preferencesMenuItem = new JMenuItem("Preferences...");
        exitMenuItem = new JMenuItem("Exit");
        
        fileMenu.add(importMenuItem);
        fileMenu.add(exportMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(preferencesMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        // Add menu bar to frame
        setJMenuBar(menuBar);
        
        // Add action listeners
        exitMenuItem.addActionListener(e -> System.exit(0));
        
        exportMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Data");
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = dataManager.exportData(filePath);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Data exported successfully", 
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to export data",
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        importMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Data");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                boolean success = dataManager.importData(filePath);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Data imported successfully", 
                        "Import Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadData(); // Reload data after import
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to import data",
                        "Import Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        preferencesMenuItem.addActionListener(e -> {
            PreferencesDialog dialog = new PreferencesDialog(this, preferences);
            dialog.setVisible(true);
            
            if (dialog.isPreferencesSaved()) {
                preferences = dialog.getUpdatedPreferences();
                dataManager.saveUserPreferences(preferences);
                updateLookAndFeel();
            }
        });
        
        aboutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Personal Finance Tracker\n" +
                "Version 1.0\n\n" +
                "A desktop application for managing personal finances.\n" +
                "Track income and expenses, create budgets, and generate financial reports.",
                "About Personal Finance Tracker",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Create the dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        
        // Current month summary card
        JPanel monthSummaryCard = createCard("This Month");
        JPanel balanceCard = createCard("Current Balance");
        JPanel savingsCard = createCard("Savings Rate");
        
        summaryPanel.add(monthSummaryCard);
        summaryPanel.add(balanceCard);
        summaryPanel.add(savingsCard);
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        // Center panel with charts
        JPanel chartsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        chartsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Placeholder panels for charts
        JPanel incomeChart = createChartPanel("Income by Category");
        JPanel expenseChart = createChartPanel("Expenses by Category");
        JPanel budgetChart = createChartPanel("Budget Status");
        JPanel trendChart = createChartPanel("Income vs. Expenses Trend");
        
        chartsPanel.add(incomeChart);
        chartsPanel.add(expenseChart);
        chartsPanel.add(budgetChart);
        chartsPanel.add(trendChart);
        
        panel.add(chartsPanel, BorderLayout.CENTER);
        
        // Recent transactions panel at bottom
        JPanel recentTransactionsPanel = new JPanel(new BorderLayout());
        recentTransactionsPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        
        // Will be populated in updateDashboard method
        JScrollPane scrollPane = new JScrollPane();
        recentTransactionsPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(recentTransactionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create a card UI component for summary figures
     */
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        
        JLabel valueLabel = new JLabel("$0.00");
        valueLabel.setFont(new Font(valueLabel.getFont().getName(), Font.BOLD, 24));
        
        JLabel changeLabel = new JLabel("0% from last period");
        changeLabel.setFont(new Font(changeLabel.getFont().getName(), Font.PLAIN, 12));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(changeLabel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Create a panel for chart display
     */
    private JPanel createChartPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        // Placeholder for chart visualization
        JPanel chartPlaceholder = new JPanel();
        chartPlaceholder.setBackground(Color.WHITE);
        chartPlaceholder.setPreferredSize(new Dimension(200, 150));
        
        panel.add(chartPlaceholder, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Update look and feel based on preferences
     */
    private void updateLookAndFeel() {
        try {
            if (preferences.getDarkMode()) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load data from data manager and update UI
     */
    private void loadData() {
        // Load data from data manager
        List<Transaction> transactions = dataManager.loadTransactions();
        List<Budget> budgets = dataManager.loadBudgets();
        List<Category> categories = dataManager.loadCategories();
        
        // Update UI panels with loaded data
        transactionPanel.setTransactions(transactions);
        transactionPanel.setCategories(categories);
        
        budgetPanel.setBudgets(budgets);
        budgetPanel.setCategories(categories);
        
        if (reportPanel != null) {
            reportPanel.setTransactions(transactions);
            reportPanel.setCategories(categories);
        }
        
        // Update dashboard
        updateDashboard(transactions, budgets);
    }
    
    /**
     * Update dashboard with current data
     */
    private void updateDashboard(List<Transaction> transactions, List<Budget> budgets) {
        // Get panel components
        Component[] summaryComponents = ((JPanel)dashboardPanel.getComponent(0)).getComponents();
        JPanel monthSummaryCard = (JPanel)summaryComponents[0];
        JPanel balanceCard = (JPanel)summaryComponents[1];
        JPanel savingsCard = (JPanel)summaryComponents[2];
        
        // Format for currency display
        Locale localeToUse = (preferences.getLocale() != null) ? preferences.getLocale() : Locale.getDefault();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeToUse);
        
        // Validate currency code with safe fallback to USD
        Currency currencyToUse;
        try {
            String currencyCode = preferences.getCurrency();
            if (currencyCode != null && currencyCode.length() == 3) {
                currencyToUse = Currency.getInstance(currencyCode);
            } else {
                currencyToUse = Currency.getInstance("USD");
            }
        } catch (IllegalArgumentException e) {
            // Default to USD if currency code is invalid
            currencyToUse = Currency.getInstance("USD");
        }
        
        currencyFormat.setCurrency(currencyToUse);
        
        // Calculate current month figures
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        
        double monthlyIncome = FinancialCalculator.calculateTotalIncome(
            transactions, startOfMonth, endOfMonth);
        double monthlyExpenses = FinancialCalculator.calculateTotalExpenses(
            transactions, startOfMonth, endOfMonth);
        double monthlySavings = monthlyIncome - monthlyExpenses;
        
        // Calculate total balance
        double totalBalance = 0;
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalBalance += t.getAmount();
            } else {
                totalBalance -= t.getAmount();
            }
        }
        
        // Calculate savings rate
        double savingsRate = 0;
        if (monthlyIncome > 0) {
            savingsRate = (monthlySavings / monthlyIncome) * 100;
        }
        
        // Update month summary card
        JLabel monthValueLabel = (JLabel)monthSummaryCard.getComponent(1);
        monthValueLabel.setText(currencyFormat.format(monthlySavings));
        
        // Update balance card
        JLabel balanceValueLabel = (JLabel)balanceCard.getComponent(1);
        balanceValueLabel.setText(currencyFormat.format(totalBalance));
        
        // Update savings rate card
        JLabel savingsValueLabel = (JLabel)savingsCard.getComponent(1);
        savingsValueLabel.setText(String.format("%.1f%%", savingsRate));
        
        // Update recent transactions
        updateRecentTransactions(transactions);
        
        // Update charts with real data
        updateIncomeByCategory(transactions);
        updateExpensesByCategory(transactions);
        updateBudgetStatus(budgets, transactions);
        updateIncomeExpenseTrend(transactions);
    }
    
    /**
     * Update Income vs Expenses Trend chart
     */
    private void updateIncomeExpenseTrend(List<Transaction> transactions) {
        // Get the trend chart panel
        JPanel chartsPanel = (JPanel)dashboardPanel.getComponent(1);
        JPanel trendChartPanel = (JPanel)chartsPanel.getComponent(3);
        
        // Clear previous content
        JPanel chartContent = (JPanel)trendChartPanel.getComponent(0);
        chartContent.removeAll();
        
        // Get data for the last 6 months
        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        Map<YearMonth, Double> monthlyIncome = new TreeMap<>();
        Map<YearMonth, Double> monthlyExpenses = new TreeMap<>();
        
        // Initialize last 6 months
        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            monthlyIncome.put(month, 0.0);
            monthlyExpenses.put(month, 0.0);
        }
        
        // Aggregate transactions by month
        for (Transaction t : transactions) {
            YearMonth month = YearMonth.from(t.getDate());
            
            // Only consider last 6 months
            if (month.isBefore(currentMonth.minusMonths(5)) || month.isAfter(currentMonth)) {
                continue;
            }
            
            if (t.getType() == TransactionType.INCOME) {
                double current = monthlyIncome.getOrDefault(month, 0.0);
                monthlyIncome.put(month, current + t.getAmount());
            } else {
                double current = monthlyExpenses.getOrDefault(month, 0.0);
                monthlyExpenses.put(month, current + t.getAmount());
            }
        }
        
        // Create simple bar chart visualization
        chartContent.setLayout(new BorderLayout());
        JPanel dataPanel = new JPanel(new GridLayout(monthlyIncome.size(), 1, 0, 10));
        
        // Format for month names
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        
        for (YearMonth month : monthlyIncome.keySet()) {
            double incomeValue = monthlyIncome.get(month);
            double expenseValue = monthlyExpenses.get(month);
            
            JPanel monthPanel = new JPanel(new BorderLayout());
            monthPanel.add(new JLabel(month.format(monthFormatter)), BorderLayout.WEST);
            
            JPanel barsPanel = new JPanel(new GridLayout(2, 1, 0, 2));
            
            // Income bar
            JPanel incomeBar = createBarPanel("Income", incomeValue, Color.GREEN, 
                Math.max(incomeValue, expenseValue));
            
            // Expense bar
            JPanel expenseBar = createBarPanel("Expenses", expenseValue, Color.RED,
                Math.max(incomeValue, expenseValue));
            
            barsPanel.add(incomeBar);
            barsPanel.add(expenseBar);
            
            monthPanel.add(barsPanel, BorderLayout.CENTER);
            dataPanel.add(monthPanel);
        }
        
        chartContent.add(new JScrollPane(dataPanel), BorderLayout.CENTER);
        
        chartContent.revalidate();
        chartContent.repaint();
    }
    
    /**
     * Helper method to create a bar visualization
     */
    private JPanel createBarPanel(String label, double value, Color color, double maxValue) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        
        JPanel barPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = (int)((value / maxValue) * getWidth());
                g.setColor(color);
                g.fillRect(0, 0, width, getHeight());
            }
        };
        barPanel.setPreferredSize(new Dimension(200, 20));
        panel.add(barPanel, BorderLayout.CENTER);
        
        panel.add(new JLabel(String.format("$%.2f", value)), BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Implementation of DataChangeListener interface
     * Called when data changes in other panels
     */
    @Override
    public void onDataChanged() {
        // Reload data from the data manager to get fresh data
        List<Transaction> transactions = dataManager.loadTransactions();
        List<Budget> budgets = dataManager.loadBudgets();
        
        // Update dashboard with fresh data
        updateDashboard(transactions, budgets);
    }
    
    /**
     * Update Budget Status chart
     */
    private void updateBudgetStatus(List<Budget> budgets, List<Transaction> transactions) {
        // Get the budget chart panel
        JPanel chartsPanel = (JPanel)dashboardPanel.getComponent(1);
        JPanel budgetChartPanel = (JPanel)chartsPanel.getComponent(2);
        
        // Clear previous content
        JPanel chartContent = (JPanel)budgetChartPanel.getComponent(0);
        chartContent.removeAll();
        
        // Create simple visualization of budget status
        if (budgets.isEmpty()) {
            chartContent.add(new JLabel("No budgets defined"));
        } else {
            chartContent.setLayout(new BorderLayout());
            JPanel dataPanel = new JPanel(new GridLayout(0, 1));
            
            // Current month for budget calculation
            YearMonth currentMonth = YearMonth.from(LocalDate.now());
            
            for (Budget budget : budgets) {
                // Calculate actual spending for this budget's category
                double actualSpending = 0.0;
                Category budgetCategory = budget.getCategory();
                
                for (Transaction t : transactions) {
                    if (t.getType() == TransactionType.EXPENSE && 
                        t.getCategory().equals(budgetCategory) &&
                        YearMonth.from(t.getDate()).equals(currentMonth)) {
                        actualSpending += t.getAmount();
                    }
                }
                
                // Calculate percentage of budget used
                double budgetLimit = budget.getAmount();
                double percentUsed = Math.min(100.0, (actualSpending / budgetLimit) * 100.0);
                
                // Create row with progress bar
                JPanel row = new JPanel(new BorderLayout(5, 0));
                row.add(new JLabel(budget.getCategory().getName()), BorderLayout.WEST);
                
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue((int)percentUsed);
                progressBar.setStringPainted(true);
                progressBar.setString(String.format("$%.2f / $%.2f (%.0f%%)", 
                    actualSpending, budgetLimit, percentUsed));
                
                // Set color based on usage
                if (percentUsed >= 90) {
                    progressBar.setForeground(Color.RED);
                } else if (percentUsed >= 75) {
                    progressBar.setForeground(Color.ORANGE);
                } else {
                    progressBar.setForeground(Color.GREEN);
                }
                
                row.add(progressBar, BorderLayout.CENTER);
                dataPanel.add(row);
            }
            
            chartContent.add(new JScrollPane(dataPanel), BorderLayout.CENTER);
        }
        
        chartContent.revalidate();
        chartContent.repaint();
    }
    
    /**
     * Update Expenses by Category chart
     */
    private void updateExpensesByCategory(List<Transaction> transactions) {
        // Get the expense chart panel
        JPanel chartsPanel = (JPanel)dashboardPanel.getComponent(1);
        JPanel expenseChartPanel = (JPanel)chartsPanel.getComponent(1);
        
        // Clear previous content
        JPanel chartContent = (JPanel)expenseChartPanel.getComponent(0);
        chartContent.removeAll();
        
        // Calculate expenses by category for current month
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        
        Map<Category, Double> expensesByCategory = FinancialCalculator.calculateExpensesByCategory(
            transactions, startOfMonth, endOfMonth);
        
        // Create simple visualization (in a real app, use a proper chart library)
        if (expensesByCategory.isEmpty()) {
            chartContent.add(new JLabel("No expense data for current month"));
        } else {
            chartContent.setLayout(new BorderLayout());
            JPanel dataPanel = new JPanel(new GridLayout(0, 1));
            
            for (Map.Entry<Category, Double> entry : expensesByCategory.entrySet()) {
                JPanel row = new JPanel(new BorderLayout());
                row.add(new JLabel(entry.getKey().getName()), BorderLayout.WEST);
                row.add(new JLabel(String.format("$%.2f", entry.getValue())), BorderLayout.EAST);
                dataPanel.add(row);
            }
            
            chartContent.add(new JScrollPane(dataPanel), BorderLayout.CENTER);
        }
        
        chartContent.revalidate();
        chartContent.repaint();
    }
    
    /**
     * Update Income by Category chart
     */
    private void updateIncomeByCategory(List<Transaction> transactions) {
        // Get the income chart panel
        JPanel chartsPanel = (JPanel)dashboardPanel.getComponent(1);
        JPanel incomeChartPanel = (JPanel)chartsPanel.getComponent(0);
        
        // Clear previous content
        JPanel chartContent = (JPanel)incomeChartPanel.getComponent(0);
        chartContent.removeAll();
        
        // Calculate income by category for current month
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();
        
        Map<Category, Double> incomeByCategory = new HashMap<>();
        
        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME && 
                !t.getDate().isBefore(startOfMonth) && 
                !t.getDate().isAfter(endOfMonth)) {
                
                Category category = t.getCategory();
                double currentAmount = incomeByCategory.getOrDefault(category, 0.0);
                incomeByCategory.put(category, currentAmount + t.getAmount());
            }
        }
        
        // Create simple visualization (in a real app, use a proper chart library)
        if (incomeByCategory.isEmpty()) {
            chartContent.add(new JLabel("No income data for current month"));
        } else {
            chartContent.setLayout(new BorderLayout());
            JPanel dataPanel = new JPanel(new GridLayout(0, 1));
            
            for (Map.Entry<Category, Double> entry : incomeByCategory.entrySet()) {
                JPanel row = new JPanel(new BorderLayout());
                row.add(new JLabel(entry.getKey().getName()), BorderLayout.WEST);
                row.add(new JLabel(String.format("$%.2f", entry.getValue())), BorderLayout.EAST);
                dataPanel.add(row);
            }
            
            chartContent.add(new JScrollPane(dataPanel), BorderLayout.CENTER);
        }
        
        chartContent.revalidate();
        chartContent.repaint();
    }
    
    /**
     * Update recent transactions list in dashboard
     */
    private void updateRecentTransactions(List<Transaction> allTransactions) {
        // Get recent transactions panel
        JPanel recentTransactionsPanel = (JPanel)dashboardPanel.getComponent(2);
        JScrollPane scrollPane = (JScrollPane)recentTransactionsPanel.getComponent(0);
        
        // Create table model for recent transactions
        String[] columnNames = {"Date", "Type", "Category", "Description", "Amount"};
        Object[][] data = new Object[Math.min(5, allTransactions.size())][5];
        
        // Sort transactions by date (most recent first)
        List<Transaction> sortedTransactions = new ArrayList<>(allTransactions);
        sortedTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
        
        // Get up to 5 most recent transactions
        Locale localeToUse = (preferences.getLocale() != null) ? preferences.getLocale() : Locale.getDefault();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeToUse);
        
        // Validate currency code with safe fallback to USD
        Currency currencyToUse;
        try {
            String currencyCode = preferences.getCurrency();
            if (currencyCode != null && currencyCode.length() == 3) {
                currencyToUse = Currency.getInstance(currencyCode);
            } else {
                currencyToUse = Currency.getInstance("USD");
            }
        } catch (IllegalArgumentException e) {
            // Default to USD if currency code is invalid
            currencyToUse = Currency.getInstance("USD");
        }
        
        currencyFormat.setCurrency(currencyToUse);
        
        for (int i = 0; i < data.length; i++) {
            Transaction t = sortedTransactions.get(i);
            data[i][0] = t.getDate();
            data[i][1] = t.getType().getDisplayName();
            data[i][2] = t.getCategory().getName();
            data[i][3] = t.getDescription();
            data[i][4] = currencyFormat.format(t.getAmount());
        }
        
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        
        scrollPane.setViewportView(table);
    }
    
    /**
     * Main entry point for application
     */
    public static void main(String[] args) {
        // Start application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
