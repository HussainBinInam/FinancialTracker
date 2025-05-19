package com.finance.model;

import java.time.LocalDate;

/**
 * Concrete implementation of Income Transaction
 */
public class IncomeTransaction extends Transaction {
    
    public IncomeTransaction(double amount, String description, LocalDate date, Category category) {
        super(amount, description, date, category);
    }
    
    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}
