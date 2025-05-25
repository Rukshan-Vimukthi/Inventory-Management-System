package com.example.inventorymanagementsystem.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ItemHasSize {
    IntegerProperty id;
    IntegerProperty itemID;
    IntegerProperty stockID;
    IntegerProperty itemSizeID;
    IntegerProperty orderQuantity;
    DoubleProperty cost;
    DoubleProperty price;
    IntegerProperty remainingQuantity;

    public ItemHasSize(int id, int itemID, int stockID, int itemSizeID, int orderQuantity, double cost, double price, int remainingQuantity) {
        idProperty().setValue(id);
        itemIDProperty().setValue(itemID);
        stockIDProperty().setValue(stockID);
        itemSizeIDProperty().setValue(itemSizeID);
        orderQuantityProperty().setValue(orderQuantity);
        costProperty().setValue(cost);
        priceProperty().setValue(price);
        remainingQtyProperty().setValue(remainingQuantity);
    }

    public IntegerProperty idProperty(){
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }
    public IntegerProperty itemIDProperty(){
        if (itemID == null){
            itemID = new SimpleIntegerProperty(this, "itemID");
        }
        return itemID;
    }
    public IntegerProperty stockIDProperty(){
        if (stockID == null){
            stockID = new SimpleIntegerProperty(this, "stockID");
        }
        return stockID;
    }
    public IntegerProperty itemSizeIDProperty(){
        if (itemSizeID == null){
            itemSizeID = new SimpleIntegerProperty(this, "itemSizeID");
        }
        return itemSizeID;
    }
    public IntegerProperty orderQuantityProperty(){
        if (orderQuantity == null){
            orderQuantity = new SimpleIntegerProperty(this, "orderQuantity");
        }
        return orderQuantity;
    }
    public DoubleProperty costProperty(){
        if (cost == null){
            cost = new SimpleDoubleProperty(this, "cost");
        }
        return cost;
    }
    public DoubleProperty priceProperty(){
        if (price == null){
            price = new SimpleDoubleProperty(this, "price");
        }
        return price;
    }

    public IntegerProperty remainingQtyProperty(){
        if (remainingQuantity == null){
            remainingQuantity = new SimpleIntegerProperty(this, "remainingQuantity");
        }
        return remainingQuantity;
    }

    public int getId() {
        return id.get();
    }

    public int getItemID() {
        return itemID.get();
    }

    public int getStockID() {
        return stockID.get();
    }

    public int getItemSizeID() {
        return itemSizeID.get();
    }

    public int getOrderQuantity() {
        return orderQuantity.get();
    }

    public double getCost() {
        return cost.get();
    }

    public double getPrice() {
        return price.get();
    }

    public int getRemainingQuantity() {
        return remainingQuantity.get();
    }
}
