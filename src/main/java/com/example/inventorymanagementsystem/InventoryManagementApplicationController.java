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
        Tab stocksTab = TabBuilder.buildTab("Analytics");
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
        Analytics stockView = new Analytics(() -> tabPane.getSelectionModel().select(inventory));
        VBox stockViewContainer = stockView.getLayout();

        ScrollPane scrollableAnalytics = new ScrollPane(stockViewContainer);
        scrollableAnalytics.setFitToWidth(true); // Optional: makes VBox match width
        scrollableAnalytics.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        stocksTab.setContent(scrollableAnalytics);

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