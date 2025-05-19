package com.finance.model;

import java.time.LocalDate;

/**
 * Concrete implementation of Expense Transaction
 */
public class ExpenseTransaction extends Transaction {
    
    public ExpenseTransaction(double amount, String description, LocalDate date, Category category) {
        super(amount, description, date, category);
    }
    
    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }
}
