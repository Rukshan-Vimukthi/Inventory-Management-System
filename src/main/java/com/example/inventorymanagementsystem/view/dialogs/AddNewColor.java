package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;

public class AddNewColor extends Dialog<Boolean> {
    ColorPicker codeField;

    public AddNewColor(Color color){
        DialogPane dialogPane = new DialogPane();
        VBox vBox = new VBox();

        Label colorCodeLabel = new Label("Color Code");
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
                AddNewColor.this.setResult(true);
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewColor.this.setResult(false));

        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(colorCodeLabel, codeField, footer);
        dialogPane.setContent(vBox);
        this.setDialogPane(dialogPane);
    }
}
