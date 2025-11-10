package com.tallylite.model;

public class Ledger {
    private String name;
    private String group;
    private double openingBalance;
    private String type; // Dr or Cr

    public Ledger() {
    }

    public Ledger(String name, String group, double openingBalance, String type) {
        this.name = name;
        this.group = group;
        this.openingBalance = openingBalance;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

