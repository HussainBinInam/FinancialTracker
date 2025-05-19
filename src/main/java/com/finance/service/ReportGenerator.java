package com.finance.service;

import com.finance.model.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Class for generating financial reports
 * Demonstrates composition and utility patterns
 */
public class ReportGenerator {
    // Currency formatter
    private final NumberFormat currencyFormatter;
    private final List<Transaction> transactions;
    private final List<Budget> budgets;
    
    /**
     * Constructor for ReportGenerator
     */
    public ReportGenerator(List<Transaction> transactions, List<Budget> budgets, Currency currency, Locale locale) {
        this.transactions = transactions;
        this.budgets = budgets;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        this.currencyFormatter.setCurrency(currency);
    }
    
    /**
     * Generate monthly summary report
     */
    public String generateMonthlySummaryReport(YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();
        
        StringBuilder report = new StringBuilder();
        report.append("Monthly Financial Summary for ")
              .append(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")))
              .append("\n")
              .append("======================================\n\n");
        
        // Calculate totals
        double totalIncome = FinancialCalculator.calculateTotalIncome(transactions, startDate, endDate);
        double totalExpenses = FinancialCalculator.calculateTotalExpenses(transactions, startDate, endDate);
        double netSavings = FinancialCalculator.calculateNetSavings(transactions, startDate, endDate);
        double savingsRate = FinancialCalculator.calculateSavingsRate(transactions, startDate, endDate) * 100;
        
        // Income and expense summary
        report.append("INCOME & EXPENSE SUMMARY:\n")
              .append("Total Income: ").append(currencyFormatter.format(totalIncome)).append("\n")
              .append("Total Expenses: ").append(currencyFormatter.format(totalExpenses)).append("\n")
              .append("Net Savings: ").append(currencyFormatter.format(netSavings)).append("\n")
              .append("Savings Rate: ").append(String.format("%.2f%%", savingsRate)).append("\n\n");
        
        // Income breakdown
        report.append("INCOME BREAKDOWN:\n");
        Map<Category, Double> incomeByCategory = FinancialCalculator.calculateIncomeByCategory(
            transactions, startDate, endDate);
        
        if (incomeByCategory.isEmpty()) {
            report.append("No income recorded for this period.\n\n");
        } else {
            incomeByCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    report.append(entry.getKey().getName())
                          .append(": ")
                          .append(currencyFormatter.format(entry.getValue()))
                          .append(" (")
                          .append(String.format("%.1f%%", (entry.getValue() / totalIncome) * 100))
                          .append(")\n");
                });
            report.append("\n");
        }
        
        // Expense breakdown
        report.append("EXPENSE BREAKDOWN:\n");
        Map<Category, Double> expensesByCategory = FinancialCalculator.calculateExpensesByCategory(
            transactions, startDate, endDate);
        
