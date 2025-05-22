package com.example.inventorymanagementsystem.models;

import javafx.beans.property.*;

/**
 * This class is used to get all the information related to a single item in the item table.
 * Recommended for using when showing all the item data and loading items in a checkout page.
 */
public class ItemDetail {
    private IntegerProperty id;
    private StringProperty name;
    private DoubleProperty price;
    private DoubleProperty sellingPrice;

    private IntegerProperty stockID;
    private StringProperty stockDate;
    private StringProperty stockName;

    private IntegerProperty itemSizeID;
    private StringProperty itemSize;

    private IntegerProperty itemColorID;
    private StringProperty itemColor;

    public ItemDetail(int id, String name, Double price,
                      Double sellingPrice, int stockID,
                      String stockDate,
                      String stockName,
                      int itemSizeID,
                      String itemSize,
                      int itemColorID,
                      String itemColor){
        idProperty().setValue(id);
        nameProperty().setValue(name);
        priceProperty().setValue(price);
        sellingPriceProperty().setValue(sellingPrice);
        stockIDProperty().setValue(stockID);
        stockDateProperty().setValue(stockDate);
        stockNameProperty().setValue(stockName);
        itemSizeIDProperty().setValue(itemSizeID);
        itemSizeProperty().setValue(itemSize);
        itemColorIDProperty().setValue(itemColorID);
        itemColorProperty().setValue(itemColor);
    }

    public IntegerProperty idProperty(){
        if (id == null) {
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }
    public StringProperty nameProperty(){
        if (name == null) {
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }
    public DoubleProperty priceProperty(){
        if (price == null) {
            price = new SimpleDoubleProperty(this, "price");
        }
        return price;
    }
    public DoubleProperty sellingPriceProperty(){
        if (sellingPrice == null) {
            sellingPrice = new SimpleDoubleProperty(this, "sellingPrice");
        }
        return sellingPrice;
    }
    public IntegerProperty stockIDProperty(){
        if (stockID == null) {
            stockID = new SimpleIntegerProperty(this, "stockID");
        }
        return stockID;
    }
    public StringProperty stockDateProperty(){
        if (stockDate == null) {
            stockDate = new SimpleStringProperty(this, "stockDate");
        }
        return stockDate;
    }
    public StringProperty stockNameProperty(){
        if (stockName == null) {
            stockName = new SimpleStringProperty(this, "stockName");
        }
        return stockName;
    }
    public IntegerProperty itemSizeIDProperty(){
        if (itemSizeID == null) {
            itemSizeID = new SimpleIntegerProperty(this, "itemSizeID");
        }
        return itemSizeID;
    }
    public StringProperty itemSizeProperty(){
        if (itemSize == null) {
            itemSize = new SimpleStringProperty(this, "itemSize");
        }
        return itemSize;
    }
    public IntegerProperty itemColorIDProperty(){
        if (itemColorID == null) {
            itemColorID = new SimpleIntegerProperty(this, "itemColorID");
        }
        return itemColorID;
    }
    public StringProperty itemColorProperty(){
        if (itemColor == null) {
            itemColor = new SimpleStringProperty(this, "itemColor");
        }
        return itemColor;
    }

}
