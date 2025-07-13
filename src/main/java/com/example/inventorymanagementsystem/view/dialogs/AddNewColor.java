package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.state.Constants;
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

public class AddNewColor extends Stage {
    ColorPicker codeField;

    public AddNewColor(Color color){
        VBox vBox = new VBox();

        this.initStyle(StageStyle.TRANSPARENT);

        Label title = new Label("Add new color");
        title.setTextFill(Paint.valueOf("#00AAFF"));

        Scene scene = new Scene(vBox);
        scene.setFill(Paint.valueOf("#00000000"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Constants.BASE_STYLES);
        scene.getStylesheets().add(Constants.DARK_THEME_CSS);

        Label colorCodeLabel = new Label("Color Code");
        colorCodeLabel.setTextFill(Paint.valueOf("#FFF"));
        codeField = new ColorPicker();

        HBox footer = new HBox();
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        Button addButton = new Button("ADD");

        if(color != null){
            codeField.setValue(javafx.scene.paint.Color.valueOf(color.getColor()));
            addButton.setText("UPDATE");
            this.setTitle("Update the color");
        }else{
            this.setTitle("Add a new color");
        }

        addButton.setOnAction(event -> {
            String colorCode = "#" + codeField.getValue().toString().split("0x")[1];
            try {
                if (color != null) {
                    Connection.getInstance().updateNewColor(color.getId(), colorCode);
                } else {
                    Connection.getInstance().addNewColor(colorCode);
                }
                AddNewColor.this.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewColor.this.close());

        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(title, colorCodeLabel, codeField, footer);

        vBox.getStyleClass().add("dialog");
        vBox.getStyleClass().addAll(Constants.BASE_STYLES, Constants.DARK_THEME_CSS);
        vBox.setPadding(new Insets(10.0D));
        addButton.getStyleClass().add("primary-button");
        cancelButton.getStyleClass().add("button-danger");

        this.setScene(scene);
    }
}
