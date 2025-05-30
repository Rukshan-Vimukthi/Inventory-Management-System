package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.*;

/**
 * This class is used to get all the information related to a single item in the item table.
 * Recommended for using when showing all the item data and loading items in a checkout page.
 */
public class ItemDetail implements DataModel {
    private IntegerProperty id;
    private StringProperty name;
    private DoubleProperty price;
    private DoubleProperty sellingPrice;
    private StringProperty itemTotalCost = new SimpleStringProperty();

    private IntegerProperty stockID;
    private StringProperty stockDate;
    private StringProperty stockName;

    private IntegerProperty sizeID;
    private StringProperty size;

    private IntegerProperty itemColorID;
    private StringProperty itemColor;

    private IntegerProperty itemHasSizeID;

    private IntegerProperty colorHasItemHasSizeID;

    private IntegerProperty remainingQty;
    private IntegerProperty orderedQty;


    public ItemDetail(int id, String name, Double price,
                      Double sellingPrice, int stockID,
                      String stockDate,
                      String stockName,
                      int itemSizeID,
                      String itemSize,
                      int itemColorID,
                      String itemColor,
                      int itemHasSizeID,
                      int colorHasItemHasSizeID,
                      int orderedQty, int remainingQty){
        idProperty().setValue(id);
        nameProperty().setValue(name);
        priceProperty().setValue(price);
        sellingPriceProperty().setValue(sellingPrice);
        stockIDProperty().setValue(stockID);
        stockDateProperty().setValue(stockDate);
        stockNameProperty().setValue(stockName);
        sizeIDProperty().setValue(itemSizeID);
        sizeProperty().setValue(itemSize);
        itemColorIDProperty().setValue(itemColorID);
        itemColorProperty().setValue(itemColor);
        itemHasSizeIDProperty().setValue(itemHasSizeID);
        colorHasItemHasSizeIDProperty().setValue(colorHasItemHasSizeID);
        orderedQtyProperty().setValue(orderedQty);
        remainingQtyProperty().setValue(remainingQty);
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
    public IntegerProperty sizeIDProperty(){
        if (sizeID == null) {
            sizeID = new SimpleIntegerProperty(this, "itemSizeID");
        }
        return sizeID;
    }

    public StringProperty sizeProperty(){
        if (size == null) {
            size = new SimpleStringProperty(this, "itemSize");
        }
        return size;
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

    public IntegerProperty itemHasSizeIDProperty(){
        if (itemHasSizeID == null) {
            itemHasSizeID = new SimpleIntegerProperty(this, "itemHasID");
        }
        return itemHasSizeID;
    }

    public IntegerProperty colorHasItemHasSizeIDProperty(){
        if (colorHasItemHasSizeID == null){
            colorHasItemHasSizeID = new SimpleIntegerProperty(this, "colorHasItemHasSizeID");
        }
        return colorHasItemHasSizeID;
    }

    public IntegerProperty orderedQtyProperty(){
        if (orderedQty == null){
            orderedQty = new SimpleIntegerProperty(this, "orderedQty");
        }
        return orderedQty;
    }

    public IntegerProperty remainingQtyProperty(){
        if (remainingQty == null){
            remainingQty = new SimpleIntegerProperty(this, "remainingQty");
        }
        return remainingQty;
    }

    public int getPrice() {
        return (int) priceProperty().get(); // Correctly retrieves the price value
    }
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public double getSellingPrice() {
        return sellingPrice.get();
    }

    public int getStockID() {
        return stockID.get();
    }

    public String getStockDate() {
        return stockDate.get();
    }

    public String getStockName() {
        return stockName.get();
    }

    public int getSizeID() {
        return sizeID.get();
    }

    public String getSize() {
        return size.get();
    }

    public int getItemColorID() {
        return itemColorID.get();
    }

    public String getItemColor() {
        return itemColor.get();
    }

    public int getItemHasSizeID() {
        return itemHasSizeID.get();
    }

    @Override
    public String getValue(){
        return name.get();
    }

    public String getITemTotalCost() {
        return itemTotalCost.get();
    }
    public void setItemTotalCost (String value) {
        itemTotalCost.set(value);
    }
    public StringProperty itemTotalCostProperty () {
        return itemTotalCost;
    }

    public Integer getColorHasItemHasSize(){
        return colorHasItemHasSizeID.get();
    }

    public String getItemTotalCost() {
        return itemTotalCost.get();
    }

    public int getColorHasItemHasSizeID() {
        return colorHasItemHasSizeID.get();
    }

    public int getRemainingQty() {
        return remainingQty.get();
    }

    public int getOrderedQty() {
        return orderedQty.get();
    }

    @Override
    public String toString() {
        return name.get();
    }
}
