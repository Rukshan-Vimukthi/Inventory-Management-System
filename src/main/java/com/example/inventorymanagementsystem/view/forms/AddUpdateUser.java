package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.Role;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class AddUpdateUser extends VBox {
    FormField<TextField, String> firstNameField;
    FormField<TextField, String> lastNameField;
    FormField<TextField, String> userNameField;
//    FormField<TextField, String> phoneNumberField;
    FormField<TextField, String> emailField;
    FormField<PasswordField, String> passwordField;

    FormField<DatePicker, String> registeredDate;

    FormField<ComboBox, Role> role;
//    FormField<DatePicker, String> registeredDateField;

    Button addUserButton;
    Button closeButton;
    Dialog parentDialog;

    public AddUpdateUser(User user, Dialog dialog){
        parentDialog = dialog;
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        userNameField = new FormField<>("User Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        passwordField = new FormField<>("Password", PasswordField.class);
        registeredDate = new FormField<>("Registered Date", DatePicker.class);
        role = new FormField<>("Role", ComboBox.class, Data.getInstance().getRoles());
        addUserButton = new Button("Add");
        addUserButton.setOnAction(actionEvent -> {
            addUser();
        });
        closeButton = new Button("Close");
        closeButton.setOnAction(actionEvent -> {
            if (dialog != null){
                dialog.setResult(false);
            }
        });

        if (user != null){
            firstNameField.setValue(user.getFirstName());
            lastNameField.setValue(user.getLastName());
            emailField.setValue(user.getEmail());
            passwordField.setValue(user.getPassword());
        }

        this.getChildren().addAll(
                firstNameField,
                lastNameField,
                emailField,
                passwordField,
                registeredDate,
                role,
                addUserButton, closeButton
        );
    }

    public void addUser(){
        int result = Connection.getInstance().addNewUser(
                (String)firstNameField.getValue(),
                (String)lastNameField.getValue(),
                (String)userNameField.getValue(),
                (String)emailField.getValue(),
                (String)passwordField.getValue(),
                ((LocalDate)registeredDate.getValue()).toString(),
                ((Role)role.getValue()).getId()
        );
        if(result == 1 && parentDialog != null){
            parentDialog.setResult(true);
        }
    }
}
