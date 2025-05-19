package com.example.inventorymanagementsystem;

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

        Tab checkoutTab = TabBuilder.buildTab("Checkout");
        Tab stocksTab = TabBuilder.buildTab("Stock");
        Tab administratorTab = TabBuilder.buildTab("Admininstrator");

        FXMLLoader adminUILoader = new FXMLLoader(InventoryManagementApplication.class.getResource("AdministratorUI.fxml"));
        try {
            administratorTab.setContent(adminUILoader.load());
        }catch(IOException e){
            // couldn't load the fxml file
            e.printStackTrace();
        }

        tabPane.getTabs().add(checkoutTab);
        tabPane.getTabs().add(stocksTab);

//        tabPane.getTabs().add(TabBuilder.buildTab("Reports"));
    }
}