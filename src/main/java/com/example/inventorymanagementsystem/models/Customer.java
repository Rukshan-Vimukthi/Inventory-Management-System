package com.example.inventorymanagementsystem.models;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer implements DataModel {
    private IntegerProperty id;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty phone;
    private StringProperty email;

    public IntegerProperty idProperty(){
        if(id == null){
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public StringProperty firstNameProperty(){
        if (firstName == null){
            firstName = new SimpleStringProperty(this, "firstName");
        }
        return firstName;
    }

    public StringProperty lastNameProperty(){
        if (lastName == null){
            lastName = new SimpleStringProperty(this, "lastName");
        }
        return lastName;
    }

    public StringProperty phoneProperty(){
        if (phone == null){
            phone = new SimpleStringProperty(this, "phone");
        }
        return phone;
    }

    public StringProperty emailProperty(){
        if (email == null){
            email = new SimpleStringProperty(this, "email");
        }
        return email;
    }


    public void setId(int id){
        idProperty().setValue(id);
    }

    public void setFirstName(String firstName){
        firstNameProperty().setValue(firstName);
    }

    public void setLastName(String lastName){
        lastNameProperty().setValue(lastName);
    }

    public void setEmail(String email){
        emailProperty().setValue(email);
    }

    public void setPhone(String phone){
        phoneProperty().setValue(phone);
    }

    public int getId() {
        return id.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getEmail() {
        return email.get();
    }

    @Override
    public String getValue() {
        return getFirstName() + ' ' + getLastName();
    }

    @Override
    public String toString() {
        return getFirstName() + ' ' + getLastName();
    }
}
