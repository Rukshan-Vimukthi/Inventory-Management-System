package com.example.inventorymanagementsystem.view;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.state.Data;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
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
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class Checkout {
    private BorderPane mainLayout;
    private Connection dbConnection;// The main container
    ComboBox<ItemDetail> itemComboBox;
    private Label totalCost = new Label();
//  For the calculations
    private double selectedItmPrice = 0.0;
    private int quantityValue;
    private double discountValue;
    private double totalCostValue = 0.0;
    private double cumulativeTotalCost = 0;
    private double cumulativeTotalDiscount = 0;
    private double cumulativeGrandTotal = 0;
    private double cumulativeReceivedFund = 0;

    public Checkout () {
        dbConnection = Connection.getInstance(); // Get the database instance
        // The main container
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));

        // The header container
        VBox headerSection = new VBox(5);

        // The Navbar section
        HBox navbar = new HBox();
        navbar.setSpacing(30);
        navbar.setPadding(new Insets(18.0, 0, 18.0, 0));
        navbar.setMaxWidth(Double.MAX_VALUE);
        navbar.setAlignment(Pos.CENTER);
        navbar.setStyle("-fx-background-color: darkGray;");
        VBox.setMargin(navbar, new Insets(2, 0, 15, 0));

        Label heading = new Label("Checkout Panel");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));

        Text dateTime = new Text();
        dateTime.setStyle("-fx-font-size: 15px;");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalDateTime now = LocalDateTime.now();
                    dateTime.setText(now.format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        navbar.getChildren().addAll(heading, dateTime);

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        // Input area
        VBox inputVerticalSec = new VBox();
        inputVerticalSec.setAlignment(Pos.TOP_LEFT);

        Text itemTxt = new Text("Item Information");

        itemComboBox = new ComboBox<>();
        itemComboBox.getItems().addAll(dbConnection.getItemDetails());

        itemComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ItemDetail item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nameProperty().get());
            }
        });
        itemComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ItemDetail item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nameProperty().get());
            }
        });

        itemComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) { // Ensure an item is selected
                int sizeId = dbConnection.getSizes().get(0).getId();
                int statusId = dbConnection.getStatus().get(0).getId();
                double selectedItemPrice = newValue.getPrice();
                System.out.println("Selected item price: " + selectedItemPrice);

                // Store price in a variable for future calculations
                selectedItmPrice = selectedItemPrice;
            }
        });

        itemComboBox.setMaxWidth(Double.MAX_VALUE);
        itemComboBox.setPromptText("Select The Item");

        TextField amount = new TextField();
        amount.setPromptText("Type the quantity");
        TextField discount = new TextField();
        discount.setPromptText("Type the discount");
        Button addButton = new Button("Add to List");
