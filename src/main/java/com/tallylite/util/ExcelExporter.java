package com.tallylite.util;

import com.tallylite.model.Company;
import com.tallylite.model.ReportRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    
    public static boolean exportReport(File file, String reportType, String reportTitle,
                                      List<ReportRow> reportData, Company company) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(reportType);
            
            // Create styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            
            int rowNum = 0;
            
            // Add title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportTitle);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));
            
            // Add company info
            if (company != null) {
                Row companyRow = sheet.createRow(rowNum++);
                Cell companyCell = companyRow.createCell(0);
                companyCell.setCellValue("Company: " + company.getCompanyName() + 
                                       " | Financial Year: " + company.getFinancialYear());
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 2));
            }
            
            rowNum++; // Empty row
            
            // Add header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Item", "Debit", "Credit"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add data rows
            for (ReportRow row : reportData) {
                Row dataRow = sheet.createRow(rowNum++);
                
                Cell cell1 = dataRow.createCell(0);
                cell1.setCellValue(row.getColumn1() != null ? row.getColumn1() : "");
                
                Cell cell2 = dataRow.createCell(1);
                String col2 = row.getColumn2() != null ? row.getColumn2() : "";
                if (!col2.isEmpty()) {
                    try {
                        cell2.setCellValue(Double.parseDouble(col2));
                    } catch (NumberFormatException e) {
                        cell2.setCellValue(col2);
                    }
                }
                cell2.setCellStyle(numberStyle);
                
                Cell cell3 = dataRow.createCell(2);
                String col3 = row.getColumn3() != null ? row.getColumn3() : "";
                if (!col3.isEmpty()) {
                    try {
                        cell3.setCellValue(Double.parseDouble(col3));
                    } catch (NumberFormatException e) {
                        cell3.setCellValue(col3);
                    }
                }
                cell3.setCellStyle(numberStyle);
                
                // Apply total style to total row
                if ("TOTAL".equals(row.getColumn1())) {
                    cell1.setCellStyle(totalStyle);
                    cell2.setCellStyle(totalStyle);
                    cell3.setCellStyle(totalStyle);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            
            Logger.log("EXPORT_EXCEL", "Successfully exported " + reportType + " to " + file.getName());
            return true;
        } catch (IOException e) {
            Logger.log("EXPORT_EXCEL", "Error exporting Excel: " + e.getMessage());
            return false;
        }
    }
}

