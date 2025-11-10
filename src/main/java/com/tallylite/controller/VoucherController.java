package com.tallylite.controller;

import com.tallylite.manager.InventoryManager;
import com.tallylite.manager.LedgerManager;
import com.tallylite.manager.VoucherManager;
import com.tallylite.model.Company;
import com.tallylite.model.InventoryItem;
import com.tallylite.model.Voucher;
import com.tallylite.util.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VoucherController implements ModuleController {
    @FXML
    private ComboBox<String> cbVoucherType;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<String> cbLedgerDr;
    @FXML
    private ComboBox<String> cbLedgerCr;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextArea txtNarration;
    @FXML
    private TableView<Voucher> voucherTableView;
    @FXML
    private TableColumn<Voucher, String> colVoucherDate;
    @FXML
    private TableColumn<Voucher, String> colVoucherType;
    @FXML
    private TableColumn<Voucher, String> colVoucherDr;
    @FXML
    private TableColumn<Voucher, String> colVoucherCr;
    @FXML
    private TableColumn<Voucher, Double> colVoucherAmount;
    @FXML
    private TableColumn<Voucher, String> colVoucherNarration;

    private Company company;
    private ObservableList<Voucher> voucherList;

    @Override
    public void setCompany(Company company) {
        this.company = company;
        initializeVoucherTypes();
        loadLedgers();
        loadVouchers();
    }

    @FXML
    private void initialize() {
        // Initialize date picker with today's date
        dpDate.setValue(LocalDate.now());
        
        // Initialize table columns
        colVoucherDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colVoucherType.setCellValueFactory(new PropertyValueFactory<>("voucherType"));
        colVoucherDr.setCellValueFactory(new PropertyValueFactory<>("ledgerDr"));
        colVoucherCr.setCellValueFactory(new PropertyValueFactory<>("ledgerCr"));
        colVoucherAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colVoucherNarration.setCellValueFactory(new PropertyValueFactory<>("narration"));
        
        // Format amount column
        colVoucherAmount.setCellFactory(column -> new TableCell<Voucher, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });

        voucherList = FXCollections.observableArrayList();
        voucherTableView.setItems(voucherList);
    }

    private void initializeVoucherTypes() {
        cbVoucherType.getItems().addAll("Sales", "Purchase", "Receipt", "Payment", "Journal");
    }

    private void loadLedgers() {
        List<String> ledgerNames = LedgerManager.getLedgerNames(company.getCompanyName());
        cbLedgerDr.getItems().clear();
        cbLedgerCr.getItems().clear();
        cbLedgerDr.getItems().addAll(ledgerNames);
        cbLedgerCr.getItems().addAll(ledgerNames);
    }

    @FXML
    private void handleSaveVoucher() {
        // Validate fields
        if (cbVoucherType.getValue() == null || cbVoucherType.getValue().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a voucher type");
            return;
        }

        if (dpDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a date");
            return;
        }

        if (cbLedgerDr.getValue() == null || cbLedgerDr.getValue().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a debit ledger");
            return;
        }

        if (cbLedgerCr.getValue() == null || cbLedgerCr.getValue().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a credit ledger");
            return;
        }

        // Validate that debit and credit ledgers are not the same
        if (cbLedgerDr.getValue().equals(cbLedgerCr.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Debit and Credit ledgers cannot be the same");
            return;
        }

        if (txtAmount.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            if (amount <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Amount must be greater than zero");
                return;
            }

            // Create voucher
            String date = dpDate.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            Voucher voucher = new Voucher(
                date,
                cbVoucherType.getValue(),
                cbLedgerDr.getValue(),
                cbLedgerCr.getValue(),
                amount,
                txtNarration.getText().trim()
            );

            // Save voucher
            if (VoucherManager.saveVoucher(company.getCompanyName(), voucher)) {
                // Update inventory if applicable
                updateInventoryForVoucher(voucher);
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Voucher saved successfully");
                
                // Clear form
                cbVoucherType.setValue(null);
                dpDate.setValue(LocalDate.now());
                cbLedgerDr.setValue(null);
                cbLedgerCr.setValue(null);
                txtAmount.clear();
                txtNarration.clear();
                
                // Refresh voucher list
                loadVouchers();
                
                Logger.log("VOUCHER_SAVED", "Saved " + voucher.getVoucherType() + " voucher for " + amount);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save voucher");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid amount");
        }
    }

    @FXML
    private void handleViewVouchers() {
        loadVouchers();
        voucherTableView.setVisible(true);
    }

    private void loadVouchers() {
        List<Voucher> vouchers = VoucherManager.loadVouchers(company.getCompanyName());
        voucherList.clear();
        voucherList.addAll(vouchers);
        Logger.log("VOUCHER_VIEW", "Loaded " + vouchers.size() + " vouchers");
    }

    private void updateInventoryForVoucher(Voucher voucher) {
        String voucherType = voucher.getVoucherType();
        String debitLedger = voucher.getLedgerDr();
        double amount = voucher.getAmount();
        
        // Check if debit ledger matches an inventory item
        InventoryItem item = InventoryManager.getItem(company.getCompanyName(), debitLedger);
        
        if (item != null) {
            // Calculate quantity change based on rate
            // For simplicity, assume amount represents the value, so quantity = amount / rate
            double quantityChange = 0;
            
            if ("Sales".equals(voucherType)) {
                // Sales: reduce inventory
                quantityChange = -(amount / item.getRate());
                if (InventoryManager.updateStock(company.getCompanyName(), debitLedger, quantityChange)) {
                    Logger.log("INVENTORY_UPDATE", "Reduced stock for " + debitLedger + " by " + Math.abs(quantityChange));
                }
            } else if ("Purchase".equals(voucherType)) {
                // Purchase: increase inventory
                quantityChange = amount / item.getRate();
                if (InventoryManager.updateStock(company.getCompanyName(), debitLedger, quantityChange)) {
                    Logger.log("INVENTORY_UPDATE", "Increased stock for " + debitLedger + " by " + quantityChange);
                }
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

