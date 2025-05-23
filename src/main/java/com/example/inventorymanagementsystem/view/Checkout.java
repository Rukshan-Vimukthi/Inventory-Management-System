package com.example.inventorymanagementsystem.view;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.state.Data;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

import java.util.List;


public class Checkout {
    private BorderPane mainLayout;

    public Checkout () {
        // The main container
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));

        // The header container
        VBox headerSection = new VBox(5);
        headerSection.setSpacing(10);
        headerSection.setAlignment(Pos.TOP_CENTER);
        Label heading = new Label("Checkout Panel");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        HBox.setHgrow(heading, Priority.ALWAYS);
        heading.setPadding(new Insets(20, 0, 0, 0));
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

        Text itemTxt = new Text("Item Information");

        ComboBox itemComboBox = new ComboBox<>();
        List<ItemDetail> allItems = Data.getInstance().getItemDetails();
        for (ItemDetail item : allItems) {
            itemComboBox.getItems().add(item.nameProperty().get());
        }
        itemComboBox.setMaxWidth(Double.MAX_VALUE);
        itemComboBox.setPromptText("Select The Item");

        TextField amount = new TextField();
        amount.setPromptText("Type the quantity");
        TextField discount = new TextField();
        discount.setPromptText("Type the discount");
        Button addButton = new Button("Add to List");

        Region theSpace = new Region();
        theSpace.setMinHeight(15);

        Text userTxt = new Text("User Information");
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField phone = new TextField();
        phone.setPromptText("Phone Number");
        TextField eMail = new TextField();
        eMail.setPromptText("E-Mail");

        Button clearForm = new Button("Clear Form");

        // For the adding items section
        VBox inputSection = new VBox();
        inputSection.setSpacing(10);
        inputSection.setPadding(new Insets(10, 50, 0, 0));
        inputSection.setAlignment(Pos.TOP_LEFT);
        inputSection.setMaxWidth(Double.MAX_VALUE);
        inputSection.setMaxWidth(250);
        inputSection.setMinWidth(250);

        // The Table Section in the Center
        HBox centerContainer = new HBox();
        TableView mainTable = new TableView();

        TableColumn<ItemDetail, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ItemDetail, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ItemDetail, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<ItemDetail, Double> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<ItemDetail, Integer> stockCol = new TableColumn<>("Stock ID");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stockID"));

        mainTable.getColumns().addAll(idCol, nameCol, priceCol, sellingPriceCol, stockCol);
        ObservableList<ItemDetail> itemList = FXCollections.observableArrayList(
                com.example.inventorymanagementsystem.db.Connection.getInstance().getItemDetails()
        );
        mainTable.setItems(itemList);

        mainTable.setMaxWidth(Double.MAX_VALUE);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainTable.prefWidthProperty().bind(mainLayout.widthProperty());

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
        floatingContainer.setPadding(new Insets(10, 0, 0, 0));

        // The Footer
        VBox wholeBottomSec = new VBox();
        VBox mainFooterSec = new VBox();
        mainFooterSec.setAlignment(Pos.CENTER);
        mainFooterSec.setPadding(new Insets(20, 0, 20, 20));
        mainFooterSec.setStyle("-fx-background-color: lightGray; -fx-font-size: 16px;");
        HBox bottomSection = new HBox();

        bottomSection.setPrefHeight(30);
        bottomSection.setSpacing(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setMaxWidth(Double.MAX_VALUE);

        HBox balanceSec = new HBox();
        balanceSec.setAlignment(Pos.CENTER);
        balanceSec.setPadding(new Insets(10, 0, 10, 0));
        balanceSec.setSpacing(10);
        balanceSec.setStyle("-fx-font-weight: bold;");
        balanceSec.getChildren().addAll(balanceTxt, balance);

        bottomSection.getChildren().addAll(discountForAll, totalCostTxt, totalCost, totalDiscountTxt, totalDiscount, grandTotalTxt, grandTotal, fundTxt, fund);
        mainFooterSec.getChildren().addAll(bottomSection, balanceSec, completeBtn);
        wholeBottomSec.getChildren().addAll(floatingContainer, mainFooterSec);

        headerSection.getChildren().addAll(heading, dateHolder, inputVerticalSec);
        inputSection.getChildren().addAll(itemTxt, itemComboBox, amount, discount, addButton, theSpace, userTxt, firstName, lastName, phone, eMail, clearForm);
        inputVerticalSec.getChildren().addAll(inputSection);

        centerContainer.getChildren().addAll(inputVerticalSec, mainTable);

        // Assigning each sections to the main section
        mainLayout.setTop(headerSection);
        mainLayout.setCenter(centerContainer);
        mainLayout.setBottom(wholeBottomSec);

    }

    public BorderPane getLayout() {
        return mainLayout;
    }

}

