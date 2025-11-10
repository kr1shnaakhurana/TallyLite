package com.tallylite.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String action, String details) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FileManager.getLogsFile(), true))) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            writer.println(String.format("[%s] %s: %s", timestamp, action, details));
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public static void log(String message) {
        log("INFO", message);
    }
}

