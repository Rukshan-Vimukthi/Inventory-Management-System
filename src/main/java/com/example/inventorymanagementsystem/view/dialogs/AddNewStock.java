package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AddNewStock extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(InventoryManagementApplication.class.getResource("./dialogs/AddNewStock.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Add New Stock");
        primaryStage.show();
    }
}
