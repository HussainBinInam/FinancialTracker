package com.finance.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import com.finance.model.UserPreferences;

/**
 * Dialog for managing application preferences
 */
public class PreferencesDialog extends JDialog {
    private UserPreferences userPreferences;
    private UserPreferences tempPreferences;
    private boolean preferencesSaved = false;
    
    private JCheckBox darkModeCheckBox;
    private JComboBox<String> currencyComboBox;
    private JComboBox<String> dateFormatComboBox;
    private JCheckBox autoSaveCheckBox;
    private JTextField backupLocationField;
    
    /**
     * Constructor
     */
    public PreferencesDialog(JFrame parent, UserPreferences userPreferences) {
        super(parent, "Preferences", true);
        this.userPreferences = userPreferences;
        this.tempPreferences = new UserPreferences();
        
        // Copy current preferences to temp
        tempPreferences.setDarkMode(userPreferences.isDarkMode());
        tempPreferences.setCurrency(userPreferences.getCurrency());
        tempPreferences.setDateFormat(userPreferences.getDateFormat());
        tempPreferences.setAutoSave(userPreferences.isAutoSave());
        tempPreferences.setBackupLocation(userPreferences.getBackupLocation());
        
        setupUI();
        loadPreferences();
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Set up the user interface
     */
    private void setupUI() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        
        // Appearance section
        formPanel.add(new JLabel("Dark Mode:"));
        darkModeCheckBox = new JCheckBox("Enable dark mode");
        formPanel.add(darkModeCheckBox);
        
        formPanel.add(new JLabel("Currency:"));
        currencyComboBox = new JComboBox<>(new String[] {"USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)", "CAD ($)"});
        formPanel.add(currencyComboBox);
        
        formPanel.add(new JLabel("Date Format:"));
        dateFormatComboBox = new JComboBox<>(new String[] {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd"});
        formPanel.add(dateFormatComboBox);
        
        // Data section
        formPanel.add(new JLabel("Auto Save:"));
        autoSaveCheckBox = new JCheckBox("Automatically save changes");
        formPanel.add(autoSaveCheckBox);
        
        formPanel.add(new JLabel("Backup Location:"));
        JPanel backupPanel = new JPanel(new BorderLayout());
        backupLocationField = new JTextField();
        JButton browseButton = new JButton("Browse...");
        backupPanel.add(backupLocationField, BorderLayout.CENTER);
        backupPanel.add(browseButton, BorderLayout.EAST);
        formPanel.add(backupPanel);
        
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        JButton applyButton = new JButton("Apply");
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        okButton.addActionListener(e -> {
            savePreferences();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        applyButton.addActionListener(e -> savePreferences());
        
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showDialog(this, "Select");
            if (result == JFileChooser.APPROVE_OPTION) {
                backupLocationField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        setContentPane(contentPanel);
    }
    
    /**
     * Load preferences from the preferences object
     */
    private void loadPreferences() {
        darkModeCheckBox.setSelected(tempPreferences.isDarkMode());
        currencyComboBox.setSelectedItem(tempPreferences.getCurrency());
        dateFormatComboBox.setSelectedItem(tempPreferences.getDateFormat());
        autoSaveCheckBox.setSelected(tempPreferences.isAutoSave());
        backupLocationField.setText(tempPreferences.getBackupLocation());
    }
    
    /**
     * Save preferences to the temporary preferences object
     */
    private void savePreferences() {
        tempPreferences.setDarkMode(darkModeCheckBox.isSelected());
        tempPreferences.setCurrency((String)currencyComboBox.getSelectedItem());
        tempPreferences.setDateFormat((String)dateFormatComboBox.getSelectedItem());
        tempPreferences.setAutoSave(autoSaveCheckBox.isSelected());
        tempPreferences.setBackupLocation(backupLocationField.getText());
        preferencesSaved = true;
    }
    
    /**
     * Check if preferences were saved
     */
    public boolean isPreferencesSaved() {
        return preferencesSaved;
    }
    
    /**
     * Get the updated preferences
     */
    public UserPreferences getUpdatedPreferences() {
        return tempPreferences;
    }
}
