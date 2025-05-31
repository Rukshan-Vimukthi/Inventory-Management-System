package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.view.forms.AddUpdateCustomer;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class AddUpdateCustomerDialog extends Dialog<Boolean> {
    public AddUpdateCustomerDialog(Customer customer){
        DialogPane dialogPane = new DialogPane();
        AddUpdateCustomer addUpdateCustomerForm = new AddUpdateCustomer(customer, this);
        dialogPane.setContent(addUpdateCustomerForm);
        this.setDialogPane(dialogPane);
    }
}
