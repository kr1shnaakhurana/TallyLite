package com.tallylite.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tallylite.model.Company;
import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CompanyManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean createCompany(String name, String financialYear) {
        try {
            Path companyPath = FileManager.getCompanyPath(name);
            
            // Check if company already exists
            if (Files.exists(companyPath)) {
                Logger.log("CREATE_COMPANY", "Failed: Company '" + name + "' already exists");
                return false;
            }

            // Create company directory
            Files.createDirectories(companyPath);

            // Create company.json
            Company company = new Company(name, financialYear);
            Path companyJsonPath = FileManager.getCompanyJsonPath(name);
            try (FileWriter writer = new FileWriter(companyJsonPath.toFile())) {
                gson.toJson(company, writer);
            }

            // Initialize empty JSON files
            initializeCompanyFiles(name);

            Logger.log("CREATE_COMPANY", "Created company: " + name + " (FY: " + financialYear + ")");
            return true;
        } catch (IOException e) {
            Logger.log("CREATE_COMPANY", "Error: " + e.getMessage());
            return false;
        }
    }

    private static void initializeCompanyFiles(String companyName) throws IOException {
        // Create empty ledgers.json
        Path ledgersPath = FileManager.getLedgersJsonPath(companyName);
        try (FileWriter writer = new FileWriter(ledgersPath.toFile())) {
            writer.write("[]");
        }

        // Create empty vouchers.json
        Path vouchersPath = FileManager.getVouchersJsonPath(companyName);
        try (FileWriter writer = new FileWriter(vouchersPath.toFile())) {
            writer.write("[]");
        }

        // Create empty inventory.json
        Path inventoryPath = FileManager.getInventoryJsonPath(companyName);
        try (FileWriter writer = new FileWriter(inventoryPath.toFile())) {
            writer.write("[]");
        }
    }

    public static Company loadCompany(String name) {
        try {
            Path companyJsonPath = FileManager.getCompanyJsonPath(name);
            if (!Files.exists(companyJsonPath)) {
                Logger.log("LOAD_COMPANY", "Failed: Company '" + name + "' not found");
                return null;
            }

            try (FileReader reader = new FileReader(companyJsonPath.toFile())) {
                Company company = gson.fromJson(reader, Company.class);
                Logger.log("LOAD_COMPANY", "Loaded company: " + name);
                return company;
            }
        } catch (IOException e) {
            Logger.log("LOAD_COMPANY", "Error: " + e.getMessage());
            return null;
        }
    }

    public static boolean deleteCompany(String name) {
        try {
            Path companyPath = FileManager.getCompanyPath(name);
            if (!Files.exists(companyPath)) {
                Logger.log("DELETE_COMPANY", "Failed: Company '" + name + "' not found");
                return false;
            }

            deleteDirectory(companyPath.toFile());
            Logger.log("DELETE_COMPANY", "Deleted company: " + name);
            return true;
        } catch (Exception e) {
            Logger.log("DELETE_COMPANY", "Error: " + e.getMessage());
            return false;
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    public static List<String> listCompanies() {
        List<String> companies = new ArrayList<>();
        try {
            File companiesDir = new File(FileManager.getCompaniesDir());
            if (companiesDir.exists() && companiesDir.isDirectory()) {
                File[] files = companiesDir.listFiles(File::isDirectory);
                if (files != null) {
                    for (File file : files) {
                        companies.add(file.getName());
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("LIST_COMPANIES", "Error: " + e.getMessage());
        }
        return companies;
    }
}

