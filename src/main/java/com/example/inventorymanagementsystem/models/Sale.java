package com.example.inventorymanagementsystem.models;

import javafx.beans.property.*;

public class Sale {
    public IntegerProperty id;
    public StringProperty date;
    public DoubleProperty receivedMoney;
    public DoubleProperty totalCost;
    public IntegerProperty remainsStatusID;

    public Sale(int id, String date, double receivedMoney, double totalCost, int remainStatusID){
        idProperty().set(id);
        dateProperty().set(date);
        receivedMoneyProperty().set(receivedMoney);
        totalCostProperty().set(totalCost);
        remainsStatusIDProperty().set(remainStatusID);
    }

    public IntegerProperty idProperty(){
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty dateProperty(){
        if (date == null){
            date = new SimpleStringProperty(this, "date");
        }
        return date;
    }

    public DoubleProperty receivedMoneyProperty(){
        if (receivedMoney == null){
            receivedMoney = new SimpleDoubleProperty(this, "id");
        }
        return receivedMoney;
    }

    public DoubleProperty totalCostProperty(){
        if (totalCost == null){
            totalCost = new SimpleDoubleProperty(this, "totalCost");
        }
        return totalCost;
    }

    public IntegerProperty remainsStatusIDProperty(){
        if (remainsStatusID == null){
            remainsStatusID = new SimpleIntegerProperty(this, "remainsStatusID");
        }
        return remainsStatusID;
    }

    public int getId() {
        return id.get();
    }

    public String getDate() {
        return date.get();
    }

    public double getReceivedMoney() {
        return receivedMoney.get();
    }

    public double getTotalCost() {
        return totalCost.get();
    }

    public int getRemainsStatusID() {
        return remainsStatusID.get();
    }
}
