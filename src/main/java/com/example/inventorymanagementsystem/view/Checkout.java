package com.example.inventorymanagementsystem.view;
import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.Item;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.ItemStatus;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.CurrencyCellFactory;
import com.example.inventorymanagementsystem.view.components.HoverTooltip;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Checkout implements ThemeObserver {
    @FXML
    private TableView<CheckoutItem> tableView;
    @FXML
    private TableColumn<CheckoutItem, Integer> colId;
    @FXML
    private TableColumn<CheckoutItem, Integer> colCustomerId;
    @FXML
    private TableColumn<CheckoutItem, Integer> colAmount;
    @FXML
    private TableColumn<CheckoutItem, Double> colPrice;
    @FXML
    private TableColumn<CheckoutItem, String> colDate;

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
    private PauseTransition stockMessageTimer;
    private PauseTransition messageTimer;
    private PauseTransition itemsDeletingMsgTimer;
    private double cumulativeTotalCost = 0;
    private double cumulativeTotalDiscount = 0;
    private double cumulativeGrandTotal = 0;
    private double cumulativeReceivedFund = 0;
    private Set<Integer> processedItemIds = new HashSet<>();
    private boolean checkoutJustCompleted = false;
    private TextField receivedFund;
    private TextField discountForAll;
    private CheckoutItem selectedCheckoutItem = null;
    Label totalDiscount;
    TableView<CheckoutItem> mainTable;
    Label grandTotal;
    Label balance;
    Text stockMessage;
    Text customerMessage;
    VBox inputVerticalSec;
    Label itemMessageContainer;
    TextField discount;
    TextField amount;
    Button checkOutButton;

    public Checkout() {
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
        inputVerticalSec = new VBox();
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
            private final Circle colorCircle = new Circle(6);
            private final HBox hbox = new HBox(10);
            private final Label textLabel = new Label();

            {
                hbox.getChildren().addAll(textLabel, colorCircle);
                hbox.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(textLabel, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(ItemDetail item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    textLabel.setText(item.getName() + " (" + item.getSize() + ")");
                    colorCircle.setFill(Color.web(item.getItemColor()));
                    colorCircle.setStroke(Color.GRAY); // Optional: slight border
                    setGraphic(hbox);
                }
            }
        });

        itemComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ItemDetail item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.nameProperty().get() + " (" + item.getSize() + ")");
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

        amount = new TextField();
        amount.setPromptText("Type the quantity");
        amount.getStyleClass().add("default-text-areas");
        discount = new TextField();
        discount.setPromptText("Type the discount");
        discount.getStyleClass().add("default-text-areas");
        Button addButton = new Button("📃 Add to List");
        addButton.getStyleClass().add("add-button");
        addButton.setMaxWidth(Double.MAX_VALUE);

        // Focusing the next text area
        itemComboBox.setOnAction(e -> amount.requestFocus());
        amount.setOnAction(e -> discount.requestFocus());
        discount.setOnAction(e -> addButton.fire());

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
        Button addCustomer = new Button("➕ Add Customer");
        addCustomer.getStyleClass().add("add-button");

        // Focusting the next text areas
        firstName.setOnAction(e -> lastName.requestFocus());
        lastName.setOnAction(e -> phone.requestFocus());
        phone.setOnAction(e -> eMail.requestFocus());
        eMail.setOnAction(e -> addCustomer.requestFocus());

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

        addCustomer.setOnAction(e -> {
            String firstNameField = firstName.getText().trim();
            String lastNameField = lastName.getText().trim();
            String phoneField = phone.getText().trim();
            String emailField = eMail.getText().trim();

            if (firstNameField.isEmpty() || lastNameField.isEmpty() || phoneField.isEmpty()) {
                showMessage("Please fill in all required fields.", Color.ORANGE);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            Connection connection = new Connection();

            String result = connection.addCustomers(
                    firstNameField,
                    lastNameField,
                    phoneField,
                    emailField,
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            );

            // Show message
            if (result.equals("Customer already exists!")) {
                customerMessage.setText(result);
                showMessage(result, Color.RED);
            } else if (result.equals("Customer added successfully!")) {
                showMessage(result, Color.GREEN);
                // Clear input fields
                firstName.clear();
                lastName.clear();
                phone.clear();
                eMail.clear();
            }
        });

        addCustomerSec.setPadding(new Insets(0, 0, 20, 0));
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
        mainTable = new TableView<>();

        TableColumn<CheckoutItem, String> nameCol = new TableColumn<>("Item");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<CheckoutItem, String> sizeCol = new TableColumn<>("Item Size");
        sizeCol.setCellValueFactory(cellData -> cellData.getValue().itemSizeProperty());

        TableColumn<CheckoutItem, String> colorCol = new TableColumn<>("Item Color");
        colorCol.setCellValueFactory(cellData -> cellData.getValue().itemColorProperty());

        colorCol.setCellFactory(column -> new TableCell<>() {
            private final Circle colorCircle = new Circle(8);

            @Override
            protected void updateItem(String colorCode, boolean empty) {
                super.updateItem(colorCode, empty);
                if (empty || colorCode == null || colorCode.isBlank()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    colorCircle.setFill(Color.web(colorCode));
                    colorCircle.setStroke(Color.GRAY);
                    setGraphic(colorCircle);
                    setText(null);
                }
            }
        });

        TableColumn<CheckoutItem, Integer> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        TableColumn<CheckoutItem, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        priceCol.setCellFactory(CurrencyCellFactory.withPrefix("$"));

        TableColumn<CheckoutItem, Double> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(cellData -> cellData.getValue().sellingPriceProperty().asObject());
        sellingPriceCol.setCellFactory(CurrencyCellFactory.withPrefix("$"));

        TableColumn<CheckoutItem, String> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("itemTotalCost"));
        totalCostCol.setCellFactory(CurrencyCellFactory.withPrefix("$"));

        TableColumn<CheckoutItem, Double> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(cellData -> cellData.getValue().discountProperty().asObject());

        mainTable.getColumns().addAll(nameCol, sizeCol, colorCol, amountCol, priceCol, sellingPriceCol, totalCostCol);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainTable.getColumns().addAll(discountCol);

        ObservableList<CheckoutItem> itemList = dbConnection.getCheckoutItems(); // contains full item objects
        mainTable.setItems(itemList);

        ObservableList<String> data = FXCollections.observableArrayList();

        mainTable.setEditable(true);
        mainTable.setMaxWidth(Double.MAX_VALUE);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mainTable.prefWidthProperty().bind(mainLayout.widthProperty());

        mainTable.setRowFactory(tv -> {
            TableRow<CheckoutItem> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    if (row.isSelected()) {
                        tableView.getSelectionModel().clearSelection(row.getIndex());
                    } else {
                        tableView.getSelectionModel().select(row.getIndex());
                    }
                }
            });

            return row;
        });


        mainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedItem) -> {
            if (selectedItem != null) {
                selectedCheckoutItem = selectedItem;
                amount.setText(String.valueOf(selectedItem.getAmount()));
                discount.setText(String.valueOf(selectedItem.getDiscount()));

                for (ItemDetail item : itemComboBox.getItems()) {
                    if (item.getName().equals(selectedItem.getName()) &&
                            item.getSize().equals(selectedItem.getItemSize())) {
                        itemComboBox.getSelectionModel().select(item);
                        break;
                    }
                    if (item.getName() != null && item.getSize() != null &&
                            item.getName().equals(selectedItem.getName()) &&
                            item.getSize().equals(selectedItem.getItemSize())) {
                        itemComboBox.getSelectionModel().select(item);
                        break;
                    }
                }
            } else {
                selectedCheckoutItem = null;
                itemComboBox.getSelectionModel().clearSelection();
                amount.clear();
                discount.clear();
            }
        });
        mainTable.setRowFactory(tv -> {
            TableRow<CheckoutItem> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    if (event.isControlDown() || event.isShiftDown() || event.isMetaDown()) {
                        return;
                    }

                    int index = row.getIndex();
                    if (mainTable.getSelectionModel().isSelected(index)) {
                        mainTable.getSelectionModel().clearSelection(index);
                    } else {
                        mainTable.getSelectionModel().select(index);
                    }

                    event.consume();
                }
            });

            return row;
        });

        // Bottom Section
        discountForAll = new TextField();
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

        receivedFund = new TextField();
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
        grandTotal = new Label("_");
        grandTotalTxt.getStyleClass().add("information-texts");
        grandTotal.getStyleClass().add("information-label");

        Text balanceTxt = new Text("Due Balance:");
        balanceTxt.setStyle("-fx-font-weight: bold; -fx-font-size: 19px;");
        balance = new Label("_");
        balance.setStyle("-fx-font-weight: bold; -fx-font-size: 19px;");
        balanceTxt.getStyleClass().add("information-texts");
        balance.getStyleClass().add("information-label");
        checkOutButton = new Button("Check Out");
        checkOutButton.getStyleClass().add("default-buttons");

        discountForAll.setOnAction(e -> receivedFund.requestFocus());
        receivedFund.setOnAction(e -> checkOutButton.requestFocus());

        /*
         *   This is the action of the adding button for items and this piece of code is here cause, to access for all the values in the above code.
         */
        customerMessage = new Text("");
        stockMessage = new Text("");
        stockMessage.setVisible(false);
        stockMessage.setManaged(false);
        stockMessage.setTextAlignment(TextAlignment.CENTER);
        stockMessage.setFont(Font.font("System"));
        stockMessage.getStyleClass().add("stock-message");

        if (stockMessageTimer != null) {
            stockMessageTimer.stop();
        }

        // Auto-hide after 3 seconds
        stockMessageTimer = new PauseTransition(Duration.seconds(1));
        stockMessageTimer.setOnFinished(ev -> stockMessage.setText(""));
        stockMessageTimer.play();

        StackPane stockMessageContainer = new StackPane();
        stockMessageContainer.setMaxWidth(Double.MAX_VALUE);
        stockMessageContainer.getChildren().addAll(stockMessage);

        addButton.setOnAction(e -> {
            selectedCheckoutItem = mainTable.getSelectionModel().getSelectedItem();

            String discountItemValue = discount.getText();
            ItemDetail selectedItemDetail = itemComboBox.getSelectionModel().getSelectedItem();

            if (selectedItemDetail == null || amount.getText().isEmpty()) {
                System.out.println("Please select an item and enter the quantity.");
                return;
            }

            try {
                int quantityValue = Integer.parseInt(amount.getText().trim());
                int remainingAmount = selectedItemDetail.getRemainingQty();

                if (remainingAmount > 0 && quantityValue <= remainingAmount) {
                    int discountValue = 0;
                    if (discountItemValue != null && !discountItemValue.trim().isEmpty()) {
                        discountValue = Integer.parseInt(discountItemValue);
                    }

                    int price = (int) selectedItemDetail.getPrice();
                    double sellingPrice = selectedItemDetail.getSellingPrice();

                    double totalCostValue = sellingPrice * quantityValue;
                    double totalDiscountValue = totalCostValue * ((double) discountValue / 100);
                    double grandTotalValue = totalCostValue - totalDiscountValue;

                    dbConnection.storeSales(
                            selectedItemDetail.getId(),
                            selectedItemDetail.getItemHasSizeID(),
                            quantityValue,
                            price,
                            1
                    );

                    if (selectedCheckoutItem != null) {
                        selectedCheckoutItem.setItemHasSizeId(selectedItemDetail.getItemHasSizeID());
                        selectedCheckoutItem.setDiscount((double) discountValue);
                        selectedCheckoutItem.amountProperty().set(quantityValue);
                        selectedCheckoutItem.itemTotalCostProperty().set(String.valueOf(totalCostValue));
                        selectedCheckoutItem.sellingPriceProperty().set(sellingPrice);
                        selectedCheckoutItem.priceProperty().set(price);
                        selectedCheckoutItem.nameProperty().set(selectedItemDetail.getName());
                        selectedCheckoutItem.itemSizeProperty().set(selectedItemDetail.getSize());
                        mainTable.refresh();

                    } else {
                        CheckoutItem newItem = new CheckoutItem(
                                selectedItemDetail.getName(),
                                selectedItemDetail.getSize(),
                                selectedItemDetail.getItemColor(),
                                quantityValue,
                                price,
                                sellingPrice,
                                discountValue,
                                String.valueOf(totalCostValue)
                        );
                        newItem.setItemHasSizeId(selectedItemDetail.getItemHasSizeID());
                        itemList.add(newItem);
                    }

                    // Update class-level cumulative totals here:
                    cumulativeTotalCost = 0;
                    cumulativeTotalDiscount = 0;
                    cumulativeGrandTotal = 0;

                    for (CheckoutItem item : itemList) {
                        int qty = item.getAmount();
                        double sellPrice = item.getSellingPrice();
                        double itemTotal = qty * sellPrice;
                        double itemDiscount = itemTotal * (item.getDiscount() / 100.0);
                        double itemGrandTotal = itemTotal - itemDiscount;

                        cumulativeTotalCost += itemTotal;
                        cumulativeTotalDiscount += itemDiscount;
                        cumulativeGrandTotal += itemGrandTotal;
                    }

                    totalCost.setText("$" + String.format("%.2f", cumulativeTotalCost));
                    totalDiscount.setText("$" + String.format("%.2f", cumulativeTotalDiscount));
                    grandTotal.setText("$" + String.format("%.2f", cumulativeGrandTotal));

                    mainTable.refresh();

                    // Clear selection and inputs
                    itemComboBox.getSelectionModel().clearSelection();
                    selectedCheckoutItem = null;

                    stockMessage.setVisible(false);
                    stockMessage.setManaged(false);
                    addButton.setDisable(false);
                    discount.clear();
                    amount.clear();

                } else {
                    stockMessageContainer.setAlignment(Pos.CENTER);
                    addButton.setDisable(true);
                    stockMessage.setText("Not enough stock!");
                    stockMessage.setTextAlignment(TextAlignment.CENTER);
                    stockMessage.getStyleClass().add("stock-error-message");
                    inputSection.setMaxWidth(Double.MAX_VALUE);
                    stockMessageContainer.getChildren().addAll(stockMessage);
                    inputSection.getChildren().addAll(stockMessageContainer);
                    stockMessage.setVisible(true);
                    stockMessage.setManaged(true);
                }

                updateSelectedRow();

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
                addButton.setDisable(true);
            }
            if (stockMessageTimer != null) {
                stockMessageTimer.stop();
            }
            stockMessageTimer = new PauseTransition(Duration.seconds(3));
            stockMessageTimer.setOnFinished(ev -> stockMessage.setText(""));
            stockMessageTimer.play();
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

                // Clear processed items to ensure all updates occur every checkout
                processedItemIds.clear();

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
                String discountText = discountForAll.getText().trim();

                if (discountText.endsWith("%")) {
                    String numberPart = discountText.substring(0, discountText.length() - 1);
                    if (!numberPart.isEmpty()) {
                        discountValue = Double.parseDouble(numberPart);
                    }
                } else if (!discountText.isEmpty()) {
                    // Assuming this is an absolute discount amount
                    discountValue = Double.parseDouble(discountText);
                }

                double totalDiscountValue;
                double adjustedTotal;

                if (discountText.endsWith("%")) {
                    // percentage discount
                    totalDiscountValue = cumulativeTotalCost * (discountValue / 100.0);
                    adjustedTotal = cumulativeTotalCost - totalDiscountValue;
                } else {
                    // absolute discount
                    totalDiscountValue = discountValue;
                    adjustedTotal = cumulativeTotalCost - totalDiscountValue;
                }

                // Format output values to 2 decimals
                totalDiscount.setText("$" + String.format("%.2f", totalDiscountValue));
                grandTotal.setText("$" + String.format("%.2f", adjustedTotal));

                cumulativeTotalDiscount = totalDiscountValue;
                cumulativeGrandTotal = adjustedTotal;

                double dueBalanceValue = cumulativeReceivedFund - adjustedTotal;
                balance.setText("$" + String.format("%.2f", dueBalanceValue));

                mainTable.refresh();

                if (!itemList.isEmpty()) {
                    checkOutButton.setDisable(true);
                    checkoutJustCompleted = true;
                }

            } catch (NumberFormatException ex) {
                System.out.println("Invalid input in received fund or discount field.");
            }
        });

        if (!receivedFund.getText().trim().equals("")) {
            checkOutButton.setDisable(false);
        }

        itemMessageContainer = new Label("");
        // The floating section in the right_side
        Button deleteRow = new Button("❌Remove Item");
        deleteRow.setWrapText(false);
        deleteRow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        deleteRow.setMinWidth(Region.USE_PREF_SIZE);
        deleteRow.setMaxWidth(Region.USE_PREF_SIZE);
        Label itemInformation = new Label("");
        deleteRow.getStyleClass().add("clear-button");

        deleteRow.setOnAction(e -> {
            ObservableList<CheckoutItem> selectedItems = mainTable.getSelectionModel().getSelectedItems();

            if (selectedItems.isEmpty()) {
                itemMessageContainer.setText("No items to delete");
                System.out.println("No items selected to delete.");
                return;
            }

            List<CheckoutItem> itemsToRemove = new ArrayList<>(selectedItems);

            itemList.removeAll(itemsToRemove);
            mainTable.refresh();

            double cumulativeTotalCost = 0;
            double cumulativeTotalDiscount = 0;
            double cumulativeGrandTotal = 0;

            for (CheckoutItem item : itemList) {
                int qty = item.getAmount();
                double sellPrice = item.getSellingPrice();
                double itemTotal = qty * sellPrice;
                double itemDiscount = itemTotal * (item.getDiscount() / 100.0);
                double itemGrandTotal = itemTotal - itemDiscount;

                cumulativeTotalCost += itemTotal;
                cumulativeTotalDiscount += itemDiscount;
                cumulativeGrandTotal += itemGrandTotal;
            }

            totalCost.setText("Rs." + String.format("%.2f", cumulativeTotalCost));
            totalDiscount.setText("Rs." + String.format("%.2f", cumulativeTotalDiscount));
            grandTotal.setText("Rs." + String.format("%.2f", cumulativeGrandTotal));

            double receivedFundValue = 0.0;
            String receivedInput = receivedFund.getText();
            if (receivedInput != null && !receivedInput.isEmpty()) {
                String cleaned = receivedInput.replaceAll("[$,\\s]", "");
                if (!cleaned.isEmpty()) {
                    try {
                        receivedFundValue = Double.parseDouble(cleaned);
                    } catch (NumberFormatException ex) {
                        System.err.println("Invalid received fund input");
                    }
                }
            }

            String deletedNames = itemsToRemove.stream()
                    .map(CheckoutItem::getName)
                    .collect(Collectors.joining(", "));
            itemMessageContainer.setVisible(true);
            itemMessageContainer.setManaged(true);
            itemMessageContainer.setStyle("-fx-font-size: 19px; -fx-font-weight: bold;");
            itemMessageContainer.setMaxWidth(250);
            itemMessageContainer.setText("Deleted item: " + deletedNames);
            itemMessageContainer.setStyle(
                    "-fx-font-size: 14px;" +
                    "-fx-text-fill: white;" +
                    "-fx-background-color: #264653;" +
                    "-fx-padding: 5 10;" +
                    "-fx-border-color: #2a9d8f;"  +
                    "-fx-border-radius: 9;" +
                    "-fx-background-radius: 6;" +
                    "-fx-font-weight: bold;" +
                    "-fx-effect: dropshadow(gaussian,  rgba(0,0,0,0.4), 4, 0, 1, 1);"
            );

            itemsDeletingMsgTimer = new PauseTransition(Duration.seconds(2));
            itemsDeletingMsgTimer.setOnFinished(ev -> {
                        itemMessageContainer.setText("");
                        itemMessageContainer.setStyle("");
                    });
            itemsDeletingMsgTimer.play();

            System.out.println("Deleted items: " + deletedNames);
        });

        Button remove = new Button("Remove All");
        remove.setWrapText(false);
        remove.setPrefWidth(Region.USE_COMPUTED_SIZE);
        remove.setMinWidth(Region.USE_PREF_SIZE);
        remove.setMaxWidth(Region.USE_PREF_SIZE);

        remove.setOnAction(e-> {
            mainTable.getItems().clear();
            totalCost.setText("-");
            totalDiscount.setText("-");
            grandTotal.setText("-");
            balance.setText("-");

            checkOutButton.setDisable(false);
        });

        remove.getStyleClass().add("clear-button");

        // The tooltip
        HoverTooltip removeItemTooltip = new HoverTooltip("Remove selected item row.");
        removeItemTooltip.attachTo(deleteRow);
        // Remove all Tool tip
        HoverTooltip removeItemsTooltip = new HoverTooltip("Remove all items from the checkout list.");
        removeItemsTooltip.attachTo(remove);

        HBox actionSection = new HBox();
        itemMessageContainer.setPrefWidth(600);
        itemMessageContainer.setWrapText(true);
        itemMessageContainer.setStyle("-fx-font-size: 19px; -fx-fill: white;");
        itemMessageContainer.setAlignment(Pos.CENTER_LEFT);
        itemMessageContainer.setMaxWidth(Double.MAX_VALUE);
        itemMessageContainer.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setBottomAnchor(itemMessageContainer, 300.0);
        AnchorPane.setRightAnchor(itemMessageContainer, 30.0);

        actionSection.setSpacing(10);
        actionSection.setPrefWidth(600);
        actionSection.setAlignment(Pos.TOP_RIGHT);
        actionSection.setPadding(new Insets(0, 0, 10, 0));
        actionSection.getChildren().addAll(itemMessageContainer, deleteRow, remove);
        actionSection.setMaxWidth(Double.MAX_VALUE);
        actionSection.setAlignment(Pos.CENTER_RIGHT);

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

    private void showMessage(String message, Color color) {
        customerMessage.setText(message);
        customerMessage.setFill(color);
        customerMessage.setTextAlignment(TextAlignment.CENTER);
        customerMessage.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Region space = new Region();
        space.setMinHeight(40);
        if (!inputVerticalSec.getChildren().contains(customerMessage)) {
            inputVerticalSec.getChildren().add(customerMessage);
        }
        if (messageTimer != null) {
            messageTimer.stop();
        }

        messageTimer = new PauseTransition(Duration.seconds(2));
        messageTimer.setOnFinished(ev -> customerMessage.setText(""));
        messageTimer.play();
    }


    // Update the selected row
    private void updateSelectedRow() {
        Object selectedObject = mainTable.getSelectionModel().getSelectedItem();

        if (selectedObject == null || !(selectedObject instanceof CheckoutItem)) {
            System.out.println("No valid row selected.");
            return;
        }

        checkOutButton.setDisable(false);
        CheckoutItem selected = (CheckoutItem) selectedObject;

        try {
            int newAmount = Integer.parseInt(amount.getText());
            double newDiscount = Double.parseDouble(discount.getText());

            selected.amountProperty().set(newAmount);
            selected.setDiscount(newDiscount);

            mainTable.refresh();

        } catch (NumberFormatException ex) {
            System.out.println("Invalid input. Amount must be an integer and discount a decimal.");
        }
    }

    private void recalculateTotals() {
        double cumulativeTotalCost = 0;
        double cumulativeTotalDiscount = 0;
        double cumulativeGrandTotal = 0;

        for (CheckoutItem item : itemList) {
            int qty = item.getAmount();
            double sellPrice = item.getSellingPrice();
            double itemTotal = qty * sellPrice;
            double itemDiscount = itemTotal * (item.getDiscount() / 100.0);
            double itemGrandTotal = itemTotal - itemDiscount;

            cumulativeTotalCost += itemTotal;
            cumulativeTotalDiscount += itemDiscount;
            cumulativeGrandTotal += itemGrandTotal;
        }

        totalCost.setText("Rs." + String.format("%.2f", cumulativeTotalCost));
        totalDiscount.setText("Rs." + String.format("%.2f", cumulativeTotalDiscount));
        grandTotal.setText("Rs." + String.format("%.2f", cumulativeGrandTotal));
    }


}