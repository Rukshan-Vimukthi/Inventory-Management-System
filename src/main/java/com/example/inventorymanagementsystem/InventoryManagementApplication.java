package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.view.Checkout;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InventoryManagementApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InventoryManagementApplication.class.getResource("ApplicationUI.fxml"));

        Parent root = fxmlLoader.load();

        TabPane rootLayout = (TabPane) root;

        Checkout checkoutLayout = new Checkout();
        HBox checkoutContainer = checkoutLayout.getLayout();

        Tab checkOutTab = new Tab("Checkout");
        checkOutTab.setContent(checkoutContainer);
        rootLayout.getTabs().add(checkOutTab);

        Scene scene = new Scene(rootLayout, 900, 500);

        scene.getStylesheets().add(
            String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        stage.setTitle("SFC Inventory Management System with Integrated POS Features");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}