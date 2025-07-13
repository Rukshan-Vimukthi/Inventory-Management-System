package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Size;
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

public class AddNewSize extends Stage {
    TextField sizeField;
    public AddNewSize(Size size){
        VBox vBox = new VBox();

        this.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(vBox);
        scene.setFill(Paint.valueOf("#00000000"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Constants.BASE_STYLES);
        scene.getStylesheets().add(Constants.DARK_THEME_CSS);

        Label title = new Label("Add new color");
        title.setTextFill(Paint.valueOf("#00AAFF"));

        Label sizeLabel = new Label("Size");
        sizeLabel.setTextFill(Paint.valueOf("#FFF"));
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
                AddNewSize.this.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(event -> AddNewSize.this.close());

        footer.getChildren().addAll(addButton, cancelButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10.0D);

        vBox.getChildren().addAll(title, sizeLabel, sizeField, footer);

        vBox.getStyleClass().add("dialog");
        vBox.getStyleClass().addAll(Constants.BASE_STYLES, Constants.DARK_THEME_CSS);
        vBox.setPadding(new Insets(10.0D));
        addButton.getStyleClass().add("primary-button");
        cancelButton.getStyleClass().add("button-danger");

        this.setScene(scene);
    }
}
