package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.components.TabBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

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

        Tab checkoutTab = TabBuilder.buildTab("Checkout");
        Tab stocksTab = TabBuilder.buildTab("Stock");

        tabPane.getTabs().add(checkoutTab);
        tabPane.getTabs().add(stocksTab);

//        tabPane.getTabs().add(TabBuilder.buildTab("Reports"));
    }
}