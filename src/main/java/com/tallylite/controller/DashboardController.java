package com.tallylite.controller;

import com.tallylite.TallyLiteApp;
import com.tallylite.model.Company;
import com.tallylite.util.DashboardStats;
import com.tallylite.util.Logger;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {
    @FXML
    private Label lblCompanyName;
    @FXML
    private Label lblFinancialYear;
    @FXML
    private BorderPane mainContentArea;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Label lblWelcomeSubtitle;
    @FXML
    private Label lblLedgerCount;
    @FXML
    private Label lblVoucherCount;
    @FXML
    private Label lblInventoryCount;
    @FXML
    private Label lblInventoryValue;
    @FXML
    private Label lblTotalDebit;
    @FXML
    private Label lblTotalCredit;
    @FXML
    private Label lblTotalTransactions;
    @FXML
    private Label lblInventoryValueSummary;
    @FXML
    private Label lblAccountBalance;

    private Company currentCompany;
    private Node dashboardOverview;

    @FXML
    public void initialize() {
        // Setup keyboard shortcuts after scene is ready
        javafx.application.Platform.runLater(this::setupKeyboardShortcuts);
        
        // Store reference to dashboard overview (the ScrollPane content)
        javafx.application.Platform.runLater(() -> {
            if (mainContentArea != null && mainContentArea.getCenter() != null) {
                dashboardOverview = mainContentArea.getCenter();
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        Scene scene = rootPane.getScene();
        if (scene != null) {
            // Ctrl+N - Open Ledgers
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN),
                this::handleLedgers
            );
            
            // Ctrl+V - Open Vouchers
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN),
                this::handleVouchers
            );
            
            // Esc - Back to Home
            scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.ESCAPE),
                this::handleBackToHome
            );
        }
    }

    public void setCompany(Company company) {
        this.currentCompany = company;
        if (lblCompanyName != null) {
            lblCompanyName.setText("Company: " + company.getCompanyName());
        }
        if (lblFinancialYear != null) {
            lblFinancialYear.setText("Financial Year: " + company.getFinancialYear());
        }
        if (lblWelcomeSubtitle != null) {
            lblWelcomeSubtitle.setText("Welcome to " + company.getCompanyName() + " - " + company.getFinancialYear());
        }
        
        // Load and display statistics
        javafx.application.Platform.runLater(this::loadStatistics);
    }
    
    private void loadStatistics() {
        if (currentCompany == null) return;
        
        try {
            DashboardStats.Stats stats = DashboardStats.getStats(currentCompany.getCompanyName());
            
            // Update statistics labels with animation
            updateLabelWithAnimation(lblLedgerCount, String.valueOf(stats.getLedgerCount()));
            updateLabelWithAnimation(lblVoucherCount, String.valueOf(stats.getVoucherCount()));
            updateLabelWithAnimation(lblInventoryCount, String.valueOf(stats.getInventoryCount()));
            updateLabelWithAnimation(lblInventoryValue, "₹" + String.format("%.2f", stats.getTotalInventoryValue()));
            updateLabelWithAnimation(lblTotalDebit, "₹" + String.format("%.2f", stats.getTotalDebit()));
            updateLabelWithAnimation(lblTotalCredit, "₹" + String.format("%.2f", stats.getTotalCredit()));
            updateLabelWithAnimation(lblTotalTransactions, String.valueOf(stats.getVoucherCount()));
            updateLabelWithAnimation(lblInventoryValueSummary, "₹" + String.format("%.2f", stats.getTotalInventoryValue()));
            
            double balance = stats.getTotalDebit() - stats.getTotalCredit();
            updateLabelWithAnimation(lblAccountBalance, "₹" + String.format("%.2f", balance));
            
        } catch (Exception e) {
            Logger.log("DASHBOARD_STATS", "Error loading statistics: " + e.getMessage());
        }
    }
    
    private void updateLabelWithAnimation(Label label, String value) {
        if (label == null) return;
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            label.setText(value);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), label);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    @FXML
    private void handleLedgers() {
        loadModule("/com/tallylite/view/Ledger.fxml");
        Logger.log("NAVIGATION", "Opened Ledgers module");
    }

    @FXML
    private void handleVouchers() {
        loadModule("/com/tallylite/view/Voucher.fxml");
        Logger.log("NAVIGATION", "Opened Vouchers module");
    }

    @FXML
    private void handleInventory() {
        loadModule("/com/tallylite/view/Inventory.fxml");
        Logger.log("NAVIGATION", "Opened Inventory module");
    }

    @FXML
    private void handleReports() {
        loadModule("/com/tallylite/view/Reports.fxml");
        Logger.log("NAVIGATION", "Opened Reports module");
    }

    @FXML
    private void handleSettings() {
        loadModule("/com/tallylite/view/Settings.fxml");
        Logger.log("NAVIGATION", "Opened Settings module");
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(TallyLiteApp.class.getResource("/com/tallylite/view/Home.fxml"));
            Scene homeScene = new Scene(loader.load(), 800, 600);
            homeScene.getStylesheets().add(getClass().getResource("/com/tallylite/style/styles.css").toExternalForm());
            
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(homeScene);
            stage.setTitle("TallyLite - Home");
            
            Logger.log("NAVIGATION", "Returned to Home");
        } catch (IOException e) {
            Logger.log("NAVIGATION", "Error loading home: " + e.getMessage());
        }
    }

    private void loadModule(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(TallyLiteApp.class.getResource(fxmlPath));
            BorderPane moduleContent = loader.load();
            
            // Pass company to module controller if it has a setCompany method
            Object controller = loader.getController();
            if (controller instanceof ModuleController) {
                ((ModuleController) controller).setCompany(currentCompany);
            }
            
            // Fade transition for module loading
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), moduleContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            mainContentArea.setCenter(moduleContent);
            fadeIn.play();
        } catch (IOException e) {
            Logger.log("MODULE_LOAD", "Error loading module " + fxmlPath + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void refreshDashboard() {
        loadStatistics();
        Logger.log("DASHBOARD", "Dashboard refreshed");
    }
    
    @FXML
    private void handleShowDashboard() {
        // Restore dashboard overview if we have it stored
        if (dashboardOverview != null) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dashboardOverview);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            mainContentArea.setCenter(dashboardOverview);
            fadeIn.play();
            refreshDashboard(); // Refresh statistics
            Logger.log("NAVIGATION", "Returned to Dashboard");
        } else {
            // If not stored, refresh current view
            refreshDashboard();
        }
    }

    public Company getCurrentCompany() {
        return currentCompany;
    }
}

interface ModuleController {
    void setCompany(Company company);
}

