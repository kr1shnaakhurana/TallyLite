package com.tallylite.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BackupManager {
    
    public static String backupCompany(String companyName) {
        try {
            Path companyPath = FileManager.getCompanyPath(companyName);
            if (!Files.exists(companyPath)) {
                Logger.log("BACKUP", "Failed: Company '" + companyName + "' not found");
                return null;
            }
            
            // Create backup filename with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupFileName = companyName + "_" + timestamp + ".zip";
            Path backupPath = Paths.get(FileManager.getBackupsDir(), backupFileName);
            
            // Create ZIP file
            try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new FileOutputStream(backupPath.toFile()))) {
                zipDirectory(companyPath.toFile(), companyName, zos);
            }
            
            Logger.log("BACKUP", "Successfully backed up company: " + companyName + " to " + backupFileName);
            return backupPath.toString();
        } catch (IOException e) {
            Logger.log("BACKUP", "Error backing up company: " + e.getMessage());
            return null;
        }
    }
    
    private static void zipDirectory(File sourceDir, String baseDir, ZipArchiveOutputStream zos) throws IOException {
        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                String entryName = baseDir + File.separator + file.getName();
                
                if (file.isDirectory()) {
                    zipDirectory(file, entryName, zos);
                } else {
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, entryName);
                    zos.putArchiveEntry(entry);
                    
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                    }
                    zos.closeArchiveEntry();
                }
            }
        }
    }
    
    public static boolean restoreCompany(File backupFile) {
        try {
            String backupFileName = backupFile.getName();
            // Extract company name from backup filename (format: CompanyName_YYYYMMDD_HHMMSS.zip)
            String companyName = backupFileName.substring(0, backupFileName.lastIndexOf('_'));
            companyName = companyName.substring(0, companyName.lastIndexOf('_'));
            
            Path companyPath = FileManager.getCompanyPath(companyName);
            
            // Delete existing company if it exists
            if (Files.exists(companyPath)) {
                deleteDirectory(companyPath.toFile());
            }
            
            // Create company directory
            Files.createDirectories(companyPath);
            
            // Extract ZIP file
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupFile))) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    Path filePath = companyPath.resolve(entry.getName());
                    
                    if (entry.isDirectory()) {
                        Files.createDirectories(filePath);
                    } else {
                        Files.createDirectories(filePath.getParent());
                        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }
                    }
                    zis.closeEntry();
                }
            }
            
            Logger.log("RESTORE", "Successfully restored company: " + companyName + " from " + backupFileName);
            return true;
        } catch (IOException e) {
            Logger.log("RESTORE", "Error restoring company: " + e.getMessage());
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
}

