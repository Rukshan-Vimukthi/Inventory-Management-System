package com.example.inventorymanagementsystem.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Size {
    private IntegerProperty id;
    private StringProperty size;

    public Size(int id, String sizeText){
        idProperty().setValue(id);
        sizeProperty().setValue(sizeText);
    }

    public IntegerProperty idProperty(){
        if(id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty sizeProperty(){
        if(size == null){
            size = new SimpleStringProperty(this, "size");
        }
        return size;
    }

    public void setId(int id){
        idProperty().setValue(id);
    }

    public void setColor(String color){
        sizeProperty().setValue(color);
    }

    public int getId() {
        return id.get();
    }

    public String getSize() {
        return size.get();
    }
}
