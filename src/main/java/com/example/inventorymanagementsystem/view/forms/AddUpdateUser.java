package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.Role;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    Button openFileChooserButton;
//    FormField<DatePicker, String> registeredDateField;

    Button addUserButton;
    Button closeButton;
    Dialog parentDialog;

    User user;

    private String selectedFilePath = null;

    public AddUpdateUser(User user, Dialog dialog){
        this.user = user;
        parentDialog = dialog;
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        userNameField = new FormField<>("User Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        passwordField = new FormField<>("Password", PasswordField.class);
        registeredDate = new FormField<>("Registered Date", DatePicker.class);
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
                selectedFilePath = destinationFilePath.toAbsolutePath().toString();
            }catch (IOException exception){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Could not copy the image to the destination location");
                alert.show();
            }
        });

        addUserButton = new Button("Add");
        addUserButton.setOnAction(actionEvent -> {
            addUser();
        });
        closeButton = new Button("Close");
        closeButton.setOnAction(actionEvent -> {
            if (dialog != null){
                dialog.setResult(false);
            }
        });

        HBox buttonContainer = new HBox();
        buttonContainer.getChildren().addAll(addUserButton, closeButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);

        if (user != null){
            firstNameField.setValue(user.getFirstName());
            lastNameField.setValue(user.getLastName());
            userNameField.setValue(user.getUserName());
            emailField.setValue(user.getEmail());
            passwordField.setValue(user.getPassword());
            addUserButton.setText("Update");
        }

        this.getChildren().addAll(
                firstNameField,
                lastNameField,
                userNameField,
                emailField,
                passwordField,
                registeredDate,
                role,
                openFileChooserButton,
                buttonContainer
        );
    }

    public void addUser(){
        int result = 0;
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
                    selectedFilePath
            );
        }else{
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

        if(result == 1 && parentDialog != null){
            parentDialog.setResult(true);
        }
    }
}
