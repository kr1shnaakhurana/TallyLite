package com.tallylite.model;

public class Voucher {
    private String date;
    private String voucherType; // Sales, Purchase, Receipt, Payment, Journal
    private String ledgerDr;
    private String ledgerCr;
    private double amount;
    private String narration;

    public Voucher() {
    }

    public Voucher(String date, String voucherType, String ledgerDr, String ledgerCr, double amount, String narration) {
        this.date = date;
        this.voucherType = voucherType;
        this.ledgerDr = ledgerDr;
        this.ledgerCr = ledgerCr;
        this.amount = amount;
        this.narration = narration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getLedgerDr() {
        return ledgerDr;
    }

    public void setLedgerDr(String ledgerDr) {
        this.ledgerDr = ledgerDr;
    }

    public String getLedgerCr() {
        return ledgerCr;
    }

    public void setLedgerCr(String ledgerCr) {
        this.ledgerCr = ledgerCr;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}

