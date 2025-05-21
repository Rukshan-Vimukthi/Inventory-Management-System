package com.example.inventorymanagementsystem.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Stock {
    private IntegerProperty id;
    private StringProperty date;
    private StringProperty name;
    public Stock(int id, String date, String name){
        idProperty().setValue(id);
        dateProperty().setValue(date);
        nameProperty().setValue(name);
    }


    private IntegerProperty idProperty(){
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    private StringProperty dateProperty(){
        if (date == null){
            date = new SimpleStringProperty(this, "date");
        }
        return date;
    }

    private StringProperty nameProperty(){
        if(name == null){
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }
}
