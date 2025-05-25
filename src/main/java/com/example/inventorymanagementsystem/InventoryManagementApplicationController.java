package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.view.Checkout;
import com.example.inventorymanagementsystem.view.Inventory;
import com.example.inventorymanagementsystem.view.Stock;

import com.example.inventorymanagementsystem.view.Users;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class InventoryManagementApplicationController {
    @FXML
    private TabPane tabPane;

    public void initialize(){
        tabPane.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setRotateGraphic(true);
        tabPane.setTabMinWidth(30.0D);
        tabPane.setTabMaxWidth(30.0D);

        tabPane.setTabMinHeight(200.0D);
        tabPane.setTabMaxHeight(200.0D);

        // tabs in the main UI
        Tab checkoutTab = TabBuilder.buildTab("Checkout");
        Tab stocksTab = TabBuilder.buildTab("Stock");
        Tab inventory = TabBuilder.buildTab("Inventory");
        Tab users = TabBuilder.buildTab("Users");

        // create the inventory view (custom javaFX layout container ex. HBox, VBox)
        Inventory inventoryView = new Inventory();
        // set the custom view as the content of the tab created for inventory (inventory)
        inventory.setContent(inventoryView);

        // The checkout Section
        Checkout checkoutLayout = new Checkout();
        BorderPane checkoutContainer = checkoutLayout.getLayout();
        checkoutTab.setContent(checkoutContainer);

        // The Stock Section
        Stock stockView = new Stock();
        VBox stockViewContainer = stockView.getLayout();
        stocksTab.setContent(stockViewContainer);


        Users userTabView = new Users();
        users.setContent(userTabView);

        // Add tabs to the tabPane
        tabPane.getTabs().addAll(
                checkoutTab,
                stocksTab,
                inventory,
                users
        );
    }
}