package com.finance.service;

import com.finance.model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class with static methods for financial calculations
 * Demonstrates static methods and utility patterns
 */
public class FinancialCalculator {
    // Private constructor to prevent instantiation
    private FinancialCalculator() {}
    
    /**
     * Calculate total income for a given period
     */
    public static double calculateTotalIncome(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Calculate total expenses for a given period
     */
    public static double calculateTotalExpenses(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    /**
     * Calculate net savings (income - expenses) for a given period
     */
    public static double calculateNetSavings(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        double income = calculateTotalIncome(transactions, startDate, endDate);
        double expenses = calculateTotalExpenses(transactions, startDate, endDate);
        return income - expenses;
    }
    
    /**
     * Calculate savings rate (savings / income) for a given period
     */
    public static double calculateSavingsRate(List<Transaction> transactions, LocalDate startDate, LocalDate endDate) {
        double income = calculateTotalIncome(transactions, startDate, endDate);
        double netSavings = calculateNetSavings(transactions, startDate, endDate);
        
        if (income == 0) {
            return 0;
        }
        
        return netSavings / income;
    }
    
    /**
     * Calculate expenses by category for a given period
     */
    public static Map<Category, Double> calculateExpensesByCategory(List<Transaction> transactions, 
                                                LocalDate startDate, LocalDate endDate) {
        Map<Category, Double> expensesByCategory = new HashMap<>();
        
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        for (Transaction transaction : filteredTransactions) {
            Category category = transaction.getCategory();
            double currentAmount = expensesByCategory.getOrDefault(category, 0.0);
            expensesByCategory.put(category, currentAmount + transaction.getAmount());
        }
        
        return expensesByCategory;
    }
    
    /**
     * Calculate income by category for a given period
     */
    public static Map<Category, Double> calculateIncomeByCategory(List<Transaction> transactions, 
                                               LocalDate startDate, LocalDate endDate) {
        Map<Category, Double> incomeByCategory = new HashMap<>();
        
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        for (Transaction transaction : filteredTransactions) {
            Category category = transaction.getCategory();
            double currentAmount = incomeByCategory.getOrDefault(category, 0.0);
            incomeByCategory.put(category, currentAmount + transaction.getAmount());
        }
        
        return incomeByCategory;
    }
    
    /**
     * Check budget status for each category in the given period
     */
    public static Map<Budget, Double> calculateBudgetStatus(List<Transaction> transactions, 
                                          List<Budget> budgets, YearMonth period) {
        Map<Budget, Double> budgetStatus = new HashMap<>();
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();
        
        // Calculate expenses by category
        Map<Category, Double> expensesByCategory = calculateExpensesByCategory(
            transactions, startDate, endDate);
        
        // Calculate status for each budget
        for (Budget budget : budgets) {
            if (budget.getPeriod().equals(period)) {
                double actual = expensesByCategory.getOrDefault(budget.getCategory(), 0.0);
                double remaining = budget.getPlannedAmount() - actual;
                budgetStatus.put(budget, remaining);
            }
        }
        
        return budgetStatus;
    }
    
    /**
     * Calculate average daily expense for a given period
     */
    public static double calculateAverageDailyExpense(List<Transaction> transactions, 
                                    LocalDate startDate, LocalDate endDate) {
        double totalExpenses = calculateTotalExpenses(transactions, startDate, endDate);
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1; // Include both start and end dates
        
        if (days <= 0) {
            return 0;
        }
        
        return totalExpenses / days;
    }
    
    /**
     * Calculate average monthly expense for a given period
     */
    public static double calculateAverageMonthlyExpense(List<Transaction> transactions, 
                                     LocalDate startDate, LocalDate endDate) {
        double totalExpenses = calculateTotalExpenses(transactions, startDate, endDate);
        
        // Calculate number of months (partial months are counted proportionally)
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        
        if (startMonth.equals(endMonth)) {
            // Same month
            return totalExpenses;
        }
        
        long monthsBetween = ChronoUnit.MONTHS.between(startMonth, endMonth) + 1;
        
        return totalExpenses / monthsBetween;
    }
    
    /**
     * Calculate projected monthly savings based on current rate
     */
    public static double calculateProjectedMonthlySavings(List<Transaction> transactions, 
                                      int numberOfPreviousMonths) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(numberOfPreviousMonths).withDayOfMonth(1);
        
        double totalSavings = calculateNetSavings(transactions, startDate, today);
        
        return totalSavings / numberOfPreviousMonths;
    }
    
    /**
     * Calculate essential vs. non-essential expenses ratio
     */
    public static double calculateEssentialExpensesRatio(List<Transaction> transactions, 
                                    LocalDate startDate, LocalDate endDate) {
        List<Transaction> filteredTransactions = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
        
        double essentialExpenses = 0;
        double totalExpenses = 0;
        
        for (Transaction transaction : filteredTransactions) {
            totalExpenses += transaction.getAmount();
            
            if (transaction instanceof Expense) {
                Expense expense = (Expense) transaction;
                if (expense.isEssential()) {
                    essentialExpenses += expense.getAmount();
                }
            }
        }
        
        if (totalExpenses == 0) {
            return 0;
        }
        
        return essentialExpenses / totalExpenses;
    }
}
