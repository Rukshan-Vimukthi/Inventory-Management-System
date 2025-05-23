package com.example.inventorymanagementsystem.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Color {
    private IntegerProperty id;
    private StringProperty color;

    public Color(int id, String colorCode){
        idProperty().setValue(id);
        colorProperty().setValue(colorCode);
    }

    public IntegerProperty idProperty(){
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty colorProperty(){
        if (color == null){
            color = new SimpleStringProperty(this, "color");
        }
        return color;
    }

    private void setColor(String color){
        colorProperty().setValue(color);
    }

    public int getId() {
        return id.get();
    }

    public String getColor() {
        return color.get();
    }
}
