package com.tallylite.controller;

import com.tallylite.model.Company;
import com.tallylite.model.Settings;
import com.tallylite.util.BackupManager;
import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;
import com.tallylite.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class SettingsController implements ModuleController {
    @FXML
    private ComboBox<String> cbTheme;

    private Company company;

    @Override
    public void setCompany(Company company) {
        this.company = company;
        loadSettings();
    }

    @FXML
    private void initialize() {
        cbTheme.getItems().addAll("dark", "light");
    }

    private void loadSettings() {
        Settings settings = SettingsManager.loadSettings();
        cbTheme.setValue(settings.getTheme());
    }

    @FXML
    private void handleSaveTheme() {
        Settings settings = SettingsManager.loadSettings();
        settings.setTheme(cbTheme.getValue());
        SettingsManager.saveSettings(settings);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings Saved");
        alert.setHeaderText(null);
        alert.setContentText("Theme saved successfully. Please restart the application to apply changes.");
        alert.showAndWait();
    }

    @FXML
    private void handleBackup() {
        if (company == null) {
            showAlert(Alert.AlertType.WARNING, "No Company", "Please load a company first");
            return;
        }
        
        String backupPath = BackupManager.backupCompany(company.getCompanyName());
        if (backupPath != null) {
            showAlert(Alert.AlertType.INFORMATION, "Backup Successful", 
                     "Company backed up successfully to:\n" + backupPath);
        } else {
            showAlert(Alert.AlertType.ERROR, "Backup Failed", "Failed to create backup");
        }
    }

    @FXML
    private void handleRestore() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Backup File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
        fileChooser.setInitialDirectory(new File(FileManager.getBackupsDir()));
        
        Stage stage = (Stage) cbTheme.getScene().getWindow();
        File backupFile = fileChooser.showOpenDialog(stage);
        
        if (backupFile != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Restore");
            confirmAlert.setHeaderText("Restore Company");
            confirmAlert.setContentText("This will replace the existing company data. Are you sure?");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    if (BackupManager.restoreCompany(backupFile)) {
                        showAlert(Alert.AlertType.INFORMATION, "Restore Successful", 
                                 "Company restored successfully. Please reload the company.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Restore Failed", "Failed to restore company");
                    }
                }
            });
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About TallyLite");
        alert.setHeaderText("TallyLite v1.0.0");
        alert.setContentText(
            "Offline Accounting Software\n\n" +
            "Version: 1.0.0\n" +
            "Built with: JavaFX 17+\n\n" +
            "Features:\n" +
            "• Company Management\n" +
            "• Ledger Management\n" +
            "• Voucher Entry System\n" +
            "• Inventory Management\n" +
            "• Financial Reports (Trial Balance, P&L, Balance Sheet)\n" +
            "• PDF & Excel Export\n" +
            "• Backup & Restore\n\n" +
            "A Tally-like accounting solution for local use.\n" +
            "All data stored locally in JSON format.\n\n" +
            "© 2025 TallyLite"
        );
        alert.setResizable(true);
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Information", message);
    }
}

