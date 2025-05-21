package com.example.inventorymanagementsystem.view;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// this is test

public class Checkout {
    private VBox mainLayout;

    public Checkout () {
        // The main container
        mainLayout = new VBox();
        mainLayout.setSpacing(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // The header container
        HBox headerSection = new HBox();
        headerSection.setSpacing(10);
        headerSection.setAlignment(Pos.TOP_CENTER);

        Label heading = new Label("Checkout Panel");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);

        // The Date
        AnchorPane dateHolder = new AnchorPane();
        Text dateTime = new Text("20/05/2025 - 09:00A.M");
        dateTime.setTextAlignment(TextAlignment.RIGHT);
        AnchorPane.setTopAnchor(dateTime, 0.0);
        AnchorPane.setRightAnchor(dateTime, 20.0);
        dateHolder.getChildren().add(dateTime);

        // For the adding items section
        HBox inputSection = new HBox();
        inputSection.setSpacing(10);
        inputSection.setPadding(new Insets(10));
        inputSection.setAlignment(Pos.CENTER);

        // Input area
        TextField amount = new TextField("Type the quantity");
        String items[] = {"T-Shirts", "Pants", "Shorts", "Caps"};
        ComboBox itemComboBox = new ComboBox(FXCollections.observableArrayList(items));
        itemComboBox.setValue("Select Items");
        Button addButton = new Button("Add to List");

        headerSection.getChildren().addAll(heading);
        inputSection.getChildren().addAll(amount, itemComboBox, addButton);
        mainLayout.getChildren().addAll(headerSection, inputSection, dateHolder);
    }

    public VBox getLayout() {
        return mainLayout;
    }

}

