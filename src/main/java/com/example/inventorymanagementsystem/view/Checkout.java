package com.example.inventorymanagementsystem.view;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// this is test

public class Checkout {
    private BorderPane mainLayout;

    public Checkout () {
        // The main container
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // The header container
        VBox headerSection = new VBox(5);
        headerSection.setSpacing(10);
        headerSection.setAlignment(Pos.TOP_CENTER);
        Label heading = new Label("Checkout Panel");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setMaxWidth(Double.MAX_VALUE);
        heading.setAlignment(Pos.CENTER);

        // The Date
        AnchorPane dateHolder = new AnchorPane();
        dateHolder.setPrefHeight(30);
        dateHolder.setMaxWidth(Double.MAX_VALUE);
        Text dateTime = new Text("20/05/2025 - 09:00A.M");
        AnchorPane.setTopAnchor(dateTime, 0.0);
        AnchorPane.setRightAnchor(dateTime, 0.0);
        dateHolder.setMaxWidth(Double.MAX_VALUE);
        dateHolder.getChildren().add(dateTime);

        // Input area
        VBox inputVerticalSec = new VBox();
        inputVerticalSec.setAlignment(Pos.TOP_LEFT);
        TextField amount = new TextField("Type the quantity");
        amount.setMinWidth(100);
        amount.setMaxWidth(150);
        TextField discount = new TextField("Type the discount");
        discount.setMinWidth(100);
        discount.setMaxWidth(150);
        String items[] = {"T-Shirts", "Pants", "Shorts", "Caps", "Shirts"};
        ComboBox itemComboBox = new ComboBox(FXCollections.observableArrayList(items));
        itemComboBox.setPrefWidth(150);
        itemComboBox.setValue("Select Items");

        // For the adding items section
        VBox inputSection = new VBox();
        inputSection.setSpacing(10);
        inputSection.setPadding(new Insets(10));
        inputSection.setAlignment(Pos.TOP_LEFT);

        HBox addButtonSection = new HBox();
        addButtonSection.setSpacing(10);
        addButtonSection.setPadding(new Insets(0, 0, 0, 10));
        addButtonSection.setAlignment(Pos.TOP_LEFT);
        Button addButton = new Button("Add to List");
        addButton.setPrefWidth(150);

        // Bottom Section
        TextField discountForAll = new TextField("Apply Discount to All");

        Text totalCostTxt = new Text("Total Cost:");
        Label totalCost = new Label("$40");

        Text totalDiscountTxt = new Text("Total Discount:");
        Label totalDiscount = new Label("$40");

        Text grandTotalTxt = new Text("Grand Total:");
        Label grandTotal = new Label("$40");

        Text fundTxt = new Text("Received Fund:");
        Label fund = new Label("$40");

        Text balanceTxt = new Text("Due Balance:");
        Label balance = new Label("$40");
        Button completeBtn = new Button("Complete Sales");

        // The floating section in the right_side
        Button remove = new Button("Remove All");
        Button clearForm = new Button("Clear Form");
        Button goBack = new Button("Cancel");

        VBox actionSection = new VBox();
        actionSection.setSpacing(10);
        actionSection.setPrefWidth(200);
        actionSection.setAlignment(Pos.TOP_RIGHT);
        actionSection.setPadding(new Insets(0, 0, 10, 0));
        actionSection.getChildren().addAll(remove, clearForm, goBack);

        AnchorPane floatingContainer = new AnchorPane();
        AnchorPane.setBottomAnchor(actionSection, 0.0);
        AnchorPane.setRightAnchor(actionSection, 0.0);
        floatingContainer.setPrefWidth(200);
        floatingContainer.getChildren().addAll(actionSection);

        // The Footer
        VBox mainFooterSec = new VBox();
        mainFooterSec.setAlignment(Pos.CENTER);
        mainFooterSec.setPadding(new Insets(20, 0, 20, 20));
        mainFooterSec.setStyle("-fx-background-color: lightGray");
        HBox bottomSection = new HBox();

        bottomSection.setPrefHeight(30);
        bottomSection.setSpacing(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setMaxWidth(Double.MAX_VALUE);

        HBox balanceSec = new HBox();
        balanceSec.setAlignment(Pos.CENTER);
        balanceSec.setPadding(new Insets(10, 0, 10, 0));
        balanceSec.getChildren().addAll(balanceTxt, balance);

        bottomSection.getChildren().addAll(discountForAll, totalCostTxt, totalCost, totalDiscountTxt, totalDiscount, grandTotalTxt, grandTotal, fundTxt, fund);
        mainFooterSec.getChildren().addAll(bottomSection, balanceSec, completeBtn);

        headerSection.getChildren().addAll(heading, dateHolder, inputVerticalSec);
        inputSection.getChildren().addAll(amount, discount, itemComboBox);
        addButtonSection.getChildren().addAll(addButton);
        inputVerticalSec.getChildren().addAll(inputSection, addButtonSection);

        // Assigning each sections to the main section
        mainLayout.setTop(headerSection);
        mainLayout.setCenter(floatingContainer);
        mainLayout.setBottom(mainFooterSec);
    }

    public BorderPane getLayout() {
        return mainLayout;
    }

}

