package com.finance.ui;

/**
 * Interface for components that need to be notified of data changes
 */
public interface DataChangeListener {
    /**
     * Called when data has changed and components need to refresh
     */
    void onDataChanged();
}
