package com.tallylite.controller;

import com.tallylite.manager.ReportsManager;
import com.tallylite.model.Company;
import com.tallylite.model.ReportRow;
import com.tallylite.util.ExcelExporter;
import com.tallylite.util.Logger;
import com.tallylite.util.PDFExporter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class ReportsController implements ModuleController {
    @FXML
    private Label lblReportTitle;
    @FXML
    private TableView<ReportRow> reportTableView;
    @FXML
    private TableColumn<ReportRow, String> colReport1;
    @FXML
    private TableColumn<ReportRow, String> colReport2;
    @FXML
    private TableColumn<ReportRow, String> colReport3;

    private Company company;
    private ObservableList<ReportRow> reportList;
    private String currentReportType = "";

    @Override
    public void setCompany(Company company) {
        this.company = company;
    }

    @FXML
    private void initialize() {
        // Initialize table columns
        colReport1.setCellValueFactory(new PropertyValueFactory<>("column1"));
        colReport2.setCellValueFactory(new PropertyValueFactory<>("column2"));
        colReport3.setCellValueFactory(new PropertyValueFactory<>("column3"));

        reportList = FXCollections.observableArrayList();
        reportTableView.setItems(reportList);
    }

    @FXML
    private void handleTrialBalance() {
        currentReportType = "Trial Balance";
        lblReportTitle.setText("Trial Balance");
        
        List<ReportsManager.TrialBalanceEntry> entries = ReportsManager.calculateTrialBalance(company.getCompanyName());
        reportList.clear();
        
        double totalDebit = 0;
        double totalCredit = 0;
        
        for (ReportsManager.TrialBalanceEntry entry : entries) {
            reportList.add(new ReportRow(
                entry.getLedgerName(),
                String.format("%.2f", entry.getDebit()),
                String.format("%.2f", entry.getCredit())
            ));
            totalDebit += entry.getDebit();
            totalCredit += entry.getCredit();
        }
        
        // Add totals row
        reportList.add(new ReportRow(
            "TOTAL",
            String.format("%.2f", totalDebit),
            String.format("%.2f", totalCredit)
        ));
        
        Logger.log("REPORT_VIEW", "Generated Trial Balance");
    }

    @FXML
    private void handleProfitLoss() {
        currentReportType = "Profit & Loss";
        lblReportTitle.setText("Profit & Loss Statement");
        
        List<ReportsManager.ProfitLossEntry> entries = ReportsManager.calculateProfitLoss(company.getCompanyName());
        reportList.clear();
        
        for (ReportsManager.ProfitLossEntry entry : entries) {
            reportList.add(new ReportRow(
                entry.getItem(),
                "",
                String.format("%.2f", entry.getAmount())
            ));
        }
        
        Logger.log("REPORT_VIEW", "Generated Profit & Loss");
    }

    @FXML
    private void handleBalanceSheet() {
        currentReportType = "Balance Sheet";
        lblReportTitle.setText("Balance Sheet");
        
        List<ReportsManager.BalanceSheetEntry> entries = ReportsManager.calculateBalanceSheet(company.getCompanyName());
        reportList.clear();
        
        for (ReportsManager.BalanceSheetEntry entry : entries) {
            reportList.add(new ReportRow(
                entry.getItem(),
                "",
                String.format("%.2f", entry.getAmount())
            ));
        }
        
        Logger.log("REPORT_VIEW", "Generated Balance Sheet");
    }

    @FXML
    private void handleExportPDF() {
        if (currentReportType.isEmpty() || reportList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Report", "Please generate a report first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName(currentReportType.replace(" ", "_") + ".pdf");
        
        File file = fileChooser.showSaveDialog(reportTableView.getScene().getWindow());
        if (file != null) {
            if (PDFExporter.exportReport(file, currentReportType, lblReportTitle.getText(), 
                                        reportList, company)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Report exported successfully to:\n" + file.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export PDF");
            }
        }
    }

    @FXML
    private void handleExportExcel() {
        if (currentReportType.isEmpty() || reportList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Report", "Please generate a report first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName(currentReportType.replace(" ", "_") + ".xlsx");
        
        File file = fileChooser.showSaveDialog(reportTableView.getScene().getWindow());
        if (file != null) {
            if (ExcelExporter.exportReport(file, currentReportType, lblReportTitle.getText(), 
                                          reportList, company)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                         "Report exported successfully to:\n" + file.getAbsolutePath());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to export Excel");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

