package com.tallylite.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tallylite.model.Voucher;
import com.tallylite.util.FileManager;
import com.tallylite.util.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoucherManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<Voucher> loadVouchers(String companyName) {
        try {
            Path vouchersPath = FileManager.getVouchersJsonPath(companyName);
            if (!Files.exists(vouchersPath)) {
                Logger.log("LOAD_VOUCHERS", "Vouchers file not found for company: " + companyName);
                return new ArrayList<>();
            }

            try (FileReader reader = new FileReader(vouchersPath.toFile())) {
                List<Voucher> vouchers = gson.fromJson(reader, new TypeToken<List<Voucher>>(){}.getType());
                if (vouchers == null) {
                    vouchers = new ArrayList<>();
                }
                Logger.log("LOAD_VOUCHERS", "Loaded " + vouchers.size() + " vouchers for company: " + companyName);
                return vouchers;
            }
        } catch (IOException e) {
            Logger.log("LOAD_VOUCHERS", "Error loading vouchers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static boolean saveVouchers(String companyName, List<Voucher> vouchers) {
        try {
            Path vouchersPath = FileManager.getVouchersJsonPath(companyName);
            try (FileWriter writer = new FileWriter(vouchersPath.toFile())) {
                gson.toJson(vouchers, writer);
            }
            Logger.log("SAVE_VOUCHERS", "Saved " + vouchers.size() + " vouchers for company: " + companyName);
            return true;
        } catch (IOException e) {
            Logger.log("SAVE_VOUCHERS", "Error saving vouchers: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveVoucher(String companyName, Voucher voucher) {
        List<Voucher> vouchers = loadVouchers(companyName);
        vouchers.add(voucher);
        return saveVouchers(companyName, vouchers);
    }

    public static List<Voucher> getVouchersByType(String companyName, String voucherType) {
        List<Voucher> allVouchers = loadVouchers(companyName);
        return allVouchers.stream()
                .filter(v -> v.getVoucherType().equals(voucherType))
                .collect(Collectors.toList());
    }
}

