package com.finance.model;
/**
 * Enum representing sources of income
 */


/**
 * Enum representing sources of income
 */
public enum IncomeSource {
    SALARY("Salary"),
    INVESTMENT("Investment"),
    BUSINESS("Business"),
    OTHER("Other");

    private final String displayName;

    IncomeSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
