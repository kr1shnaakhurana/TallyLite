package com.tallylite.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Company {
    private String companyName;
    private String financialYear;
    private String createdOn;

    public Company() {
        this.createdOn = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public Company(String companyName, String financialYear) {
        this.companyName = companyName;
        this.financialYear = financialYear;
        this.createdOn = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}

