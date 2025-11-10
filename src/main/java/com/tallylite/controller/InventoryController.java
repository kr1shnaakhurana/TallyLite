package com.tallylite.controller;

import com.tallylite.manager.InventoryManager;
import com.tallylite.model.Company;
import com.tallylite.model.InventoryItem;
import com.tallylite.util.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.util.List;

public class InventoryController implements ModuleController {
    @FXML
    private TableView<InventoryItem> inventoryTableView;
    @FXML
    private TableColumn<InventoryItem, String> colItemName;
    @FXML
    private TableColumn<InventoryItem, String> colItemGroup;
    @FXML
    private TableColumn<InventoryItem, Double> colQuantity;
    @FXML
    private TableColumn<InventoryItem, Double> colRate;
    @FXML
    private TableColumn<InventoryItem, Double> colValue;

    private Company company;
    private ObservableList<InventoryItem> inventoryList;

    @Override
    public void setCompany(Company company) {
        this.company = company;
        loadInventory();
    }

    @FXML
    private void initialize() {
        // Initialize table columns
        colItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colItemGroup.setCellValueFactory(new PropertyValueFactory<>("group"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        
        // Format numeric columns
        colQuantity.setCellFactory(column -> new TableCell<InventoryItem, Double>() {
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
        
        colRate.setCellFactory(column -> new TableCell<InventoryItem, Double>() {
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
        
        colValue.setCellFactory(column -> new TableCell<InventoryItem, Double>() {
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

        inventoryList = FXCollections.observableArrayList();
        inventoryTableView.setItems(inventoryList);
    }

    @FXML
    private void handleAddItem() {
        showItemDialog(null);
    }

    @FXML
    private void handleEditItem() {
        InventoryItem selectedItem = inventoryTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to edit");
            return;
        }
        showItemDialog(selectedItem);
    }

    @FXML
    private void handleDeleteItem() {
        InventoryItem selectedItem = inventoryTableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to delete");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Item");
        confirmAlert.setContentText("Are you sure you want to delete '" + selectedItem.getName() + "'?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (InventoryManager.deleteItem(company.getCompanyName(), selectedItem.getName())) {
                    loadInventory();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete item");
                }
            }
        });
    }

    private void showItemDialog(InventoryItem item) {
        Dialog<InventoryItem> dialog = new Dialog<>();
        dialog.setTitle(item == null ? "Add Item" : "Edit Item");
        dialog.setHeaderText(item == null ? "Enter item details" : "Edit item details");
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Item Name");
        
        ComboBox<String> groupComboBox = new ComboBox<>();
        groupComboBox.setEditable(true);
        groupComboBox.getItems().addAll(
            "Grains", "Vegetables", "Fruits", "Electronics", 
            "Clothing", "Furniture", "Stationery", "Other"
        );
        groupComboBox.setPromptText("Select or enter group");
        
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        
        TextField rateField = new TextField();
        rateField.setPromptText("Rate per unit");

        if (item != null) {
            nameField.setText(item.getName());
            groupComboBox.setValue(item.getGroup());
            quantityField.setText(String.valueOf(item.getQuantity()));
            rateField.setText(String.valueOf(item.getRate()));
        }

        dialog.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label("Name:"), nameField,
                new Label("Group:"), groupComboBox,
                new Label("Quantity:"), quantityField,
                new Label("Rate:"), rateField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String group = groupComboBox.getValue() != null ? groupComboBox.getValue().trim() : "";
                    double quantity = Double.parseDouble(quantityField.getText().trim());
                    double rate = Double.parseDouble(rateField.getText().trim());

                    if (name.isEmpty() || group.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields");
                        return null;
                    }

                    if (quantity < 0 || rate < 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Quantity and rate must be non-negative");
                        return null;
                    }

                    InventoryItem newItem = new InventoryItem(name, group, quantity, rate);
                    
                    if (item == null) {
                        // Add new item
                        if (InventoryManager.addItem(company.getCompanyName(), newItem)) {
                            loadInventory();
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully");
                            return newItem;
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add item. Name may already exist.");
                            return null;
                        }
                    } else {
                        // Update existing item
                        if (InventoryManager.updateItem(company.getCompanyName(), item.getName(), newItem)) {
                            loadInventory();
                            showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully");
                            return newItem;
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update item. Name may already exist.");
                            return null;
                        }
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid number. Please enter valid numbers for quantity and rate.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void loadInventory() {
        List<InventoryItem> items = InventoryManager.loadInventory(company.getCompanyName());
        inventoryList.clear();
        inventoryList.addAll(items);
        Logger.log("INVENTORY_VIEW", "Loaded " + items.size() + " inventory items");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

