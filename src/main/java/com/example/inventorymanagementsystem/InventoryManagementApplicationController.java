package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.view.Checkout;
import com.example.inventorymanagementsystem.view.Inventory;
import com.example.inventorymanagementsystem.view.Analytics;

import com.example.inventorymanagementsystem.view.Users;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class InventoryManagementApplicationController {
    @FXML
    private TabPane tabPane;

    private Tab inventory;

    public interface NavigationHandler {
        void goToInventory();
    }

    public void initialize(){

    }
}