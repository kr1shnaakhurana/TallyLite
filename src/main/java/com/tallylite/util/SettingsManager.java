package com.tallylite.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tallylite.model.Settings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SettingsManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Settings loadSettings() {
        try {
            if (Files.exists(Paths.get(FileManager.getSettingsFile()))) {
                String content = new String(Files.readAllBytes(Paths.get(FileManager.getSettingsFile())));
                if (content.trim().isEmpty()) {
                    Settings defaultSettings = new Settings();
                    saveSettings(defaultSettings);
                    return defaultSettings;
                }
                return gson.fromJson(content, Settings.class);
            }
        } catch (IOException e) {
            System.err.println("Error loading settings: " + e.getMessage());
        }
        Settings defaultSettings = new Settings();
        saveSettings(defaultSettings);
        return defaultSettings;
    }

    public static void saveSettings(Settings settings) {
        try (FileWriter writer = new FileWriter(FileManager.getSettingsFile())) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}

