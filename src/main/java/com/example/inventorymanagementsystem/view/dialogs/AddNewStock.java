package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Constants;
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
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

public class AddNewStock extends Stage {
    TextField nameField;
    DatePicker picker;
    public AddNewStock(Stock stock){
        this.initStyle(StageStyle.TRANSPARENT);
        VBox vBox = new VBox();

        Scene scene = new Scene(vBox);
        scene.setFill(Paint.valueOf("#00000000"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Constants.BASE_STYLES);
        scene.getStylesheets().add(Constants.DARK_THEME_CSS);

        Label title = new Label("Add new color");
        title.setTextFill(Paint.valueOf("#00AAFF"));

        Label nameLabel = new Label("Name");
        nameLabel.setTextFill(Paint.valueOf("#FFF"));
        nameField = new TextField();

        Label dateLabel = new Label("Date");
        dateLabel.setTextFill(Paint.valueOf("#FFF"));
        picker = new DatePicker();

        HBox footer = new HBox();
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));

        Button addButton = new Button("ADD");
        if(stock != null){
            nameField.setText(stock.getName());
            picker.setValue(LocalDate.parse(stock.getDate().split(" ")[0]));
            addButton.setText("UPDATE");
            this.setTitle("Update the stock");
        }else{
            this.setTitle("Add a new stock");
        }
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            String date = picker.getValue().toString();
            try {
                if (stock != null) {
                    Connection.getInstance().updateStock(stock.getId(), date, name);
                } else {
                    Connection.getInstance().addNewStock(date, name);
                }
                AddNewStock.this.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewStock.this.close());


        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);
        footer.setPadding(new Insets(10.0D, 0.0D, 10.0D, 0.0D));

        vBox.getChildren().addAll(title, nameLabel, nameField, dateLabel, picker, footer);

        vBox.getStyleClass().add("dialog");
        vBox.getStyleClass().addAll(Constants.BASE_STYLES, Constants.DARK_THEME_CSS);
        vBox.setPadding(new Insets(10.0D));
        addButton.getStyleClass().add("primary-button");
        cancelButton.getStyleClass().add("button-danger");
//        dialogPane.getChildren().add(vBox);
        this.setScene(scene);
    }
}
