package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Map;

public class ItemStatus implements DataModel {
    private IntegerProperty id;
    private StringProperty status;

    public ItemStatus(int id, String status){
        idProperty().setValue(id);
        statusProperty().setValue(status);
    }

    public IntegerProperty idProperty() {
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty statusProperty() {
        if (status == null){
            status = new SimpleStringProperty(this, "status");
        }
        return status;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        idProperty().setValue(id);
    }

    public void setStatus(String statusText) {
        statusProperty().setValue(statusText);
    }

    public String getStatus() {
        return status.get();
    }

    @Override
    public String getValue(){
        return status.get();
    }

    @Override
    public String toString() {
        return status.get();
    }
}

