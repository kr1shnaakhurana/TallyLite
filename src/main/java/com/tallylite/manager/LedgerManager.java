package com.tallylite.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tallylite.model.Ledger;
import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LedgerManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<Ledger> loadLedgers(String companyName) {
        try {
            Path ledgersPath = FileManager.getLedgersJsonPath(companyName);
            if (!Files.exists(ledgersPath)) {
                Logger.log("LOAD_LEDGERS", "Ledgers file not found for company: " + companyName);
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(ledgersPath.toFile())) {
                List<Ledger> ledgers = gson.fromJson(reader, new TypeToken<List<Ledger>>(){}.getType());
                if (ledgers == null) {
                    ledgers = new ArrayList<>();
                }
                Logger.log("LOAD_LEDGERS", "Loaded " + ledgers.size() + " ledgers for company: " + companyName);
                return ledgers;
            }
        } catch (IOException e) {
            Logger.log("LOAD_LEDGERS", "Error loading ledgers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean saveLedgers(String companyName, List<Ledger> ledgers) {
        try {
            Path ledgersPath = FileManager.getLedgersJsonPath(companyName);
            try (FileWriter writer = new FileWriter(ledgersPath.toFile())) {
                gson.toJson(ledgers, writer);
            }
            Logger.log("SAVE_LEDGERS", "Saved " + ledgers.size() + " ledgers for company: " + companyName);
            return true;
        } catch (IOException e) {
            Logger.log("SAVE_LEDGERS", "Error saving ledgers: " + e.getMessage());
            return false;
        }
    }

    public static boolean addLedger(String companyName, Ledger ledger) {
        List<Ledger> ledgers = loadLedgers(companyName);
        
        // Check for duplicate name
        for (Ledger l : ledgers) {
            if (l.getName().equalsIgnoreCase(ledger.getName())) {
                Logger.log("ADD_LEDGER", "Failed: Duplicate ledger name: " + ledger.getName());
                return false;
            }
        }
        
        ledgers.add(ledger);
        return saveLedgers(companyName, ledgers);
    }

    public static boolean updateLedger(String companyName, String oldName, Ledger updatedLedger) {
        List<Ledger> ledgers = loadLedgers(companyName);
        
        for (int i = 0; i < ledgers.size(); i++) {
            Ledger ledger = ledgers.get(i);
            if (ledger.getName().equals(oldName)) {
                // Check for duplicate name if name changed
                if (!oldName.equalsIgnoreCase(updatedLedger.getName())) {
                    for (Ledger l : ledgers) {
                        if (l != ledger && l.getName().equalsIgnoreCase(updatedLedger.getName())) {
                            Logger.log("UPDATE_LEDGER", "Failed: Duplicate ledger name: " + updatedLedger.getName());
                            return false;
                        }
                    }
                }
                ledgers.set(i, updatedLedger);
                return saveLedgers(companyName, ledgers);
            }
        }
        
        Logger.log("UPDATE_LEDGER", "Failed: Ledger not found: " + oldName);
        return false;
    }

    public static boolean deleteLedger(String companyName, String ledgerName) {
        List<Ledger> ledgers = loadLedgers(companyName);
        
        boolean removed = ledgers.removeIf(l -> l.getName().equals(ledgerName));
        
        if (removed) {
            saveLedgers(companyName, ledgers);
            Logger.log("DELETE_LEDGER", "Deleted ledger: " + ledgerName);
            return true;
        } else {
            Logger.log("DELETE_LEDGER", "Failed: Ledger not found: " + ledgerName);
            return false;
        }
    }

    public static List<String> getLedgerNames(String companyName) {
        List<Ledger> ledgers = loadLedgers(companyName);
        List<String> names = new ArrayList<>();
        for (Ledger ledger : ledgers) {
            names.add(ledger.getName());
        }
        return names;
    }
}