        if (expensesByCategory.isEmpty()) {
            report.append("No expenses recorded for this period.\n\n");
        } else {
            expensesByCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> {
                    report.append(entry.getKey().getName())
                          .append(": ")
                          .append(currencyFormatter.format(entry.getValue()))
                          .append(" (")
                          .append(String.format("%.1f%%", (entry.getValue() / totalExpenses) * 100))
                          .append(")\n");
                });
            report.append("\n");
        }
        
        // Budget status
        report.append("BUDGET STATUS:\n");
        Map<Budget, Double> budgetStatus = FinancialCalculator.calculateBudgetStatus(
            transactions, budgets, month);
        
        if (budgetStatus.isEmpty()) {
            report.append("No budgets set for this period.\n\n");
        } else {
            budgetStatus.entrySet().forEach(entry -> {
                Budget budget = entry.getKey();
                double remaining = entry.getValue();
                double spent = budget.getPlannedAmount() - remaining;
                double percentage = (spent / budget.getPlannedAmount()) * 100;
                
                report.append(budget.getCategory().getName())
                      .append(" - Planned: ")
                      .append(currencyFormatter.format(budget.getPlannedAmount()))
                      .append(", Spent: ")
                      .append(currencyFormatter.format(spent))
                      .append(" (")
                      .append(String.format("%.1f%%", percentage))
                      .append(")\n");
                
                if (remaining < 0) {
                    report.append("  ⚠️ Over budget by ")
                          .append(currencyFormatter.format(Math.abs(remaining)))
                          .append("\n");
                } else {
                    report.append("  Remaining: ")
                          .append(currencyFormatter.format(remaining))
                          .append("\n");
                }
            });
            report.append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Generate yearly summary report
     */
    public String generateYearlySummaryReport(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        StringBuilder report = new StringBuilder();
        report.append("Yearly Financial Summary for ")
              .append(year)
              .append("\n")
              .append("======================================\n\n");
        
        // Calculate yearly totals
        double totalIncome = FinancialCalculator.calculateTotalIncome(transactions, startDate, endDate);
        double totalExpenses = FinancialCalculator.calculateTotalExpenses(transactions, startDate, endDate);
        double netSavings = FinancialCalculator.calculateNetSavings(transactions, startDate, endDate);
        double savingsRate = FinancialCalculator.calculateSavingsRate(transactions, startDate, endDate) * 100;
        
        report.append("YEARLY SUMMARY:\n")
              .append("Total Income: ").append(currencyFormatter.format(totalIncome)).append("\n")
              .append("Total Expenses: ").append(currencyFormatter.format(totalExpenses)).append("\n")
              .append("Net Savings: ").append(currencyFormatter.format(netSavings)).append("\n")
              .append("Savings Rate: ").append(String.format("%.2f%%", savingsRate)).append("\n\n");
        
        // Monthly breakdown
        report.append("MONTHLY BREAKDOWN:\n");
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();
            
            double monthlyIncome = FinancialCalculator.calculateTotalIncome(
                transactions, monthStart, monthEnd);
            double monthlyExpenses = FinancialCalculator.calculateTotalExpenses(
                transactions, monthStart, monthEnd);
            double monthlySavings = monthlyIncome - monthlyExpenses;
            
            report.append(yearMonth.format(DateTimeFormatter.ofPattern("MMMM")))
                  .append(": Income = ").append(currencyFormatter.format(monthlyIncome))
                  .append(", Expenses = ").append(currencyFormatter.format(monthlyExpenses))
                  .append(", Savings = ").append(currencyFormatter.format(monthlySavings))
                  .append("\n");
        }
        
        report.append("\n");
        
        // Top spending categories
        report.append("TOP SPENDING CATEGORIES:\n");
        Map<Category, Double> yearlyExpensesByCategory = FinancialCalculator.calculateExpensesByCategory(
            transactions, startDate, endDate);
        
        if (yearlyExpensesByCategory.isEmpty()) {
            report.append("No expenses recorded for this year.\n\n");
        } else {
            yearlyExpensesByCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(entry -> {
                    report.append(entry.getKey().getName())
                          .append(": ")
                          .append(currencyFormatter.format(entry.getValue()))
                          .append(" (")
                          .append(String.format("%.1f%%", (entry.getValue() / totalExpenses) * 100))
                          .append(")\n");
                });
        }
        
        return report.toString();
    }
    
    /**
     * Generate cash flow report for a specific period
     */
    public String generateCashFlowReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder report = new StringBuilder();
        String periodStr = startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + 
                           " to " + 
                           endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        
        report.append("Cash Flow Report: ")
              .append(periodStr)
              .append("\n")
              .append("======================================\n\n");
        
        // Get transactions for the period, sorted by date
        List<Transaction> periodTransactions = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate)) {
                periodTransactions.add(t);
            }
        }
        periodTransactions.sort(Comparator.comparing(Transaction::getDate));
        
        // Initial and final balances
        double openingBalance = calculateBalanceBeforeDate(startDate);
        double currentBalance = openingBalance;
        
        report.append("Opening Balance: ").append(currencyFormatter.format(openingBalance)).append("\n\n");
        report.append("TRANSACTIONS:\n");
        report.append(String.format("%-12s %-10s %-20s %-30s %-10s %-15s\n", 
                      "Date", "Type", "Category", "Description", "Amount", "Balance"));
        report.append("--------------------------------------------------------------------------------\n");
        
        // List all transactions with running balance
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy");
        for (Transaction t : periodTransactions) {
            if (t.getType() == TransactionType.INCOME) {
                currentBalance += t.getAmount();
            } else {
                currentBalance -= t.getAmount();
            }
            
            report.append(String.format("%-12s %-10s %-20s %-30s %10s %15s\n",
                          t.getDate().format(dateFormat),
                          t.getType().getDisplayName(),
                          t.getCategory().getName(),
                          t.getDescription(),
                          currencyFormatter.format(t.getAmount()),
                          currencyFormatter.format(currentBalance)));
        }
        
        report.append("\nClosing Balance: ").append(currencyFormatter.format(currentBalance)).append("\n");
        report.append("Net Change: ").append(currencyFormatter.format(currentBalance - openingBalance)).append("\n");
        
        return report.toString();
    }
    
    /**
     * Calculate balance before a specific date
     */
    private double calculateBalanceBeforeDate(LocalDate date) {
        double balance = 0;
        
        for (Transaction transaction : transactions) {
            if (transaction.getDate().isBefore(date)) {
                if (transaction.getType() == TransactionType.INCOME) {
                    balance += transaction.getAmount();
                } else {
                    balance -= transaction.getAmount();
                }
            }
        }
        
        return balance;
    }
}
