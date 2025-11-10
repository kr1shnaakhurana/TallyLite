package com.tallylite.controller;

import com.tallylite.TallyLiteApp;
import com.tallylite.manager.CompanyManager;
import com.tallylite.model.Company;
import com.tallylite.util.Logger;
import com.tallylite.util.SettingsManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class HomeController {
    @FXML
    private Button btnCreateCompany;
    @FXML
    private Button btnLoadCompany;
    @FXML
    private Button btnDeleteCompany;
    @FXML
    private Button btnExit;
    @FXML
    private ListView<String> companyListView;

    private ObservableList<String> companyList;

    @FXML
    public void initialize() {
        companyList = FXCollections.observableArrayList();
        companyListView.setItems(companyList);
        refreshCompanyList();
        
        // Auto-load recent company after scene is ready
        javafx.application.Platform.runLater(() -> {
            String recentCompany = SettingsManager.loadSettings().getRecentCompany();
            if (recentCompany != null && !recentCompany.isEmpty() && companyList.contains(recentCompany)) {
                // Auto-select but don't auto-load - user can click Load
                companyListView.getSelectionModel().select(recentCompany);
            }
        });

        Logger.log("SESSION_START", "Application started");
    }

    @FXML
    private void handleCreateCompany() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Create New Company");
        dialog.setHeaderText("Enter company details");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Company Name");
        TextField financialYearField = new TextField();
        financialYearField.setPromptText("Financial Year (e.g., 2024-25)");

        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label("Company Name:"), nameField,
                new Label("Financial Year:"), financialYearField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new String[]{nameField.getText(), financialYearField.getText()};
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String name = result[0].trim();
            String financialYear = result[1].trim();

            if (name.isEmpty() || financialYear.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
                return;
            }

            if (CompanyManager.createCompany(name, financialYear)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Company created successfully!");
                refreshCompanyList();
                loadCompany(name);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create company. It may already exist.");
            }
        });
    }

    @FXML
    private void handleLoadCompany() {
        String selectedCompany = companyListView.getSelectionModel().getSelectedItem();
        if (selectedCompany == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a company to load");
            return;
        }
        loadCompany(selectedCompany);
    }

    private void loadCompany(String companyName) {
        Company company = CompanyManager.loadCompany(companyName);
        if (company != null) {
            try {
                // Save recent company
                var settings = SettingsManager.loadSettings();
                settings.setRecentCompany(companyName);
                SettingsManager.saveSettings(settings);

                // Load dashboard
                FXMLLoader loader = new FXMLLoader(TallyLiteApp.class.getResource("/com/tallylite/view/Dashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load(), 1200, 800);
                dashboardScene.getStylesheets().add(getClass().getResource("/com/tallylite/style/styles.css").toExternalForm());
                
                DashboardController controller = loader.getController();
                controller.setCompany(company);

                // Get stage safely - check if scene is available
                Stage stage = null;
                if (btnCreateCompany.getScene() != null) {
                    stage = (Stage) btnCreateCompany.getScene().getWindow();
                } else {
                    // If scene is not available, try to get from any other node
                    if (companyListView.getScene() != null) {
                        stage = (Stage) companyListView.getScene().getWindow();
                    }
                }
                
                if (stage != null) {
                    stage.setScene(dashboardScene);
                    stage.setTitle("TallyLite - " + company.getCompanyName());
                    Logger.log("COMPANY_LOADED", "Loaded company: " + companyName);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Cannot access application window");
                    Logger.log("LOAD_DASHBOARD", "Error: Cannot access stage");
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
                Logger.log("LOAD_DASHBOARD", "Error: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load company");
        }
    }

    @FXML
    private void handleDeleteCompany() {
        String selectedCompany = companyListView.getSelectionModel().getSelectedItem();
        if (selectedCompany == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a company to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Company");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedCompany + "'? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (CompanyManager.deleteCompany(selectedCompany)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Company deleted successfully");
                    refreshCompanyList();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete company");
                }
            }
        });
    }

    @FXML
    private void handleExit() {
        Logger.log("SESSION_END", "Application closed");
        System.exit(0);
    }

    private void refreshCompanyList() {
        List<String> companies = CompanyManager.listCompanies();
        companyList.clear();
        companyList.addAll(companies);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

