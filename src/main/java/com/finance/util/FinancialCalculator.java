package com.finance.util;

import com.finance.model.Transaction;
import com.finance.model.TransactionType;

import java.time.LocalDate;
import java.util.List;

/**
 * Utility class for financial calculations
 */
public class FinancialCalculator {
    
    /**
     * Calculate total income for a given date range
     * 
     * @param transactions List of transactions to analyze
     * @param startDate Start date of the period (inclusive)
     * @param endDate End date of the period (inclusive)
     * @return Total income amount
     */
    public static double calculateTotalIncome(List<Transaction> transactions, 
                                            LocalDate startDate, 
                                            LocalDate endDate) {
        double total = 0;
        
        for (Transaction t : transactions) {
            LocalDate transactionDate = t.getDate();
            
            if (t.getType() == TransactionType.INCOME && 
                !transactionDate.isBefore(startDate) && 
                !transactionDate.isAfter(endDate)) {
                total += t.getAmount();
            }
        }
        
        return total;
    }
    
    /**
     * Calculate total expenses for a given date range
     * 
     * @param transactions List of transactions to analyze
     * @param startDate Start date of the period (inclusive)
     * @param endDate End date of the period (inclusive)
     * @return Total expense amount
     */
    public static double calculateTotalExpenses(List<Transaction> transactions, 
                                              LocalDate startDate, 
                                              LocalDate endDate) {
        double total = 0;
        
        for (Transaction t : transactions) {
            LocalDate transactionDate = t.getDate();
            
            if (t.getType() == TransactionType.EXPENSE && 
                !transactionDate.isBefore(startDate) && 
                !transactionDate.isAfter(endDate)) {
                total += t.getAmount();
            }
        }
        
        return total;
    }
    
    /**
     * Calculate net cashflow (income - expenses) for a given date range
     * 
     * @param transactions List of transactions to analyze
     * @param startDate Start date of the period (inclusive)
     * @param endDate End date of the period (inclusive)
     * @return Net cashflow amount
     */
    public static double calculateNetCashflow(List<Transaction> transactions, 
                                             LocalDate startDate, 
                                             LocalDate endDate) {
        double income = calculateTotalIncome(transactions, startDate, endDate);
        double expenses = calculateTotalExpenses(transactions, startDate, endDate);
        
        return income - expenses;
    }
    
    /**
     * Calculate savings rate (percentage of income saved)
     * 
     * @param transactions List of transactions to analyze
     * @param startDate Start date of the period (inclusive)
     * @param endDate End date of the period (inclusive)
     * @return Savings rate as a percentage (0-100)
     */
    public static double calculateSavingsRate(List<Transaction> transactions, 
                                            LocalDate startDate, 
                                            LocalDate endDate) {
        double income = calculateTotalIncome(transactions, startDate, endDate);
        double expenses = calculateTotalExpenses(transactions, startDate, endDate);
        
        if (income <= 0) {
            return 0; // Avoid division by zero
        }
        
        double savingsRate = ((income - expenses) / income) * 100;
        return savingsRate;
    }
}
