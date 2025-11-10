package com.tallylite.manager;

import com.tallylite.model.Ledger;
import com.tallylite.model.Voucher;

import java.util.*;

public class ReportsManager {
    
    public static class TrialBalanceEntry {
        private String ledgerName;
        private double debit;
        private double credit;
        
        public TrialBalanceEntry(String ledgerName, double debit, double credit) {
            this.ledgerName = ledgerName;
            this.debit = debit;
            this.credit = credit;
        }
        
        public String getLedgerName() { return ledgerName; }
        public double getDebit() { return debit; }
        public double getCredit() { return credit; }
    }
    
    public static List<TrialBalanceEntry> calculateTrialBalance(String companyName) {
        List<Ledger> ledgers = LedgerManager.loadLedgers(companyName);
        List<Voucher> vouchers = VoucherManager.loadVouchers(companyName);
        
        Map<String, Double> debitBalances = new HashMap<>();
        Map<String, Double> creditBalances = new HashMap<>();
        
        // Initialize with opening balances
        for (Ledger ledger : ledgers) {
            if ("Dr".equals(ledger.getType())) {
                debitBalances.put(ledger.getName(), ledger.getOpeningBalance());
                creditBalances.put(ledger.getName(), 0.0);
            } else {
                creditBalances.put(ledger.getName(), ledger.getOpeningBalance());
                debitBalances.put(ledger.getName(), 0.0);
            }
        }
        
        // Process vouchers
        for (Voucher voucher : vouchers) {
            String drLedger = voucher.getLedgerDr();
            String crLedger = voucher.getLedgerCr();
            double amount = voucher.getAmount();
            
            // Debit side
            debitBalances.put(drLedger, debitBalances.getOrDefault(drLedger, 0.0) + amount);
            
            // Credit side
            creditBalances.put(crLedger, creditBalances.getOrDefault(crLedger, 0.0) + amount);
        }
        
        // Create trial balance entries
        List<TrialBalanceEntry> entries = new ArrayList<>();
        Set<String> allLedgers = new HashSet<>();
        allLedgers.addAll(debitBalances.keySet());
        allLedgers.addAll(creditBalances.keySet());
        
        for (String ledgerName : allLedgers) {
            double debit = debitBalances.getOrDefault(ledgerName, 0.0);
            double credit = creditBalances.getOrDefault(ledgerName, 0.0);
            
            // Only include if there's a balance
            if (debit != 0 || credit != 0) {
                entries.add(new TrialBalanceEntry(ledgerName, debit, credit));
            }
        }
        
        // Sort by ledger name
        entries.sort(Comparator.comparing(TrialBalanceEntry::getLedgerName));
        
        return entries;
    }
    
    public static class ProfitLossEntry {
        private String item;
        private double amount;
        
        public ProfitLossEntry(String item, double amount) {
            this.item = item;
            this.amount = amount;
        }
        
        public String getItem() { return item; }
        public double getAmount() { return amount; }
    }
    
    public static List<ProfitLossEntry> calculateProfitLoss(String companyName) {
        List<TrialBalanceEntry> trialBalance = calculateTrialBalance(companyName);
        List<ProfitLossEntry> entries = new ArrayList<>();
        
        double totalIncome = 0;
        double totalExpenses = 0;
        
        // Categorize based on ledger groups
        for (TrialBalanceEntry entry : trialBalance) {
            Ledger ledger = getLedgerByName(companyName, entry.getLedgerName());
            if (ledger != null) {
                String group = ledger.getGroup().toLowerCase();
                double balance = entry.getDebit() - entry.getCredit();
                
                // Income groups
                if (group.contains("income") || group.contains("revenue") || group.contains("sales")) {
                    if (balance > 0) {
                        totalIncome += balance;
                        entries.add(new ProfitLossEntry(entry.getLedgerName(), balance));
                    }
                }
                // Expense groups
                else if (group.contains("expense") || group.contains("purchase") || group.contains("cost")) {
                    if (balance > 0) {
                        totalExpenses += balance;
                        entries.add(new ProfitLossEntry(entry.getLedgerName(), balance));
                    }
                }
            }
        }
        
        // Add totals
        entries.add(new ProfitLossEntry("Total Income", totalIncome));
        entries.add(new ProfitLossEntry("Total Expenses", totalExpenses));
        entries.add(new ProfitLossEntry("Net Profit/Loss", totalIncome - totalExpenses));
        
        return entries;
    }
    
    public static class BalanceSheetEntry {
        private String item;
        private double amount;
        
        public BalanceSheetEntry(String item, double amount) {
            this.item = item;
            this.amount = amount;
        }
        
        public String getItem() { return item; }
        public double getAmount() { return amount; }
    }
    
    public static List<BalanceSheetEntry> calculateBalanceSheet(String companyName) {
        List<TrialBalanceEntry> trialBalance = calculateTrialBalance(companyName);
        List<BalanceSheetEntry> entries = new ArrayList<>();
        
        double totalAssets = 0;
        double totalLiabilities = 0;
        double totalCapital = 0;
        
        // Categorize based on ledger groups
        for (TrialBalanceEntry entry : trialBalance) {
            Ledger ledger = getLedgerByName(companyName, entry.getLedgerName());
            if (ledger != null) {
                String group = ledger.getGroup().toLowerCase();
                double balance = entry.getDebit() - entry.getCredit();
                
                // Assets
                if (group.contains("asset")) {
                    if (balance > 0) {
                        totalAssets += balance;
                        entries.add(new BalanceSheetEntry(entry.getLedgerName(), balance));
                    }
                }
                // Liabilities
                else if (group.contains("liability")) {
                    if (balance > 0) {
                        totalLiabilities += balance;
                        entries.add(new BalanceSheetEntry(entry.getLedgerName(), balance));
                    }
                }
                // Capital
                else if (group.contains("capital")) {
                    if (balance > 0) {
                        totalCapital += balance;
                        entries.add(new BalanceSheetEntry(entry.getLedgerName(), balance));
                    }
                }
            }
        }
        
        // Add totals
        entries.add(new BalanceSheetEntry("Total Assets", totalAssets));
        entries.add(new BalanceSheetEntry("Total Liabilities", totalLiabilities));
        entries.add(new BalanceSheetEntry("Total Capital", totalCapital));
        entries.add(new BalanceSheetEntry("Total (Liabilities + Capital)", totalLiabilities + totalCapital));
        
        return entries;
    }
    
    private static Ledger getLedgerByName(String companyName, String ledgerName) {
        List<Ledger> ledgers = LedgerManager.loadLedgers(companyName);
        for (Ledger ledger : ledgers) {
            if (ledger.getName().equals(ledgerName)) {
                return ledger;
            }
        }
        return null;
    }
}

