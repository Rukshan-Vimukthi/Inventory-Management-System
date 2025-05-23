package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.models.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class AddNewColor extends Dialog<Boolean> {
    TextField codeField;

    public AddNewColor(Color color){
        DialogPane dialogPane = new DialogPane();
        VBox vBox = new VBox();

        Label nameLabel = new Label("Name");
        codeField = new TextField();

        Label dateLabel = new Label("Date");

        if(color != null){
            codeField.setText(color.getColor());
            this.setTitle("Update a new stock");
        }else{
            this.setTitle("Add a new stock");
        }

        HBox footer = new HBox();
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        Button addButton = new Button("ADD");
        addButton.setOnAction(event -> {
            String name = codeField.getText();
            AddNewColor.this.setResult(true);
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewColor.this.setResult(false));


        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(nameLabel, dateLabel, footer);
        dialogPane.setContent(vBox);
        this.setDialogPane(dialogPane);
    }
}
