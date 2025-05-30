package com.example.inventorymanagementsystem.view;
import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.Item;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.ItemStatus;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.HoverTooltip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;


public class Checkout implements ThemeObserver {
    @FXML private TableView<CheckoutItem> tableView;
    @FXML private TableColumn<CheckoutItem, Integer> colId;
    @FXML private TableColumn<CheckoutItem, Integer> colCustomerId;
    @FXML private TableColumn<CheckoutItem, Integer> colAmount;
    @FXML private TableColumn<CheckoutItem, Double> colPrice;
    @FXML private TableColumn<CheckoutItem, String> colDate;

    private BorderPane mainLayout;
    private Connection dbConnection;// The main container
    ComboBox<ItemDetail> itemComboBox;
    private final ObservableList<CheckoutItem> itemList = FXCollections.observableArrayList();
    private Label totalCost = new Label();
    // For the calculations
    private double selectedItmPrice = 0.0;
    private int quantityValue;
    private double discountValue;
    private double totalCostValue = 0.0;
    private double cumulativeTotalCost = 0;
    private double cumulativeTotalDiscount = 0;
    private double cumulativeGrandTotal = 0;
    private double cumulativeReceivedFund = 0;
    private Set<Integer> processedItemIds = new HashSet<>();
    private boolean checkoutJustCompleted = false;

