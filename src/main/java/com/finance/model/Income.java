package com.finance.model;

import java.time.LocalDate;

/**
 * Income transaction class extending the base Transaction class.
 * Demonstrates inheritance.
 */
public class Income extends Transaction {
    private IncomeSource source;

    public Income(double amount, String description, LocalDate date, Category category, IncomeSource source) {
        super(amount, description, date, category);
        this.source = source;
    }

    public IncomeSource getSource() {
        return source;
    }

    public void setSource(IncomeSource source) {
        this.source = source;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}
