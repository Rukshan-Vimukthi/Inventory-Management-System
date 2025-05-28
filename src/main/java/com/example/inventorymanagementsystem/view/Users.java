package com.example.inventorymanagementsystem.view;


import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.models.UserAnalytics;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.TableContainer;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Users extends HBox {
    public Users(){
        TableContainer<User> userTableContainer = new TableContainer<>(false, null, null);
        userTableContainer.addColumn("id", Integer.class);
        userTableContainer.addColumn("firstName", Integer.class);
        userTableContainer.addColumn("lastName", Integer.class);
        userTableContainer.addColumn("email", Integer.class);
        userTableContainer.addColumn("role", Integer.class);

        TableContainer<User> customerTableContainer = new TableContainer<>(false, null, null);
        customerTableContainer.addColumn("id", Integer.class);
        customerTableContainer.addColumn("first_ame", Integer.class);
        customerTableContainer.addColumn("last_ame", Integer.class);
        customerTableContainer.addColumn("phone", Integer.class);
        customerTableContainer.addColumn("email", Integer.class);

        UserAnalytics userAnalytics = Data.getInstance().getUserAnalytics();

        VBox analyticsContainer = new VBox();
        analyticsContainer.setPadding(new Insets(10.0D));
        analyticsContainer.setMinWidth(300.0D);

        HBox usersCardHeader = new HBox();
        FontIcon fontIcon = new FontIcon(FontAwesomeSolid.USER);
        Label userCardHeaderText = new Label(" Users", fontIcon);
        usersCardHeader.getChildren().add(userCardHeaderText);

        VBox userCardBody = new VBox();

        HBox footer = new HBox();

        Card card = new Card(usersCardHeader, userCardBody, footer);
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
