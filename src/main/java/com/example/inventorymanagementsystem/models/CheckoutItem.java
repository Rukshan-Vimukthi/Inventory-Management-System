package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.*;

/**
 * Represents a single item entry in the checkout table.
 */
public class CheckoutItem implements DataModel {
    private ItemDetail itemDetail;
    private final StringProperty name;
    private final StringProperty itemSize;
    private final StringProperty itemColor;
    private final IntegerProperty amount;
    private final DoubleProperty price;
    private final DoubleProperty sellingPrice;
    private final StringProperty itemTotalCost;
    private int itemHasSizeId;
    private DoubleProperty discount;
    private boolean isCheckedout = false;



    public CheckoutItem(String name, String itemSize, String itemColor,
                        int amount, double price, double sellingPrice, String itemTotalCost) {
        this(name, itemSize, itemColor, amount, price, sellingPrice, 0.0, itemTotalCost); // Default discount = 0.0%
    }

    public CheckoutItem(String name, String itemSize, String itemColor,
                        int amount, double price, double sellingPrice, double discountValue, String itemTotalCost) {
        this.itemDetail = itemDetail;
        this.name = new SimpleStringProperty(name);
        this.itemSize = new SimpleStringProperty(itemSize);
        this.itemColor = new SimpleStringProperty(itemColor);
        this.amount = new SimpleIntegerProperty(amount);
        this.price = new SimpleDoubleProperty(price);
        this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
        this.itemTotalCost = new SimpleStringProperty(itemTotalCost);
        this.discount = new SimpleDoubleProperty(discountValue);
        this.itemHasSizeId = itemHasSizeId;
    }

    public void function(){
        System.out.println(this.isCheckedout);
    }

    // Getters for TableView
    public ItemDetail getItemDetail() {
        return itemDetail;
    }

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

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
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

    public void setItemHasSizeId(int itemHasSizeId) {
        this.itemHasSizeId = itemHasSizeId;
    }

    public int getitemHasSizeId() {
        return itemHasSizeId;
    }

    public double getDiscount() {
        return discount.get();
    }

    public void setDiscount(double discountValue) {
        discount.set(discountValue);
    }

    public DoubleProperty discountProperty() {
        return discount;
    }
}
