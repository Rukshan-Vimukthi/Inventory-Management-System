package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Stock;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

public class AddNewStock extends Dialog<Boolean> {
    TextField nameField;
    DatePicker picker;
    public AddNewStock(Stock stock){
        DialogPane dialogPane = new DialogPane();

        VBox vBox = new VBox();

        Label nameLabel = new Label("Name");
        nameField = new TextField();

        Label dateLabel = new Label("Date");
        picker = new DatePicker();

        if(stock != null){
            nameField.setText(stock.getName());
            picker.setValue(LocalDate.parse(stock.getDate().split(" ")[0]));
            this.setTitle("Update a new stock");
        }else{
            this.setTitle("Add a new stock");
        }

        HBox footer = new HBox();
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        Button addButton = new Button("ADD");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String date = picker.getValue().toString();
            System.out.println(name);
            System.out.println(date);
            AddNewStock.this.setResult(true);
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewStock.this.setResult(false));


        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(nameLabel, nameField, dateLabel, picker, footer);

//        dialogPane.getChildren().add(vBox);
        dialogPane.setContent(vBox);
        this.setDialogPane(dialogPane);
    }
}
