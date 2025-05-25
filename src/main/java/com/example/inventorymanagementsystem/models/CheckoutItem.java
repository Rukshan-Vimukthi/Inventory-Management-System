package com.example.inventorymanagementsystem.models;
import javafx.beans.property.*;

public class CheckoutItem {
    private final IntegerProperty id;
    private final IntegerProperty customerId;
    private final SimpleIntegerProperty itemHasSizeId;
    private final IntegerProperty amount;
    private final DoubleProperty price;
    private final StringProperty date;
    private final SimpleIntegerProperty itemStatusId;

    public CheckoutItem(int id, int customerId, int itemHasSizeId , int amount, double price, String date, int itemStatusId) {
        this.id = new SimpleIntegerProperty(id);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.itemHasSizeId = new SimpleIntegerProperty(itemHasSizeId);
        this.amount = new SimpleIntegerProperty(amount);
        this.price = new SimpleDoubleProperty(price);
        this.date = new SimpleStringProperty(date);
        this.itemStatusId = new SimpleIntegerProperty(itemStatusId);
    }

    public int getId() { return id.get(); }
    public int getCustomerId() { return customerId.get(); }
    public int getItemHasSizeId() { return itemHasSizeId.get(); }
    public int getAmount() { return amount.get(); }
    public double getPrice() { return price.get(); }
    public String getDate() { return date.get(); }
    public int getItemStatusId() { return itemStatusId.get(); }

    public IntegerProperty idProperty() { return id; }
    public IntegerProperty customerIdProperty() { return customerId; }
    public IntegerProperty itemHasSizeIdProperty() { return itemHasSizeId; }
    public IntegerProperty amountProperty() { return amount; }
    public DoubleProperty priceProperty() { return price; }
    public StringProperty dateProperty() { return date; }
    public IntegerProperty itemStatusIdProperty() { return itemStatusId; }

}
