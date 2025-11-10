package com.tallylite.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    private static final String TALLY_HOME = System.getProperty("user.home") + File.separator + "TallyLocal";
    private static final String COMPANIES_DIR = TALLY_HOME + File.separator + "Companies";
    private static final String BACKUPS_DIR = TALLY_HOME + File.separator + "Backups";
    private static final String SETTINGS_FILE = TALLY_HOME + File.separator + "settings.json";
    private static final String LOGS_FILE = TALLY_HOME + File.separator + "logs.txt";

    static {
        initializeDirectories();
    }

    private static void initializeDirectories() {
        try {
            Files.createDirectories(Paths.get(COMPANIES_DIR));
            Files.createDirectories(Paths.get(BACKUPS_DIR));
            
            // Create settings file if it doesn't exist
            if (!Files.exists(Paths.get(SETTINGS_FILE))) {
                Files.createFile(Paths.get(SETTINGS_FILE));
            }
            
            // Create logs file if it doesn't exist
            if (!Files.exists(Paths.get(LOGS_FILE))) {
                Files.createFile(Paths.get(LOGS_FILE));
            }
        } catch (IOException e) {
            System.err.println("Error initializing directories: " + e.getMessage());
        }
    }

    public static String getTallyHome() {
        return TALLY_HOME;
    }

    public static String getCompaniesDir() {
        return COMPANIES_DIR;
    }

    public static String getBackupsDir() {
        return BACKUPS_DIR;
    }

    public static String getSettingsFile() {
        return SETTINGS_FILE;
    }

    public static String getLogsFile() {
        return LOGS_FILE;
    }

    public static Path getCompanyPath(String companyName) {
        return Paths.get(COMPANIES_DIR, companyName);
    }

    public static Path getCompanyJsonPath(String companyName) {
        return Paths.get(COMPANIES_DIR, companyName, "company.json");
    }

    public static Path getLedgersJsonPath(String companyName) {
        return Paths.get(COMPANIES_DIR, companyName, "ledgers.json");
    }

    public static Path getVouchersJsonPath(String companyName) {
        return Paths.get(COMPANIES_DIR, companyName, "vouchers.json");
    }

    public static Path getInventoryJsonPath(String companyName) {
        return Paths.get(COMPANIES_DIR, companyName, "inventory.json");
    }
}

