package com.finance.model;



import java.time.LocalDate;

/**
 * Expense transaction class extending the base Transaction class.
 * Demonstrates inheritance.
 */
/**
 * Expense transaction class extending the base Transaction class.
 * Demonstrates inheritance.
 */

public class Expense extends Transaction {
    private PaymentMethod paymentMethod;
    private boolean isEssential;

    public Expense(double amount, String description, LocalDate date, Category category, 
                  PaymentMethod paymentMethod, boolean isEssential) {
        super(amount, description, date, category);
        this.paymentMethod = paymentMethod;
        this.isEssential = isEssential;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isEssential() {
        return isEssential;
    }

    public void setEssential(boolean essential) {
        isEssential = essential;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }
}
