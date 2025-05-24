package com.example.inventorymanagementsystem.view;


import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.TableContainer;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Users extends HBox {
    public Users(){
        TableContainer<User> userTableContainer = new TableContainer<>();
        userTableContainer.addColumn("id", Integer.class);
        userTableContainer.addColumn("firstName", Integer.class);
        userTableContainer.addColumn("lastName", Integer.class);
        userTableContainer.addColumn("email", Integer.class);
        userTableContainer.addColumn("role", Integer.class);

        TableContainer<User> customerTableContainer = new TableContainer<>();
        customerTableContainer.addColumn("id", Integer.class);
        customerTableContainer.addColumn("first_ame", Integer.class);
        customerTableContainer.addColumn("last_ame", Integer.class);
        customerTableContainer.addColumn("phone", Integer.class);
        customerTableContainer.addColumn("email", Integer.class);


        VBox analyticsContainer = new VBox();
        analyticsContainer.setPadding(new Insets(10.0D));
        analyticsContainer.setMinWidth(300.0D);
        Card card = new Card("Title", "Content", "Footer");
        card.setBackgroundColor("#DDD");
        card.setPadding(new Insets(5.0D));
        card.setRoundedCorner(10.0D);
        analyticsContainer.getChildren().add(card);

        HBox.setHgrow(userTableContainer, Priority.ALWAYS);
        HBox.setHgrow(customerTableContainer, Priority.ALWAYS);
//        HBox.setHgrow(analyticsContainer, Priority.ALWAYS);
        this.getChildren().addAll(userTableContainer, customerTableContainer, analyticsContainer);
    }
}
