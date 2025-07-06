package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Size;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class AddNewSize extends Dialog<Boolean> {
    TextField sizeField;
    public AddNewSize(Size size){
        DialogPane dialogPane = new DialogPane();
        VBox vBox = new VBox();

        Label sizeLabel = new Label("Size");
        sizeField = new TextField();

        HBox footer = new HBox();
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        Button addButton = new Button("ADD");
        if(size != null){
            sizeField.setText(size.getSize());
            addButton.setText("UPDATE");
            this.setTitle("Update the size");
        }else{
            this.setTitle("Add a new size");
        }

        addButton.setOnAction(event -> {
            String sizeFieldText = sizeField.getText();
            try {
                if (size != null) {
                    Connection.getInstance().updateSize(size.getId(), sizeFieldText);
                } else {
                    Connection.getInstance().addNewSize(sizeFieldText);
                }
                AddNewSize.this.setResult(true);
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewSize.this.setResult(false));

        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(sizeLabel, sizeField, footer);
        dialogPane.setContent(vBox);
        this.setDialogPane(dialogPane);
    }
}
