package com.example.inventorymanagementsystem.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Role {
    private IntegerProperty id;
    private StringProperty role;

    public Role(int id, String role){
        this.idProperty().setValue(id);
        this.roleProperty().setValue(role);
    }

    public IntegerProperty idProperty(){
        if (id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty roleProperty(){
        if (role == null){
            role = new SimpleStringProperty(this, "id");
        }
        return role;
    }

    public int getId() {
        return id.get();
    }

    public String getRole() {
        return role.get();
    }
}