    public Checkout () {
        dbConnection = Connection.getInstance();
        // The main container
        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        // The header container
        VBox headerSection = new VBox(5);

        // The Navbar section
        StackPane navbar = new StackPane();
        VBox.setMargin(navbar, new Insets(2, 0, 15, 0));
        navbar.setPadding(new Insets(18.0, 0, 18.0, 0));
        navbar.setMaxWidth(Double.MAX_VALUE);
        navbar.getStyleClass().add("nav-bar");
        navbar.setStyle("-fx-background-radius: 10px;");
        VBox.setMargin(navbar, new Insets(2, 0, 15, 0));

        Text heading = new Text("Checkout Panel");
        heading.setTextAlignment(TextAlignment.CENTER);
        heading.getStyleClass().add("heading-texts");

        Text dateTime = new Text();
        dateTime.setStyle("-fx-font-size: 19px; -fx-font-weight: bold;");
        dateTime.getStyleClass().add("paragraph-texts");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss");
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    LocalDateTime now = LocalDateTime.now();
                    dateTime.setText(now.format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();

        AnchorPane clockPane = new AnchorPane(dateTime);
        AnchorPane.setRightAnchor(dateTime, 20.0);
        AnchorPane.setTopAnchor(dateTime, 0.0);

        navbar.getChildren().addAll(heading, clockPane);

        // Input area
        VBox inputVerticalSec = new VBox();
        inputVerticalSec.setAlignment(Pos.TOP_LEFT);

        Text itemTxt = new Text("Item Information");
        itemTxt.getStyleClass().add("paragraph-texts");

        itemComboBox = new ComboBox<>();
        List<ItemDetail> itemDetails = dbConnection.getItemDetails();
        if (itemDetails.isEmpty()) {
            System.out.println("No items found!");
        } else {
            itemComboBox.getItems().setAll(itemDetails);
        }

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

        // SINGLE selection listener (remove duplicates)
        itemComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedItmPrice = newValue.getPrice();
                System.out.println("Selected item price: " + selectedItmPrice);
            }
        });
        itemComboBox.setMaxWidth(Double.MAX_VALUE);
        itemComboBox.setPromptText("Select The Item");
        itemComboBox.getStyleClass().add("default-dropdowns");

        TextField amount = new TextField();
        amount.setPromptText("Type the quantity");
        amount.getStyleClass().add("default-text-areas");
        TextField discount = new TextField();
        discount.setPromptText("Type the discount");
        discount.getStyleClass().add("default-text-areas");
        Button addButton = new Button("📃 Add to List");
        addButton.getStyleClass().add("add-button");
        addButton.setMaxWidth(Double.MAX_VALUE);

        Region theSpace = new Region();
        theSpace.setMinHeight(15);

        Text customerTxt = new Text("Customer Information");
        customerTxt.getStyleClass().add("paragraph-texts");
        TextField firstName = new TextField();
        firstName.setPromptText("First Name");
        firstName.getStyleClass().add("default-text-areas");
        TextField lastName = new TextField();
        lastName.setPromptText("Last Name");
        lastName.getStyleClass().add("default-text-areas");
        TextField phone = new TextField();
        phone.setPromptText("Phone Number");
        phone.getStyleClass().add("default-text-areas");
        TextField eMail = new TextField();
        eMail.setPromptText("E-Mail(Optional)");
        eMail.getStyleClass().add("default-text-areas");

        HBox addCustomerSec = new HBox();
        addCustomerSec.setSpacing(5.5);
        Button clearForm = new Button("❌ Clear Form");
        clearForm.getStyleClass().add("clear-button");
        clearForm.setOnAction(actionEvent -> {
            amount.clear();
            discount.clear();
            firstName.clear();
            lastName.clear();
            phone.clear();
            eMail.clear();
        });
        // The tooltip
        HoverTooltip clearFormsTooltip = new HoverTooltip("Clear the forms");
        clearFormsTooltip.attachTo(clearForm);

        Button addCustomer = new Button("➕ Add Customer");
        addCustomer.getStyleClass().add("add-button");

        addCustomer.setOnAction(e -> {
            String firstNameField = firstName.getText();
            String lastNameField = lastName.getText();
            String phoneField = phone.getText();
            String emailField = eMail.getText();

            LocalDateTime now = LocalDateTime.now();

            Connection connection = new Connection();
            connection.addCustomers(firstNameField, lastNameField, phoneField, emailField, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

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

        TableColumn<CheckoutItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CheckoutItem, String> sizeCol = new TableColumn<>("Item Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("itemSize"));

        TableColumn<CheckoutItem, String> colorCol = new TableColumn<>("Item Color");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("itemColor"));

        TableColumn<CheckoutItem, Integer> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<CheckoutItem, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<CheckoutItem, Integer> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<CheckoutItem, String> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("itemTotalCost"));

        mainTable.getColumns().addAll(nameCol, sizeCol, colorCol, amountCol, priceCol, sellingPriceCol, totalCostCol);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ObservableList<CheckoutItem> itemList = dbConnection.getCheckoutItems();

        mainTable.setItems(itemList);

        mainTable.setMaxWidth(Double.MAX_VALUE);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainTable.prefWidthProperty().bind(mainLayout.widthProperty());

        // Bottom Section
        TextField discountForAll = new TextField();
        discountForAll.setPromptText("Apply discount for all");
        discountForAll.getStyleClass().add("default-text-areas");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();

            if (newText.isEmpty()) return change;

            if (newText.matches("\\d*%?")) {
                if (newText.chars().filter(ch -> ch == '%').count() <= 1 &&
                        (!newText.contains("%") || newText.endsWith("%"))) {
                    return change;
                }
            }
            return null;
        };
        discountForAll.setTextFormatter(new TextFormatter<>(filter));
        discountForAll.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                String txt = discountForAll.getText();
                if (txt != null && !txt.isEmpty() && !txt.endsWith("%")) {
                    discountForAll.setText(txt + "%");
                }
            }
        });

        TextField receivedFund = new TextField();
        receivedFund.setPromptText("Received Fund");
        receivedFund.getStyleClass().add("default-text-areas");

        receivedFund.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            String digitsOnly = newValue.replaceAll("[^\\d]", "");

            if (digitsOnly.isEmpty()) {
                receivedFund.setText("");
                return;
            }
            try {
                long number = Long.parseLong(digitsOnly);
                String formatted = String.format("$%,d", number);

                if (!formatted.equals(newValue)) {
                    receivedFund.setText(formatted);
                    receivedFund.positionCaret(formatted.length());
                }
            } catch (NumberFormatException e) {
                receivedFund.setText(oldValue);
                receivedFund.positionCaret(oldValue.length());
            }
        });

        Text totalCostTxt = new Text("Total Cost:");
        Label totalCost = new Label("_");
        totalCostTxt.getStyleClass().add("information-texts");
        totalCost.getStyleClass().add("information-label");

        Text totalDiscountTxt = new Text("Total Discount:");
        Label totalDiscount = new Label("_");
        totalDiscountTxt.getStyleClass().add("information-texts");
        totalDiscount.getStyleClass().add("information-label");

        Text grandTotalTxt = new Text("Grand Total:");
        Label grandTotal = new Label("_");
        grandTotalTxt.getStyleClass().add("information-texts");
        grandTotal.getStyleClass().add("information-label");

        Text balanceTxt = new Text("Due Balance:");
        balanceTxt.setStyle("-fx-font-weight: bold; -fx-font-size: 19px;");
        Label balance = new Label("_");
        balance.setStyle("-fx-font-weight: bold; -fx-font-size: 19px;");
        balanceTxt.getStyleClass().add("information-texts");
        balance.getStyleClass().add("information-label");
        Button checkOutButton = new Button("Check Out");
        checkOutButton.getStyleClass().add("default-buttons");

        /*
        *   This is the action of the adding button for items and this piece of code is here cause, to access for all the values in the above code.
        */
        Text stockMessage = new Text("");
        stockMessage.setVisible(false);
        stockMessage.setManaged(false);
        stockMessage.setTextAlignment(TextAlignment.CENTER);
        stockMessage.setFont(Font.font("System"));
        stockMessage.getStyleClass().add("stock-message");
        StackPane stockMessageContainer = new StackPane();
        stockMessageContainer.setMaxWidth(Double.MAX_VALUE);
        Region space = new Region();
        space.setPrefHeight(40);
        stockMessageContainer.getChildren().addAll(space, stockMessage);

        addButton.setOnAction(e -> {
            ItemDetail selectedItemDetail = itemComboBox.getSelectionModel().getSelectedItem();
            int remainingAmount =  selectedItemDetail.getRemainingQty();

            if (selectedItemDetail == null || amount.getText().isEmpty()) {
                System.out.println("Please select an item and enter the quantity.");
                return;
            }
            if (selectedItemDetail != null) {
                int itemTotalCost = (int) (selectedItemDetail.getSellingPrice() * quantityValue);
                selectedItemDetail.setItemTotalCost(String.valueOf(itemTotalCost));
            }

            if (checkoutJustCompleted) {
                itemList.clear();
                mainTable.refresh();
                cumulativeTotalCost= 0;
                cumulativeTotalDiscount = 0;
                cumulativeGrandTotal = 0;
                cumulativeReceivedFund = 0;

                totalCost.setText("-");
                totalDiscount.setText("-");
                grandTotal.setText("-");
                balance.setText("");
                checkoutJustCompleted = false;
            }

            try {
                int quantityValue = Integer.parseInt(amount.getText().trim());
            if ((remainingAmount > 0) && quantityValue <= remainingAmount) {
                stockMessage.setText("");
                int discountValue = 0;
                if (!discount.getText().trim().isEmpty()) {
                    discountValue = Integer.parseInt(discount.getText().trim());
                }
                int price = selectedItemDetail.getPrice();
                double sellingPrice = selectedItemDetail.getSellingPrice();
                double totalCostValue = selectedItemDetail.getSellingPrice() * quantityValue ;
                double totalDiscountValue = totalCostValue * ((double) discountValue / 100);
                double grandTotalValue = totalCostValue - totalDiscountValue;

                dbConnection.storeSales(
                        selectedItemDetail.getId(),
                        selectedItemDetail.getItemHasSizeID(),
                        quantityValue,
                        price,
                        1
                );
                String itemTotalCostStr = String.valueOf(selectedItemDetail.getSellingPrice() * quantityValue);

                CheckoutItem newItem = new CheckoutItem(
                        selectedItemDetail.getName(),
                        selectedItemDetail.getSize(),
                        selectedItemDetail.getItemColor(),
                        quantityValue,
                        price,
                        sellingPrice,
                        itemTotalCostStr
                );

                newItem.setItemHasSizeId(selectedItemDetail.getItemHasSizeID());
                itemList.add(newItem);
                checkOutButton.setDisable(false);
                totalCost.setText("-");
                totalDiscount.setText("-");
                grandTotal.setText("-");
                balance.setText("-");

                mainTable.refresh();
                itemComboBox.getSelectionModel().clearSelection();

                cumulativeTotalCost += totalCostValue;
                cumulativeTotalDiscount += totalDiscountValue;
                cumulativeGrandTotal += grandTotalValue;

                totalCost.setText("$" + cumulativeTotalCost);
                totalDiscount.setText("$" + cumulativeTotalDiscount);
                grandTotal.setText("$" + cumulativeGrandTotal);

                stockMessage.setVisible(false);
                stockMessage.setManaged(false);
                addButton.setDisable(false);
                amount.clear();
                discount.clear();
            }
            else {
                stockMessageContainer.setAlignment(Pos.CENTER);
                space.setMinHeight(10);
                addButton.setDisable(true);
                stockMessage.setText("Not enough stock!");
                stockMessage.setTextAlignment(TextAlignment.CENTER);
                stockMessage.getStyleClass().add("stock-error-message");
                inputSection.setMaxWidth(Double.MAX_VALUE);
                stockMessageContainer.getChildren().addAll(space, stockMessage);
                inputSection.getChildren().addAll(space, stockMessageContainer);
                stockMessage.setVisible(true);
                stockMessage.setManaged(true);
            }

            } catch (NumberFormatException ex) {
                System.out.println("Invalid quantity entered.");
            }
        });

        amount.textProperty().addListener((obs, oldVal, newVal) -> {
            ItemDetail selectedItemDetail = itemComboBox.getSelectionModel().getSelectedItem();

            if (selectedItemDetail != null && !newVal.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(newVal.trim());
                    int remainingQty = selectedItemDetail.getRemainingQty();

                    if (quantity > 0 && quantity <= remainingQty) {
                        stockMessage.setVisible(false);
                        stockMessage.setManaged(false);
                        addButton.setDisable(false);
                    } else {
                        stockMessage.setText("Not enough stock!");
                        stockMessage.setVisible(true);
                        stockMessage.setManaged(true);
                        addButton.setDisable(true);
                    }

                } catch (NumberFormatException e) {
                    stockMessage.setText("Invalid input!");
                    stockMessage.setVisible(true);
                    stockMessage.setManaged(true);
                    addButton.setDisable(true);
                }
            } else {
                // Empty or no item selected
                addButton.setDisable(true);
            }
        });

        amount.textProperty().addListener((observable, oldValue, newValue) -> {
            ItemDetail selectedItemDetail = itemComboBox.getSelectionModel().getSelectedItem();
            int typedAmount = Integer.parseInt(amount.getText().trim());
            int remainingAmount = selectedItemDetail.getRemainingQty();

            if (typedAmount >= remainingAmount) {
                stockMessage.setText("Not enough stock!");

                if (!(inputSection.getChildren().contains(stockMessageContainer))) {
                    inputSection.getChildren().add(stockMessageContainer);
                }
            } else {
                inputSection.getChildren().remove(stockMessageContainer);
            }
        });

        checkOutButton.setOnAction(e -> {
            try {
                if (itemList.isEmpty()) {
                    System.out.println("No items in the checkout list!");
                    return;
                }

                for (CheckoutItem item : itemList) {
                    if (!processedItemIds.contains(item.getitemHasSizeId())) {
                        String updateQuery = "UPDATE item_has_size SET remaining_qty = remaining_qty - ? WHERE id = ?";
                        try (PreparedStatement pstmt = dbConnection.getJdbcConnection().prepareStatement(updateQuery)) {
                            pstmt.setInt(1, item.getAmount());
                            pstmt.setInt(2, item.getitemHasSizeId());
                            int rowsUpdated = pstmt.executeUpdate();
                            if (rowsUpdated > 0) {
                                System.out.println("Remaining quantity updated for item: " + item.getName());
                                processedItemIds.add(item.getitemHasSizeId());
                            }
                        } catch (SQLException ex) {
                            System.out.println("Database error while updating quantity: " + ex.getMessage());
                        }
                    }
                }

                String receivedInput = receivedFund.getText();
                double receivedFundValue = 0.0;
                if (receivedInput != null && !receivedInput.isEmpty()) {
                    String cleaned = receivedInput.replaceAll("[$,\\s]", "");
                    if (!cleaned.isEmpty()) {
                        receivedFundValue = Double.parseDouble(cleaned);
                    }
                }

                cumulativeReceivedFund = receivedFundValue;

                double discountValue = 0.0;
                String  discountText = discountForAll.getText().trim();

                if (discountText.endsWith("%")) {
                    String numberPart = discountText.substring(0, discountText.length() -1);
                    if (!numberPart.isEmpty()) {
                        discountValue = Double.parseDouble(numberPart);
                    }
                } else if (!discountText.isEmpty()) {
                    discountValue = Double.parseDouble(discountText);
                }

                double totalDiscountValue = cumulativeTotalCost * (discountValue / 100.0);
                double adjustedTotal = cumulativeTotalCost - totalDiscountValue;

                totalDiscount.setText("$" + totalDiscountValue);
                grandTotal.setText("$" + adjustedTotal);

                cumulativeTotalDiscount = totalDiscountValue;
                cumulativeGrandTotal = adjustedTotal;

                double dueBalanceValue = cumulativeReceivedFund - adjustedTotal;
                balance.setText("$" + dueBalanceValue);

                mainTable.refresh();
                discountForAll.clear();
                receivedFund.clear();

                if (!itemList.isEmpty()) {
                    checkOutButton.setDisable(true);
                    checkoutJustCompleted = true;
                }

            } catch (NumberFormatException ex) {
                System.out.println("Invalid input in received fund or discount field.");
            }
        });

        // The floating section in the right_side
        Button remove = new Button("❌ Remove All");
        remove.getStyleClass().add("clear-button");
        remove.setOnAction(e -> {
            totalCost.setText("_");
            totalDiscount.setText("_");
            grandTotal.setText("_");
            balance.setText("_");
            mainTable.getItems().clear();
        });
        // The tooltip
        HoverTooltip removeItemsTooltip = new HoverTooltip("Remove all items from the checkout list.");
        removeItemsTooltip.attachTo(remove);

        VBox actionSection = new VBox();
        actionSection.setSpacing(10);
        actionSection.setPrefWidth(200);
        actionSection.setAlignment(Pos.TOP_RIGHT);
        actionSection.setPadding(new Insets(0, 0, 10, 0));
        actionSection.getChildren().addAll(remove);

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
        mainFooterSec.getStyleClass().add("footer-section");
        mainFooterSec.setPadding(new Insets(40, 0, 50, 20));
        HBox bottomSection = new HBox();

        bottomSection.setPrefHeight(30);
        bottomSection.setSpacing(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setMaxWidth(Double.MAX_VALUE);

        HBox balanceSec = new HBox();
        balanceSec.setAlignment(Pos.CENTER);
        balanceSec.setPadding(new Insets(19, 0, 10, 0));
        balanceSec.setSpacing(10);
        balanceSec.getChildren().addAll(balanceTxt, balance, checkOutButton);

        bottomSection.getChildren().addAll(discountForAll, receivedFund, totalCostTxt, totalCost, totalDiscountTxt, totalDiscount, grandTotalTxt, grandTotal);
        mainFooterSec.getChildren().addAll(bottomSection, balanceSec);
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

    @Override
    public void lightTheme() {
        mainLayout.setStyle("-fx-background-color: white; ");
        mainLayout.getStylesheets().clear();
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/lightTheme.css"))
        );
    }

    @Override
    public void darkTheme() {
        mainLayout.setStyle("-fx-background-color: #222; ");
        mainLayout.getStylesheets().clear();
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/darkTheme.css"))
        );
    }
}
