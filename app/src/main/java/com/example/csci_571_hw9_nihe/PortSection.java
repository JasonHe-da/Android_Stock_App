package com.example.csci_571_hw9_nihe;

import org.json.JSONArray;

import java.util.List;

public class PortSection {
    private String sectionName;
    private JSONArray portItems;
    private double cash;
    private double netWorth;

    // constructor
    public PortSection(String sectionName, JSONArray portItems, double cash, double netWorth) {
        this.sectionName = sectionName;
        this.portItems = portItems;
        this.cash = cash;
        this.netWorth = netWorth;
    }


    @Override
    public String toString() {
        return "PortSection{" +
                "sectionName='" + sectionName + '\'' +
                ", portItems=" + portItems +
                ", cash=" + cash +
                ", netWorth=" + netWorth +
                '}';
    }

    // getter and setter
    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public JSONArray getPortItems() {
        return portItems;
    }

    public void setPortItems(JSONArray portItems) {
        this.portItems = portItems;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getNetWorth() {
        return netWorth;
    }

    public void setNetWorth(double netWorth) {
        this.netWorth = netWorth;
    }
}
