package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.state.Session;
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
import java.sql.SQLException;

public class SignIn extends VBox {
    TextField userNameField;
    PasswordField passwordField;

    Label errorLabel;
    public SignIn(Stage stage){
        this.setMinWidth(300.0D);
        this.setMaxWidth(300.0D);
        this.setMinHeight(180.0D);
        this.setPadding(new Insets(10.0D));

        errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("#FF0000"));

        Label userNameLabel = new Label("Username");
        userNameLabel.setStyle("-fx-text-fill: #00FFFF; -fx-font-weight: bold;");
        userNameField = new TextField();
        userNameField.setStyle("-fx-background-color: transparent; -fx-text-fill: #88DDFF; -fx-border-color: #0055FF; -fx-border-radius: 2px;");

        Label passwordLabel = new Label("Password");
        passwordLabel.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        passwordLabel.setStyle("-fx-text-fill: #00FFFF; -fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: transparent; -fx-text-fill: #88DDFF; -fx-border-color: #0055FF; -fx-border-radius: 2px;");

        HBox footer = new HBox();
        Button signInButton = new Button("SignIn");
        signInButton.getStyleClass().add("primary-button");
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button-danger");

        signInButton.setOnAction(actionEvent -> {
            errorLabel.setText("");
            try {
                ResultSet resultSet = Connection.getInstance().getUser(userNameField.getText(), passwordField.getText());
                if (resultSet != null) {
                    try {
                        int userID = resultSet.getInt("id");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        String userName = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String password = resultSet.getString("password");
                        String registeredDate = resultSet.getString("registered_date");
                        String role = resultSet.getString("role.role");
                        String phone = resultSet.getString("phone");

                        String pathToImage = resultSet.getString("image_path");
                        User user = new User(userID, firstName, lastName, userName, email, password, registeredDate, role, pathToImage, phone);

                        Session.getInstance().setSessionUser(user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    errorLabel.setText("Invalid username or password");
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        });

        closeButton.setOnAction(actionEvent -> {
            stage.close();
        });

        footer.getChildren().addAll(signInButton, closeButton);
        footer.setSpacing(10.0D);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        this.getChildren().addAll(
                errorLabel,
                userNameLabel,
                userNameField,
                passwordLabel,
                passwordField,
                footer);
    }
}