//      The action for this button is after the bottom section and also the reason is there

        Region theSpace = new Region();
        theSpace.setMinHeight(15);

        Text customerTxt = new Text("Customer Information");
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        TextField phone = new TextField();
        phone.setPromptText("Phone Number");
        TextField eMail = new TextField();
        eMail.setPromptText("E-Mail");

        HBox addCustomerSec = new HBox();
        addCustomerSec.setSpacing(5.5);
        Button clearForm = new Button("Clear Form");
        clearForm.setOnAction(actionEvent -> {
            amount.clear();
            discount.clear();
            firstName.clear();
            lastName.clear();
            phone.clear();
            eMail.clear();
        });
        Button addCustomer = new Button("Add Customer");

        addCustomer.setOnAction(e -> {
            String firstNameField = firstName.getText();
            String lastNameField = lastName.getText();
            String phoneField = phone.getText();
            String emailField = eMail.getText();

            Connection connection = new Connection();
            connection.addCustomers(firstNameField, lastNameField, phoneField, emailField);

            // Clear input fields
            firstName.clear();
            lastName.clear();
            phone.clear();
            eMail.clear();
        });

        addCustomerSec.getChildren().addAll(addCustomer, clearForm);

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
        TextField discountForAll = new TextField();
        discountForAll.setPromptText("Apply discount for all");

        TextField receivedFund = new TextField();
        receivedFund.setPromptText("Received Fund");

        Text totalCostTxt = new Text("Total Cost:");
        Label totalCost = new Label();

        Text totalDiscountTxt = new Text("Total Discount:");
        Label totalDiscount = new Label();

        Text grandTotalTxt = new Text("Grand Total:");
        Label grandTotal = new Label("$40");

        Text balanceTxt = new Text("Due Balance:");
        Label balance = new Label();
        Button checkOutButton = new Button("Check Out");

        /*
        *   This is the action of the adding button for items and this piece of code is here
        *   cause, to access for all the values in the above code.
        * */
        addButton.setOnAction(e -> {
            try {
                int quantityValue = Integer.parseInt(amount.getText().trim());
                double discountValue = Double.parseDouble(discount.getText().trim());
                double receivedFundValue = Double.parseDouble(receivedFund.getText().trim());

                double totalCostValue = selectedItmPrice * quantityValue ;
                double totalDiscountValue = quantityValue * discountValue;
                double grandTotalValue = totalCostValue - totalDiscountValue;

                // Accumulate values,
                cumulativeTotalCost += totalCostValue;
                cumulativeTotalDiscount += totalDiscountValue;
                cumulativeGrandTotal += grandTotalValue;
                cumulativeReceivedFund += receivedFundValue;

                double dueBalanceValue = cumulativeReceivedFund - cumulativeGrandTotal;

                totalCost.setText("$" + cumulativeTotalCost);
                totalDiscount.setText("$" + cumulativeTotalDiscount);
                grandTotal.setText("$" + cumulativeGrandTotal);
                balance.setText("$" + dueBalanceValue);

            } catch (NumberFormatException ex) {
                System.out.println("Please enter valid numbers!");
            }
        });

        checkOutButton.setOnAction(e -> {
            ItemDetail selectedItem = itemComboBox.getValue();
            String quantity = amount.getText();
            String discountText = discount.getText();

            if (selectedItem == null || quantity.isEmpty() || discountText.isEmpty()){
                System.out.println("Please fill all the fields");
                return;
            }
            int itemId = selectedItem.getId();
            int sizeId = dbConnection.getSizes().get(0).getId();
            int statusId = dbConnection.getStatus().get(0).getId();
            double price = selectedItem.priceProperty().get();
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int quantityDetail = Integer.parseInt(quantity.trim());
            double discountDetail = Double.parseDouble(discountText.trim());

            // Sending the details to the table
            // Fetch additional IDs required for database insertion
            dbConnection.storeSales(1, sizeId, quantityDetail, (int) price, statusId);
        });

        // The floating section in the right_side
        Button remove = new Button("Remove All");
        Button goBack = new Button("Cancel");

        VBox actionSection = new VBox();
        actionSection.setSpacing(10);
        actionSection.setPrefWidth(200);
        actionSection.setAlignment(Pos.TOP_RIGHT);
        actionSection.setPadding(new Insets(0, 0, 10, 0));
        actionSection.getChildren().addAll(remove, goBack);

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

        bottomSection.getChildren().addAll(discountForAll, receivedFund, totalCostTxt, totalCost, totalDiscountTxt, totalDiscount, grandTotalTxt, grandTotal);
        mainFooterSec.getChildren().addAll(bottomSection, balanceSec, checkOutButton);
        wholeBottomSec.getChildren().addAll(floatingContainer, mainFooterSec);

        headerSection.getChildren().addAll(navbar, inputVerticalSec);
        inputSection.getChildren().addAll(itemTxt, itemComboBox, amount, discount, addButton, theSpace, customerTxt, firstName, lastName, phone, eMail, addCustomerSec);
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
