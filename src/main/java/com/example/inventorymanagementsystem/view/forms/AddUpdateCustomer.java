package com.example.inventorymanagementsystem.view.forms;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddUpdateCustomer extends VBox {
    FormField<TextField, String> firstNameField;
    FormField<TextField, String> lastNameField;
    FormField<TextField, String> phoneNumberField;
    FormField<TextField, String> emailField;
    FormField<DatePicker, String> registeredDateField;

    Button openFileChooserButton;
    String selectedFilePath;

    public AddUpdateCustomer(Customer customer, Stage parent){
        this.setStyle("-fx-background-color: #335; -fx-background-radius: 10px;");
        this.setPadding(new Insets(10.0D));
        this.setSpacing(5.0D);
        firstNameField = new FormField<>("First Name", TextField.class);
        lastNameField = new FormField<>("Last Name", TextField.class);
        emailField = new FormField<>("Email", TextField.class);
        phoneNumberField = new FormField<>("Phone", TextField.class);
        registeredDateField = new FormField<>("Date Registered", DatePicker.class);

        openFileChooserButton = new Button("Select the image");
        openFileChooserButton.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);

            Path destinationPath = Paths.get(Constants.customersMediaDirectory);
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

        Button addCustomerButton = new Button("Add");
        addCustomerButton.getStyleClass().add("success-button");
        addCustomerButton.setOnAction(actionEvent -> {
            if (customer == null) {
                try {
                    Connection.getInstance().addCustomers(
                            (String) firstNameField.getValue(),
                            (String) lastNameField.getValue(),
                            (String) phoneNumberField.getValue(),
                            (String) emailField.getValue(),
                            ((LocalDate) registeredDateField.getValue()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            selectedFilePath);
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        });
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("button-danger");
        closeButton.setOnAction(actionEvent -> {
            parent.close();
        });

        HBox footer = new HBox();
        footer.setSpacing(5.0D);
        footer.getChildren().addAll(addCustomerButton, closeButton);
        footer.setAlignment(Pos.CENTER_RIGHT);

        if (customer != null){
            firstNameField.setValue(customer.getFirstName());
            lastNameField.setValue(customer.getLastName());
            emailField.setValue(customer.getEmail());
            phoneNumberField.setValue(customer.getPhone());
            registeredDateField.setValue(customer.getRegisteredDate());
        }

        this.getChildren().addAll(
                firstNameField,
                lastNameField,
                emailField,
                phoneNumberField,
                registeredDateField,
                openFileChooserButton,
                footer
        );

        this.getStylesheets().add(Constants.DARK_THEME_CSS);

    }

}
