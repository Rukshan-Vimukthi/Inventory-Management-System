package com.example.inventorymanagementsystem.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private IntegerProperty id;
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty username;
    private StringProperty email;
    private StringProperty password;

    private StringProperty registeredDate;

    private StringProperty role;

    public User( Integer id, String firstName, String lastName, String username, String email, String password, String registeredDate, String role) {

        idProperty().setValue(id);
        firstNameProperty().setValue(firstName);
        lastNameProperty().setValue(lastName);
        usernameProperty().setValue(username);
        emailProperty().setValue(email);
        passwordProperty().setValue(password);
        registeredDateProperty().setValue(registeredDate);
        roleProperty().setValue(role);

    }

    public IntegerProperty idProperty(){
        if (id == null){
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
    public StringProperty usernameProperty(){
        if (username == null){
            username = new SimpleStringProperty(this, "username");
        }
        return username;
    }
    public StringProperty emailProperty(){
        if (email == null){
            email = new SimpleStringProperty(this, "email");
        }
        return email;
    }
    public StringProperty passwordProperty(){
        if (password == null){
            password = new SimpleStringProperty(this, "password");
        }
        return password;
    }
    public StringProperty registeredDateProperty(){
        if (registeredDate == null){
            registeredDate = new SimpleStringProperty(this, "registeredDateProperty");
        }
        return registeredDate;
    }

    public StringProperty roleProperty(){
        if (role == null){
            role = new SimpleStringProperty(this, "role");
        }
        return role;
    }
    public Integer getId() { return id.get(); }

    public String getFirstName() { return firstName.get(); }

    public String getLastName() { return lastName.get(); }

    public String getUserName() { return username.get(); }

    public String getEmail() { return email.get(); }

    public String getPassword() { return password.get(); }

    public String getRegisteredDate(){
        return registeredDate.get();
    }
    public String getRole(){
        return role.get();
    }
}
