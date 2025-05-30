package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddUpdateCustomer extends VBox {
    FormField<TextField, String> firstNameField;
    FormField<TextField, String> lastNameField;
    FormField<TextField, String> phoneNumberField;
    FormField<TextField, String> emailField;
    FormField<DatePicker, String> registeredDateField;

    public AddUpdateCustomer(Customer customer){
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        phoneNumberField = new FormField<>("Phone", TextField.class);
        registeredDateField = new FormField<>("Date Registered", DatePicker.class);

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
                registeredDateField
        );
    }
}
