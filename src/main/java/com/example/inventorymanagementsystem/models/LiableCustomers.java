package com.example.inventorymanagementsystem.models;

import javafx.beans.property.*;

public class LiableCustomers {
    public ObjectProperty<Customer> customerObject;
    public DoubleProperty liableAmount;

    public LiableCustomers(Customer customer, Double liableAmount){
        customerObjectProperty().setValue(customer);
        liableAmountProperty().setValue(liableAmount);
    }

    public ObjectProperty<Customer> customerObjectProperty(){
        if (customerObject == null){
            customerObject = new SimpleObjectProperty<>(this, "customerObject");
        }
        return customerObject;
    }

    public DoubleProperty liableAmountProperty(){
        if (liableAmount == null){
            liableAmount = new SimpleDoubleProperty(this, "liableAmount");
        }
        return liableAmount;
    }

    public Customer getCustomerObject() {
        return customerObject.get();
    }

    public double getLiableAmount() {
        return liableAmount.get();
    }
}
