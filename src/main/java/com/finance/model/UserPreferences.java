package com.finance.model;


import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;

/**
 * Class to store user preferences
 * Demonstrates encapsulation and singleton pattern
 */
public class UserPreferences implements Serializable {
    // Singleton instance (Static keyword)
    private static UserPreferences instance;

    // Default values
    private String username;
    private Currency defaultCurrency;
    private Locale locale;
    private boolean darkMode;
    private boolean showNotifications;
    private int dataRetentionPeriodDays;
    private String currency;
    private String dateFormat;
    private boolean autoSave;
    private String backupLocation;

    // Private constructor
    public UserPreferences() {
        this.username = "User";
        this.defaultCurrency = Currency.getInstance(Locale.getDefault());
        this.locale = Locale.getDefault();
        this.darkMode = false;
        this.showNotifications = true;
        this.dataRetentionPeriodDays = 365;
        this.currency = "USD";
        this.dateFormat = "yyyy-MM-dd";
        this.autoSave = true;
        this.backupLocation = System.getProperty("user.home");
    }

    // Static method to get instance (Static keyword)
    public static synchronized UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }

    // Reset instance for importing preferences
    public static void resetInstance() {
        instance = null;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(Currency defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public Locale getLocale() {
        return locale;
    }
    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public boolean isDarkMode() {
        return darkMode;
    }
    
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
    
    public boolean isShowNotifications() {
        return showNotifications;
    }
    
    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }
    
    public int getDataRetentionPeriodDays() {
        return dataRetentionPeriodDays;
    }
    
    public void setDataRetentionPeriodDays(int dataRetentionPeriodDays) {
        this.dataRetentionPeriodDays = dataRetentionPeriodDays;
    }
    
    /**
     * Get the currency string
     * @return the currency string
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * Set the currency string
     * @param currency the currency string to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    /**
     * Get the date format
     * @return the date format string
     */
    public String getDateFormat() {
        return dateFormat;
    }
    
    /**
     * Set the date format
     * @param dateFormat the date format to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    /**
     * Check if auto save is enabled
     * @return true if auto save is enabled, false otherwise
     */
    public boolean isAutoSave() {
        return autoSave;
    }
    
    /**
     * Set the auto save option
     * @param autoSave the auto save setting to set
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    /**
     * Get the backup location
     * @return the backup location path
     */
    public String getBackupLocation() {
        return backupLocation;
    }
    
    /**
     * Set the backup location
     * @param backupLocation the backup location path to set
     */
    public void setBackupLocation(String backupLocation) {
        this.backupLocation = backupLocation;
    }
    
    /**
     * Alias for isDarkMode to support both naming conventions
     */
    public boolean getDarkMode() {
        return darkMode;
    }
}


