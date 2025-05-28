package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Size implements DataModel {
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

    public int getId() {
        return id.get();
    }

    public void setId(int id){
        idProperty().setValue(id);
    }

    public void setColor(String color){
        sizeProperty().setValue(color);
    }

    public String getSize() {
        return size.get();
    }

    @Override
    public String getValue(){
        return size.get();
    }

    @Override
    public String toString() {
        return size.get();
    }
}
