package com.tallylite.model;

public class Settings {
    private String theme; // dark or light
    private String recentCompany;

    public Settings() {
        this.theme = "dark";
        this.recentCompany = "";
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getRecentCompany() {
        return recentCompany;
    }

    public void setRecentCompany(String recentCompany) {
        this.recentCompany = recentCompany;
    }
}

