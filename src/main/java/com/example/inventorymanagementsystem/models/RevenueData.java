package com.example.inventorymanagementsystem.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RevenueData {
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty unitsSold;
    private final SimpleDoubleProperty revenue;

    public RevenueData(String name, int unitsSold, double revenue) {
        this.name = new SimpleStringProperty(name);
        this.unitsSold = new SimpleIntegerProperty(unitsSold);
        this.revenue = new SimpleDoubleProperty(revenue);
    }

    public String getName() {
        return name.get();
    }

    public int getUnitsSold() {
        return unitsSold.get();
    }

    public double getRevenue() {
        return revenue.get();
    }
}
