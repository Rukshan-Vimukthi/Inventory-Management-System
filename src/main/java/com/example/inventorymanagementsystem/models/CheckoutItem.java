package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.*;

/**
 * Represents a single item entry in the checkout table.
 */
public class CheckoutItem implements DataModel {
    private final StringProperty name;
    private final StringProperty itemSize;
    private final StringProperty itemColor;
    private final IntegerProperty amount;
    private final IntegerProperty price;
    private final DoubleProperty sellingPrice;
    private final StringProperty itemTotalCost;

    public CheckoutItem(String name, String itemSize, String itemColor,
                        int amount, int price, double sellingPrice, String itemTotalCost) {
        this.name = new SimpleStringProperty(name);
        this.itemSize = new SimpleStringProperty(itemSize);
        this.itemColor = new SimpleStringProperty(itemColor);
        this.amount = new SimpleIntegerProperty(amount);
        this.price = new SimpleIntegerProperty(price);
        this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
        this.itemTotalCost = new SimpleStringProperty(itemTotalCost);
    }

    // Getters for TableView
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getItemSize() {
        return itemSize.get();
    }

    public StringProperty itemSizeProperty() {
        return itemSize;
    }

    public String getItemColor() {
        return itemColor.get();
    }

    public StringProperty itemColorProperty() {
        return itemColor;
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public int getPrice() {
        return price.get();
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public double getSellingPrice() {
        return sellingPrice.get();
    }

    public DoubleProperty sellingPriceProperty() {
        return sellingPrice;
    }
    public String getItemTotalCost() {
        return itemTotalCost.get();
    }

    public void setItemTotalCost(String itemTotalCost) {
        this.itemTotalCost.set(itemTotalCost);
    }

    public StringProperty itemTotalCostProperty() {
        return itemTotalCost;
    }


    @Override
    public String getValue() {
        return nameProperty().get();
    }

}
