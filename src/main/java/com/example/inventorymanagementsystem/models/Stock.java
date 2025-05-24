package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Date;

public class Stock implements DataModel {
    private IntegerProperty id;
    private StringProperty date;
    private StringProperty name;
    public Stock(int id, String date, String name){
        idProperty().setValue(id);
        dateProperty().setValue(date);
        nameProperty().setValue(name);
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

    public StringProperty nameProperty(){
        if(name == null){
            name = new SimpleStringProperty(this, "name");
        }
        return name;
    }

    public int getId() {
        return id.get();
    }

    public String getDate() {
        return date.get();
    }

    public String getName() {
        return name.get();
    }

    @Override
    public String getValue(){
        return date.get() + " | " + name.get();
    }
}
