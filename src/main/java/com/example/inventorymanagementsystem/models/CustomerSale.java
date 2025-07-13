package com.example.inventorymanagementsystem.models;

import javafx.beans.property.*;

public class CustomerSale {
    private IntegerProperty saleID;
    private DoubleProperty receivedAmount;
    private DoubleProperty cost;
    private StringProperty date;

    private IntegerProperty amount;
    private ObjectProperty<ItemDetail> itemDetails;
    private ObjectProperty<Customer> customer;

    private ObjectProperty<CheckoutItem> checkoutItem;

    private IntegerProperty quantity;

    public CustomerSale(
            int saleID,
            double receivedAmount,
            double cost,
            int qty,
            String date,
            Customer customer,
            ItemDetail itemDetail,
            CheckoutItem checkoutItem
    ) {
        this.saleIDProperty().setValue(saleID);
        this.receivedAmountProperty().setValue(receivedAmount);
        this.costProperty().setValue(cost);
        this.dateProperty().setValue(date);
        this.customerProperty().setValue(customer);
        this.quantityProperty().setValue(qty);
        this.itemDetailsProperty().setValue(itemDetail);
        this.checkoutItemProperty().setValue(checkoutItem);
    }


    public int getSaleID() {
        return saleID.get();
    }

    public IntegerProperty saleIDProperty() {
        if (saleID == null){
            saleID = new SimpleIntegerProperty(this, "saleID");
        }
        return saleID;
    }

    public void setSaleID(int saleID) {
        this.saleID.set(saleID);
    }

    public double getReceivedAmount() {
        return receivedAmount.get();
    }

    public DoubleProperty receivedAmountProperty() {
        if (receivedAmount == null){
            receivedAmount = new SimpleDoubleProperty(this, "receivedAmount");
        }
        return receivedAmount;
    }

    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount.set(receivedAmount);
    }

    public double getCost() {
        return cost.get();
    }

    public DoubleProperty costProperty() {
        if (cost == null){
            cost = new SimpleDoubleProperty(this, "cost");
        }
        return cost;
    }

    public void setCost(double cost) {
        this.cost.set(cost);
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        if (date == null){
            date = new SimpleStringProperty(this, "date");
        }
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public ItemDetail getItemDetails() {
        return itemDetails.get();
    }

    public ObjectProperty<ItemDetail> itemDetailsProperty() {
        if (itemDetails == null){
            itemDetails = new SimpleObjectProperty<>(this, "itemDetails");
        }
        return itemDetails;
    }

    public void setItemDetails(ItemDetail itemDetails) {
        this.itemDetails.set(itemDetails);
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public ObjectProperty<Customer> customerProperty() {
        if (customer == null){
            customer = new SimpleObjectProperty<>(this, "customer");
        }
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer.set(customer);
    }

    public int getAmount() {
        return amount.get();
    }

    public IntegerProperty amountProperty() {
        if (amount == null){
            amount = new SimpleIntegerProperty(this, "amount");
        }
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.set(amount);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        if (quantity == null){
            quantity = new SimpleIntegerProperty(this, "quantity");
        }
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public CheckoutItem getCheckoutItem() {
        return checkoutItem.get();
    }

    public ObjectProperty<CheckoutItem> checkoutItemProperty() {
        if (checkoutItem == null){
            checkoutItem = new SimpleObjectProperty<>(this, "checkoutItem");
        }
        return checkoutItem;
    }

    public void setCheckoutItem(CheckoutItem checkoutItem) {
        this.checkoutItem.set(checkoutItem);
    }
}
