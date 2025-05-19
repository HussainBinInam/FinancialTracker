import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    private JButton browseButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    /**
     * Creates a new preferences dialog
     * 
     * @param parent The parent frame
     * @param userPreferences The current user preferences
     */
    public PreferencesDialog(Frame parent, UserPreferences userPreferences) {
        super(parent, "Preferences", true);
        this.userPreferences = userPreferences;
        this.tempPreferences = new UserPreferences(userPreferences); // Create a copy
        
        initializeComponents();
        setupLayout();
        loadPreferences();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize UI components
     */
    private void initializeComponents() {
        // Create UI components
        darkModeCheckBox = new JCheckBox("Dark Mode");
        
        String[] currencies = {"USD - $", "EUR - €", "GBP - £", "JPY - ¥", "CAD - $"};
        currencyComboBox = new JComboBox<>(currencies);
        
        String[] dateFormats = {"MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd"};
        dateFormatComboBox = new JComboBox<>(dateFormats);
        
        autoSaveCheckBox = new JCheckBox("Auto Save");
        
        backupLocationField = new JTextField(20);
        backupLocationField.setEditable(false);
        
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseForBackupLocation();
            }
        });
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreferences();
            }
        });
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    /**
     * Set up the dialog layout
     */
    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Preferences panel
        JPanel prefsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Appearance section
        JLabel appearanceLabel = new JLabel("Appearance:");
        appearanceLabel.setFont(appearanceLabel.getFont().deriveFont(Font.BOLD));
        prefsPanel.add(appearanceLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        prefsPanel.add(darkModeCheckBox, gbc);
        
        // Region settings
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel regionLabel = new JLabel("Regional Settings:");
        regionLabel.setFont(regionLabel.getFont().deriveFont(Font.BOLD));
        prefsPanel.add(regionLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        prefsPanel.add(new JLabel("Currency:"), gbc);
        
        gbc.gridx = 1;
        prefsPanel.add(currencyComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        prefsPanel.add(new JLabel("Date Format:"), gbc);
        
        gbc.gridx = 1;
        prefsPanel.add(dateFormatComboBox, gbc);
        
        // Data management
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel dataLabel = new JLabel("Data Management:");
        dataLabel.setFont(dataLabel.getFont().deriveFont(Font.BOLD));
        prefsPanel.add(dataLabel, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 5);
        prefsPanel.add(autoSaveCheckBox, gbc);
        
        gbc.gridy++;
        prefsPanel.add(new JLabel("Backup Location:"), gbc);
        
        gbc.gridx = 1;
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backupPanel.add(backupLocationField);
        backupPanel.add(Box.createHorizontalStrut(5));
        backupPanel.add(browseButton);
        prefsPanel.add(backupPanel, gbc);
        
        contentPanel.add(prefsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPanel);
    }
    
    /**
     * Load user preferences into the UI
     */
    private void loadPreferences() {
        darkModeCheckBox.setSelected(tempPreferences.isDarkMode());
        
        // Set currency
        String currency = tempPreferences.getCurrency();
        for (int i = 0; i < currencyComboBox.getItemCount(); i++) {
            if (currencyComboBox.getItemAt(i).startsWith(currency)) {
                currencyComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Set date format
        String dateFormat = tempPreferences.getDateFormat();
        for (int i = 0; i < dateFormatComboBox.getItemCount(); i++) {
            if (dateFormatComboBox.getItemAt(i).equals(dateFormat)) {
                dateFormatComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        autoSaveCheckBox.setSelected(tempPreferences.isAutoSave());
        backupLocationField.setText(tempPreferences.getBackupLocation());
    }
    
    /**
     * Show file chooser for backup location
     */
    private void browseForBackupLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Backup Directory");
        
        if (backupLocationField.getText() != null && !backupLocationField.getText().isEmpty()) {
            fileChooser.setCurrentDirectory(new File(backupLocationField.getText()));
        }
        
        int result = fileChooser.showDialog(this, "Select");
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            backupLocationField.setText(selectedDir.getAbsolutePath());
        }
    }
    
    /**
     * Save preferences and close dialog
     */
    private void savePreferences() {
        // Update temp preferences from UI
        tempPreferences.setDarkMode(darkModeCheckBox.isSelected());
        
        String selectedCurrency = (String) currencyComboBox.getSelectedItem();
        tempPreferences.setCurrency(selectedCurrency.substring(0, 3));
        
        tempPreferences.setDateFormat((String) dateFormatComboBox.getSelectedItem());
        tempPreferences.setAutoSave(autoSaveCheckBox.isSelected());
        tempPreferences.setBackupLocation(backupLocationField.getText());
        
        // Copy temp preferences back to user preferences
        userPreferences.copyFrom(tempPreferences);
        
        preferencesSaved = true;
        dispose();
    }
    
    /**
     * Check if preferences were saved
     * 
     * @return true if preferences were saved, false otherwise
     */
    public boolean isPreferencesSaved() {
        return preferencesSaved;
    }
    
    /**
     * Get the updated user preferences
     * 
     * @return The updated user preferences
     */
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
}
