package com.tallylite.util;

import com.tallylite.manager.InventoryManager;
import com.tallylite.manager.LedgerManager;
import com.tallylite.manager.ReportsManager;
import com.tallylite.manager.VoucherManager;
import com.tallylite.model.InventoryItem;
import com.tallylite.model.Ledger;
import com.tallylite.model.Voucher;

import java.util.List;

public class DashboardStats {
    
    public static class Stats {
        private int ledgerCount;
        private int voucherCount;
        private int inventoryCount;
        private double totalInventoryValue;
        private double totalDebit;
        private double totalCredit;
        
        public Stats(int ledgerCount, int voucherCount, int inventoryCount, 
                    double totalInventoryValue, double totalDebit, double totalCredit) {
            this.ledgerCount = ledgerCount;
            this.voucherCount = voucherCount;
            this.inventoryCount = inventoryCount;
            this.totalInventoryValue = totalInventoryValue;
            this.totalDebit = totalDebit;
            this.totalCredit = totalCredit;
        }
        
        public int getLedgerCount() { return ledgerCount; }
        public int getVoucherCount() { return voucherCount; }
        public int getInventoryCount() { return inventoryCount; }
        public double getTotalInventoryValue() { return totalInventoryValue; }
        public double getTotalDebit() { return totalDebit; }
        public double getTotalCredit() { return totalCredit; }
    }
    
    public static Stats getStats(String companyName) {
        List<Ledger> ledgers = LedgerManager.loadLedgers(companyName);
        List<Voucher> vouchers = VoucherManager.loadVouchers(companyName);
        List<InventoryItem> inventory = InventoryManager.loadInventory(companyName);
        
        // Calculate inventory value
        double totalInventoryValue = inventory.stream()
                .mapToDouble(InventoryItem::getValue)
                .sum();
        
        // Calculate trial balance totals
        List<ReportsManager.TrialBalanceEntry> trialBalance = 
                ReportsManager.calculateTrialBalance(companyName);
        double totalDebit = trialBalance.stream()
                .mapToDouble(ReportsManager.TrialBalanceEntry::getDebit)
                .sum();
        double totalCredit = trialBalance.stream()
                .mapToDouble(ReportsManager.TrialBalanceEntry::getCredit)
                .sum();
        
        return new Stats(
            ledgers.size(),
            vouchers.size(),
            inventory.size(),
            totalInventoryValue,
            totalDebit,
            totalCredit
        );
    }
}

