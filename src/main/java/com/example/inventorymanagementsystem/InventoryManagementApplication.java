package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.state.Data;

import com.example.inventorymanagementsystem.view.Checkout;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import com.example.inventorymanagementsystem.view.dialogs.SignIn;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class InventoryManagementApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InventoryManagementApplication.class.getResource("ApplicationUI.fxml"));

        Parent root = fxmlLoader.load();

        TabPane rootLayout = (TabPane) root;


//        Checkout checkoutLayout = new Checkout();
//        BorderPane checkoutContainer = checkoutLayout.getLayout();

//        Tab checkOutTab = new Tab("Checkout");
//        checkOutTab.setContent(checkoutContainer);

//        rootLayout.getTabs().add(checkOutTab);

        Scene scene = new Scene(rootLayout);

        scene.getStylesheets().add(
            String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        stage.setTitle("SFC Inventory Management System with Integrated POS Features");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();

        SignIn signIn = new SignIn(stage);
        signIn.show();
    }

    public static void main(String[] args) {
        launch();
    }
}