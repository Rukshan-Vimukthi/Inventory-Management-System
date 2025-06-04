package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddUpdateCustomer extends VBox {
    FormField<TextField, String> firstNameField;
    FormField<TextField, String> lastNameField;
    FormField<TextField, String> phoneNumberField;
    FormField<TextField, String> emailField;
    FormField<DatePicker, String> registeredDateField;

    public AddUpdateCustomer(Customer customer, Dialog<Boolean> dialog){
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        phoneNumberField = new FormField<>("Phone", TextField.class);
        registeredDateField = new FormField<>("Date Registered", DatePicker.class);

        Button addCustomerButton = new Button("Add");
        addCustomerButton.setOnAction(actionEvent -> {
            Connection.getInstance().addCustomers(
                    (String)firstNameField.getValue(),
                    (String)lastNameField.getValue(),
                    (String)phoneNumberField.getValue(),
                    (String)emailField.getValue(),
                    ((LocalDate)registeredDateField.getValue()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        });
        Button closeButton = new Button("Close");
        closeButton.setOnAction(actionEvent -> {
            dialog.setResult(false);
        });

        HBox footer = new HBox();
        footer.getChildren().addAll(addCustomerButton, closeButton);
        footer.setAlignment(Pos.CENTER_RIGHT);

        if (customer != null){
            firstNameField.setValue(customer.getFirstName());
            lastNameField.setValue(customer.getLastName());
            emailField.setValue(customer.getEmail());
            phoneNumberField.setValue(customer.getPhone());
            registeredDateField.setValue(customer.getRegisteredDate());
        }

        this.getChildren().addAll(
                firstNameField,
                lastNameField,
                emailField,
                phoneNumberField,
                registeredDateField,
                footer
        );
    }

}
