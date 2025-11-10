package com.tallylite.model;

public class InventoryItem {
    private String name;
    private String group;
    private double quantity;
    private double rate;
    private double value;

    public InventoryItem() {
    }

    public InventoryItem(String name, String group, double quantity, double rate) {
        this.name = name;
        this.group = group;
        this.quantity = quantity;
        this.rate = rate;
        this.value = quantity * rate;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.value = this.quantity * this.rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        this.value = this.quantity * this.rate;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}

