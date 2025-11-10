package com.tallylite.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.tallylite.model.Company;
import com.tallylite.model.ReportRow;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFExporter {
    
    public static boolean exportReport(File file, String reportType, String reportTitle, 
                                      List<ReportRow> reportData, Company company) {
        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add title
            Paragraph title = new Paragraph(reportTitle)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);
            
            // Add company info
            if (company != null) {
                Paragraph companyInfo = new Paragraph(
                    "Company: " + company.getCompanyName() + "\n" +
                    "Financial Year: " + company.getFinancialYear()
                )
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
                document.add(companyInfo);
            }
            
            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);
            
            // Add header row
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Item"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold());
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Debit"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Credit"))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT));
            
            // Add data rows
            for (ReportRow row : reportData) {
                table.addCell(new Paragraph(row.getColumn1() != null ? row.getColumn1() : ""));
                
                com.itextpdf.layout.element.Cell debitCell = new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(row.getColumn2() != null ? row.getColumn2() : ""))
                        .setTextAlignment(TextAlignment.RIGHT);
                if ("TOTAL".equals(row.getColumn1())) {
                    debitCell.setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY);
                }
                table.addCell(debitCell);
                
                com.itextpdf.layout.element.Cell creditCell = new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(row.getColumn3() != null ? row.getColumn3() : ""))
                        .setTextAlignment(TextAlignment.RIGHT);
                if ("TOTAL".equals(row.getColumn1())) {
                    creditCell.setBold().setBackgroundColor(ColorConstants.LIGHT_GRAY);
                }
                table.addCell(creditCell);
            }
            
            document.add(table);
            
            // Add footer
            Paragraph footer = new Paragraph(
                "Generated on: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                )
            )
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20);
            document.add(footer);
            
            document.close();
            
            Logger.log("EXPORT_PDF", "Successfully exported " + reportType + " to " + file.getName());
            return true;
        } catch (IOException e) {
            Logger.log("EXPORT_PDF", "Error exporting PDF: " + e.getMessage());
            return false;
        }
    }
}

