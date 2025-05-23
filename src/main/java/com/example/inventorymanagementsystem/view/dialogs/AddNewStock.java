package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddNewStock extends Dialog<Boolean> {
    TextField nameField;
    DatePicker picker;
    public AddNewStock(){
        DialogPane dialogPane = new DialogPane();
        dialogPane.setExpanded(true);
        VBox vBox = new VBox();

        Label nameLabel = new Label("Name");
        nameField = new TextField();

        Label dateLabel = new Label("Date");
        picker = new DatePicker();

        HBox footer = new HBox();

        Button addButton = new Button("ADD");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String date = picker.getValue().toString();
            System.out.println(name);
            System.out.println(date);
//            Connection.getInstance().addNewStock();
            AddNewStock.this.setResult(true);
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewStock.this.setResult(false));


        footer.getChildren().addAll(addButton, cancelButton);

        vBox.getChildren().addAll(nameLabel, nameField, dateLabel, picker, footer);

        dialogPane.getChildren().add(vBox);
        this.setDialogPane(dialogPane);
    }
}
