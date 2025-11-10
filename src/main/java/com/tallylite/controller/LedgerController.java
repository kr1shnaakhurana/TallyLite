package com.tallylite.controller;

import com.tallylite.manager.LedgerManager;
import com.tallylite.model.Company;
import com.tallylite.model.Ledger;
import com.tallylite.util.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.util.List;

public class LedgerController implements ModuleController {
    @FXML
    private TableView<Ledger> ledgerTableView;
    @FXML
    private TableColumn<Ledger, String> colName;
    @FXML
    private TableColumn<Ledger, String> colGroup;
    @FXML
    private TableColumn<Ledger, Double> colOpeningBalance;
    @FXML
    private TableColumn<Ledger, String> colType;

    private Company company;
    private ObservableList<Ledger> ledgerList;

    @Override
    public void setCompany(Company company) {
        this.company = company;
        loadLedgers();
    }

    @FXML
    private void initialize() {
        // Initialize table columns
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGroup.setCellValueFactory(new PropertyValueFactory<>("group"));
        colOpeningBalance.setCellValueFactory(new PropertyValueFactory<>("openingBalance"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        // Format opening balance column
        colOpeningBalance.setCellFactory(column -> new TableCell<Ledger, Double>() {
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

        ledgerList = FXCollections.observableArrayList();
        ledgerTableView.setItems(ledgerList);
    }

    @FXML
    private void handleAddLedger() {
        showLedgerDialog(null);
    }

    @FXML
    private void handleEditLedger() {
        Ledger selectedLedger = ledgerTableView.getSelectionModel().getSelectedItem();
        if (selectedLedger == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a ledger to edit");
            return;
        }
        showLedgerDialog(selectedLedger);
    }

    @FXML
    private void handleDeleteLedger() {
        Ledger selectedLedger = ledgerTableView.getSelectionModel().getSelectedItem();
        if (selectedLedger == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a ledger to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Ledger");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedLedger.getName() + "'?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (LedgerManager.deleteLedger(company.getCompanyName(), selectedLedger.getName())) {
                    loadLedgers();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Ledger deleted successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete ledger");
                }
            }
        });
    }

    private void showLedgerDialog(Ledger ledger) {
        Dialog<Ledger> dialog = new Dialog<>();
        dialog.setTitle(ledger == null ? "Add Ledger" : "Edit Ledger");
        dialog.setHeaderText(ledger == null ? "Enter ledger details" : "Edit ledger details");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Ledger Name");
        
        ComboBox<String> groupComboBox = new ComboBox<>();
        groupComboBox.setEditable(true);
        groupComboBox.getItems().addAll(
            "Current Assets", "Fixed Assets", "Current Liabilities", 
            "Long Term Liabilities", "Capital", "Income", "Expenses", 
            "Direct Income", "Indirect Income", "Direct Expenses", "Indirect Expenses"
        );
        groupComboBox.setPromptText("Select or enter group");
        
        TextField balanceField = new TextField();
        balanceField.setPromptText("Opening Balance");
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Dr", "Cr");
        typeComboBox.setValue("Dr");

        if (ledger != null) {
            nameField.setText(ledger.getName());
            groupComboBox.setValue(ledger.getGroup());
            balanceField.setText(String.valueOf(ledger.getOpeningBalance()));
            typeComboBox.setValue(ledger.getType());
        }

        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label("Name:"), nameField,
                new Label("Group:"), groupComboBox,
                new Label("Opening Balance:"), balanceField,
                new Label("Type:"), typeComboBox));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String group = groupComboBox.getValue() != null ? groupComboBox.getValue().trim() : "";
                    double balance = Double.parseDouble(balanceField.getText().trim());
                    String type = typeComboBox.getValue();

                    if (name.isEmpty() || group.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
                        return null;
                    }

                    Ledger newLedger = new Ledger(name, group, balance, type);
                    
                    if (ledger == null) {
                        // Add new ledger
                        if (LedgerManager.addLedger(company.getCompanyName(), newLedger)) {
                            loadLedgers();
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Ledger added successfully");
                            return newLedger;
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add ledger. Name may already exist.");
                            return null;
                        }
                    } else {
                        // Update existing ledger
                        if (LedgerManager.updateLedger(company.getCompanyName(), ledger.getName(), newLedger)) {
                            loadLedgers();
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Ledger updated successfully");
                            return newLedger;
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update ledger. Name may already exist.");
                            return null;
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid opening balance. Please enter a valid number.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void loadLedgers() {
        List<Ledger> ledgers = LedgerManager.loadLedgers(company.getCompanyName());
        ledgerList.clear();
        ledgerList.addAll(ledgers);
        Logger.log("LEDGER_VIEW", "Loaded " + ledgers.size() + " ledgers");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

