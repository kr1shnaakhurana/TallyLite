package com.tallylite.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tallylite.model.InventoryItem;
import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<InventoryItem> loadInventory(String companyName) {
        try {
            Path inventoryPath = FileManager.getInventoryJsonPath(companyName);
            if (!Files.exists(inventoryPath)) {
                Logger.log("LOAD_INVENTORY", "Inventory file not found for company: " + companyName);
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(inventoryPath.toFile())) {
                List<InventoryItem> items = gson.fromJson(reader, new TypeToken<List<InventoryItem>>(){}.getType());
                if (items == null) {
                    items = new ArrayList<>();
                }
                Logger.log("LOAD_INVENTORY", "Loaded " + items.size() + " inventory items for company: " + companyName);
                return items;
            }
        } catch (IOException e) {
            Logger.log("LOAD_INVENTORY", "Error loading inventory: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean saveInventory(String companyName, List<InventoryItem> items) {
        try {
            Path inventoryPath = FileManager.getInventoryJsonPath(companyName);
            try (FileWriter writer = new FileWriter(inventoryPath.toFile())) {
                gson.toJson(items, writer);
            }
            Logger.log("SAVE_INVENTORY", "Saved " + items.size() + " inventory items for company: " + companyName);
            return true;
        } catch (IOException e) {
            Logger.log("SAVE_INVENTORY", "Error saving inventory: " + e.getMessage());
            return false;
        }
    }

    public static boolean addItem(String companyName, InventoryItem item) {
        List<InventoryItem> items = loadInventory(companyName);
        
        // Check for duplicate name
        for (InventoryItem i : items) {
            if (i.getName().equalsIgnoreCase(item.getName())) {
                Logger.log("ADD_INVENTORY", "Failed: Duplicate item name: " + item.getName());
                return false;
            }
        }
        
        items.add(item);
        return saveInventory(companyName, items);
    }

    public static boolean updateItem(String companyName, String oldName, InventoryItem updatedItem) {
        List<InventoryItem> items = loadInventory(companyName);
        
        for (int i = 0; i < items.size(); i++) {
            InventoryItem item = items.get(i);
            if (item.getName().equals(oldName)) {
                // Check for duplicate name if name changed
                if (!oldName.equalsIgnoreCase(updatedItem.getName())) {
                    for (InventoryItem inv : items) {
                        if (inv != item && inv.getName().equalsIgnoreCase(updatedItem.getName())) {
                            Logger.log("UPDATE_INVENTORY", "Failed: Duplicate item name: " + updatedItem.getName());
                            return false;
                        }
                    }
                }
                items.set(i, updatedItem);
                return saveInventory(companyName, items);
            }
        }
        
        Logger.log("UPDATE_INVENTORY", "Failed: Item not found: " + oldName);
        return false;
    }

    public static boolean deleteItem(String companyName, String itemName) {
        List<InventoryItem> items = loadInventory(companyName);
        
        boolean removed = items.removeIf(i -> i.getName().equals(itemName));
        
        if (removed) {
            saveInventory(companyName, items);
            Logger.log("DELETE_INVENTORY", "Deleted item: " + itemName);
            return true;
        } else {
            Logger.log("DELETE_INVENTORY", "Failed: Item not found: " + itemName);
            return false;
        }
    }

    public static boolean updateStock(String companyName, String itemName, double quantityChange) {
        List<InventoryItem> items = loadInventory(companyName);
        
        for (InventoryItem item : items) {
            if (item.getName().equals(itemName)) {
                double newQuantity = item.getQuantity() + quantityChange;
                if (newQuantity < 0) {
                    Logger.log("UPDATE_STOCK", "Failed: Insufficient stock for " + itemName);
                    return false;
                }
                item.setQuantity(newQuantity);
                Logger.log("UPDATE_STOCK", "Updated stock for " + itemName + " by " + quantityChange);
                return saveInventory(companyName, items);
            }
        }
        
        Logger.log("UPDATE_STOCK", "Failed: Item not found: " + itemName);
        return false;
    }

    public static InventoryItem getItem(String companyName, String itemName) {
        List<InventoryItem> items = loadInventory(companyName);
        for (InventoryItem item : items) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }
}

