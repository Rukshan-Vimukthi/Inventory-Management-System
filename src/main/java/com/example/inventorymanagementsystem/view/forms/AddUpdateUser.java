package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Role;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddUpdateUser extends VBox {
    FormField<TextField, String> firstNameField;
    FormField<TextField, String> lastNameField;
    FormField<TextField, String> userNameField;
//    FormField<TextField, String> phoneNumberField;
    FormField<TextField, String> emailField;
    FormField<PasswordField, String> passwordField;

    FormField<DatePicker, String> registeredDate;

    FormField<ComboBox, Role> role;
    FormField<TextField, Role> phoneNumber;

    Button openFileChooserButton;
//    FormField<DatePicker, String> registeredDateField;

    Button addUserButton;
    Button closeButton;
    Stage stage;
    User user;

    private String selectedFilePath = null;

    public AddUpdateUser(User user, Stage stage) throws SQLException {
        this.user = user;
        this.stage = stage;
        this.setPadding(new Insets(15.0D));
        this.setStyle("-fx-background-color: #335; -fx-background-radius: 10px;");
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        userNameField = new FormField<>("User Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        passwordField = new FormField<>("Password", PasswordField.class);
        registeredDate = new FormField<>("Registered Date", DatePicker.class);
        phoneNumber = new FormField<>("Phone Number", TextField.class);
        role = new FormField<>("Role", ComboBox.class, Data.getInstance().getRoles());

        openFileChooserButton = new Button("Select the image");
        openFileChooserButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);

            Path destinationPath = Paths.get(Constants.usersMediaDirectory);
            try{
                Files.createDirectories(destinationPath);
            }catch(IOException exception){
                exception.printStackTrace();
            }
            Path destinationFilePath = destinationPath.resolve(selectedFile.getName());
            Path sourcePath = selectedFile.toPath();

            try {
                Files.copy(sourcePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
                selectedFilePath = destinationFilePath.toAbsolutePath().toString().replace('\\', '/');
                System.out.println(selectedFilePath);
            }catch (IOException exception){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Could not copy the image to the destination location");
                alert.show();
            }
        });

        addUserButton = new Button("Add");
        addUserButton.getStyleClass().add("success-button");
        addUserButton.setOnAction(actionEvent -> {
            addUser();
        });
        closeButton = new Button("Close");
        closeButton.getStyleClass().add("button-danger");
        closeButton.setOnAction(actionEvent -> {
            this.stage.close();
        });

        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().addAll(addUserButton, closeButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        buttonContainer.setSpacing(10.0D);

        if (user != null){
            firstNameField.setValue(user.getFirstName());
            lastNameField.setValue(user.getLastName());
            userNameField.setValue(user.getUserName());
            emailField.setValue(user.getEmail());
            passwordField.setValue(user.getPassword());
            phoneNumber.setValue(user.getPhoneNumber());
            addUserButton.setText("Update");
        }

        this.setSpacing(10.0D);

        this.getChildren().addAll(
                firstNameField,
                lastNameField,
                userNameField,
                emailField,
                passwordField,
                registeredDate,
                phoneNumber,
                role,
                openFileChooserButton,
                buttonContainer
        );
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }

    public void addUser(){
        int result = 0;
        try {
            if (this.user == null) {
                System.out.println(selectedFilePath);
                result = Connection.getInstance().addNewUser(
                        (String) firstNameField.getValue(),
                        (String) lastNameField.getValue(),
                        (String) userNameField.getValue(),
                        (String) emailField.getValue(),
                        (String) passwordField.getValue(),
                        ((LocalDate) registeredDate.getValue()).toString(),
                        ((Role) role.getValue()).getId(),
                        selectedFilePath,
                        (String) phoneNumber.getValue()
                );
            } else {
                result = Connection.getInstance().updateUser(
                        this.user.getId(),
                        (String) firstNameField.getValue(),
                        (String) lastNameField.getValue(),
                        (String) userNameField.getValue(),
                        (String) emailField.getValue(),
                        (String) passwordField.getValue(),
                        ((LocalDate) registeredDate.getValue()).toString(),
                        ((Role) role.getValue()).getId()
                );
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
