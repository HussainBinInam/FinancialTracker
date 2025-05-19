package com.finance.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Budget class for managing category-based spending limits
 * Demonstrates encapsulation
 */
public class Budget implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id; // Unique identifier for the budget
    private YearMonth period;
    private Category category;
    private double amount;
    private String notes;
    private double spent; // Track how much has been spent against this budget
    private double plannedAmount;
    private String  startDate;
    private String endDate;
    /**
     * Constructor for creating a new budget
     *
     * @param period the year and month for this budget
     * @param category the category this budget applies to
     * @param amount the maximum amount for this budget period
     */
    public Budget(YearMonth period, Category category, double amount) {
        this.id = UUID.randomUUID().toString();
        this.period = period;
        this.category = category;
        this.amount = amount;
        this.notes = "";
        this.spent = 0.0;
    }
    
    /**
     * Constructor with notes parameter
     * 
     * @param period the year and month for this budget
     * @param category the category this budget applies to
     * @param amount the maximum amount for this budget period
     * @param notes any additional notes for this budget
     */
    public Budget(YearMonth period, Category category, double amount, String notes) {
        this.id = UUID.randomUUID().toString();
        this.period = period;
        this.category = category;
        this.amount = amount;
        this.notes = notes;
        this.spent = 0.0;
    }

    /**
     * Get the unique identifier for this budget
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Set the unique identifier for this budget
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Get the period (year and month)
     * @return the budget period
     */

    public double getSpent() {
        return spent;
    }

    /**
     * Set the amount spent against this budget
     * @param spent the spent amount to set
     */
    public void setSpent(double spent) {
        this.spent = spent;
    }

    /**
     * Add an expense to this budget
     * @param amount the amount to add to spent
     */
    public void addExpense(double amount) {
        this.spent += amount;
    }

    /**
     * Get the remaining amount in this budget
     * @return the remaining amount
     */
    public double getRemaining() {
        return amount - spent;
    }

    /**
     * Get the percentage spent of the budget
     * @return the percentage spent (0-100)
     */
    public double getPercentSpent() {
        if (amount == 0) return 0;
        return (spent / amount) * 100;
    }

    /**
     * Check if this budget is over the limit
     * @return true if spent exceeds amount
     */
    public boolean isOverBudget() {
        return spent > amount;
    }




    /**
     * Constructor that takes a UUID string, month, year, category, and amount
     * 
     * @param id the UUID string for this budget
     * @param month the month (1-12)
     * @param year the year
     * @param category the category this budget applies to
     * @param amount the maximum amount for this budget period
     */
    public Budget(String id, int month, int year, Category category, double amount) {
        this.id = id;
        this.period = YearMonth.of(year, month);
        this.category = category;
        this.amount = amount;
        this.notes = "";
        this.spent = 0.0;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Budget other = (Budget) obj;
        return period.equals(other.period) && category.equals(other.category);
    }

    @Override
    public int hashCode() {
        return 31 * period.hashCode() + category.hashCode();
    }







public Category getCategory() {
    return category;
}


public double getAmount() {
    return amount;
}

public void setAmount(double amount) {
    this.amount = amount;
}

public String getNotes() {
    return notes;
}

public void setNotes(String notes) {
    this.notes = notes;
}

@Override
public String toString() {
    return String.format("%s - %s - $%.2f",
            period.toString(), category.getName(), amount);
}

public void setCategory(Category category) {
    this.category = category;
}

public double getPlannedAmount() {
    return plannedAmount;
}

public void setPlannedAmount(double plannedAmount) {
    this.plannedAmount = plannedAmount;
}

public YearMonth getPeriod() {
    return period;
}

public void setPeriod(YearMonth period) {
    this.period = period;
    updateDates();
}

/**
 * Convenience accessors kept to avoid widespread changes.
 */
public int getMonth() {
    return period.getMonthValue();
}

    public int getYear() {
        return period.getYear();
    }

/**
 * @deprecated Use {@link #setPeriod(YearMonth)} instead.
 */
@Deprecated
public void setMonth(int month) {
    setPeriod(YearMonth.of(getYear(), month));
}

/**
 * @deprecated Use {@link #setPeriod(YearMonth)} instead.
 */
@Deprecated
public void setYear(int year) {
    setPeriod(YearMonth.of(year, getMonth()));
}

/**
 * Get the start date of the budget period (first day of the month)
 * @return the start date as LocalDate
 */
public LocalDate getStartDate() {
    return period.atDay(1);
}

/**
 * Get the end date of the budget period (last day of the month)
 * @return the end date as LocalDate
 */
public LocalDate getEndDate() {
    return period.atEndOfMonth();
}

/**
 * Check if a given date falls within this budget's period
 * @param date the date to check
 * @return true if the date is within this budget's period
 */
public boolean isDateInPeriod(LocalDate date) {
    YearMonth dateYearMonth = YearMonth.from(date);
    return period.equals(dateYearMonth);
}


public String getDateRange() {
    return startDate + " to " + endDate;
}


private void updateDates() {
    this.startDate = String.valueOf(period.atDay(1));
    this.endDate = String.valueOf(period.atEndOfMonth());
}}