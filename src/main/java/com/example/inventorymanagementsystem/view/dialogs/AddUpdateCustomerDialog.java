package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.view.forms.AddUpdateCustomer;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AddUpdateCustomerDialog extends Stage {
    public AddUpdateCustomerDialog(Customer customer){
        this.initStyle(StageStyle.TRANSPARENT);
        AddUpdateCustomer addUpdateCustomerForm = new AddUpdateCustomer(customer, this);
        Scene scene = new Scene(addUpdateCustomerForm);
        scene.setFill(Paint.valueOf("#0000"));
        scene.getStylesheets().add(Constants.DARK_THEME_CSS);
        scene.getStylesheets().add(Constants.BASE_STYLES);
        this.setScene(scene);
    }
}
