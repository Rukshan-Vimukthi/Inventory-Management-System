package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class SignIn extends Dialog<Boolean> {
    TextField userNameField;
    PasswordField passwordField;

    Label errorLabel;
    public SignIn(Stage stage){
        DialogPane dialogPane = new DialogPane();
        this.setTitle("SignIn");
        dialogPane.setMinWidth(300.0D);
        dialogPane.setMinHeight(180.0D);
        VBox vBox = new VBox();

        errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("#FF0000"));

        Label userNameLabel = new Label("Username");
        userNameField = new TextField();

        Label passwordLabel = new Label("Password");
        passwordField = new PasswordField();

        HBox footer = new HBox();
        Button okButton = new Button("SignIn");
        Button closeButton = new Button("Close");

        okButton.setOnAction(actionEvent -> {
            errorLabel.setText("");
            ResultSet resultSet = Connection.getInstance().getUser(userNameField.getText(), passwordField.getText());
            if(resultSet != null){
                this.setResult(true);
            }else{
                errorLabel.setText("Invalid username or password");
            }
        });

        closeButton.setOnAction(actionEvent -> {
            this.setResult(false);
            stage.close();
        });

        footer.getChildren().addAll(okButton, closeButton);
        footer.setSpacing(10.0D);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        vBox.getChildren().addAll(
                errorLabel,
                userNameLabel,
                userNameField,
                passwordLabel,
                passwordField,
                footer);

        dialogPane.setContent(vBox);
        this.setDialogPane(dialogPane);
    }
}
