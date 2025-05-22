package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.view.Inventory;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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
        Tab administratorTab = TabBuilder.buildTab("Admininstrator");

        // create the inventory view (custom javaFX layout container ex. HBox, VBox)
        Inventory inventoryView = new Inventory();
        // set the custom view as the content of the tab created for inventory (inventory)
        inventory.setContent(inventoryView);

        // Add tabs to the tabPane
        tabPane.getTabs().add(checkoutTab);
        tabPane.getTabs().add(stocksTab);
        tabPane.getTabs().add(inventory);
    }
}