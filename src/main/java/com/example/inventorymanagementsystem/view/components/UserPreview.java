package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.User;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserPreview extends VBox {
    Label firstName;
    Label lastName;

    Label userName;
    Label email;
    Label phone;
    Label registeredDate;

    private void init(){
        this.setFillWidth(true);
        this.setStyle("-fx-background-color: #151515; -fx-background-radius: 10px;");
        firstName = new Label("");
        lastName = new Label("");
        userName = new Label("");
        email = new Label("");
        phone = new Label("");
        registeredDate = new Label("");

        VBox nameContainer = new VBox();
        HBox firstAndLastnameContainer = new HBox();
        firstAndLastnameContainer.getChildren().addAll(firstName, lastName);
        nameContainer.getChildren().addAll(firstAndLastnameContainer, userName);

        this.getChildren().addAll(nameContainer, email, phone, registeredDate);
    }

    public UserPreview(User user){
        super();
        init();
        firstName.setText(user.getFirstName());
        lastName.setText(user.getUserName());
        userName.setText(user.getUserName());
        email.setText(user.getEmail());
    }

    public UserPreview(Customer customer){
        super();
        init();
        firstName.setText(customer.getFirstName());
        lastName.setText(customer.getLastName());
        email.setText(customer.getEmail());
        phone.setText(customer.getPhone());
    }

    public UserPreview(){
        super();
        init();
    }


}
