package com.example.inventorymanagementsystem.view;
import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.services.interfaces.AuthenticateStateListener;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.services.utils.Logger;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.state.Session;
import com.example.inventorymanagementsystem.view.components.CurrencyCellFactory;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.HoverTooltip;
import com.example.inventorymanagementsystem.view.components.EasyCheckoutItem;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


/**
 *
 */
public class Checkout implements ThemeObserver, AuthenticateStateListener {
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

    private double availablePoints = 0.0D;
    private double refundAmount = 0.0D;
    private Set<Integer> processedItemIds = new HashSet<>();
    private boolean checkoutJustCompleted = false;
    private TextField receivedFund;
    private TextField discountForAll;
    private CheckoutItem selectedCheckoutItem = null;
    private FormField<ComboBox, Customer> registeredCustomers;
//    private ComboBox<Customer> registeredCustomers;
    Label totalDiscount;
    TableView<CheckoutItem> mainTable;
    Label grandTotal;
    Label balance;
    Text stockMessage;
    Text customerMessage;
    VBox inputVerticalSec;
    Label itemMessageContainer;
    TextField itemId;
    TextField discount;
    TextField amount;
    Button checkOutButton;

    CheckBox payFromRefundAmount;
    CheckBox payFromPoints;

    Customer selectedCustomer;

    Label liableAmountLabel;
    double liableAmount = 0.0D;

    Label points;
    Label refunds;

    FilteredList<Customer> filteredItems = null;
    FilteredList<ItemDetail> checkoutItems;

    TableColumn<CheckoutItem, Double> priceCol;

    public Checkout() throws SQLException{
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

        checkoutItems = new FilteredList<>(Data.getInstance().getItemDetails());
        checkoutItems.setPredicate(itemDetail -> itemDetail.getRemainingQty() > 0);

        itemComboBox = new ComboBox<>();
        itemComboBox.setItems(checkoutItems);


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
                    String size = "";
                    if (item.getSize() != null){
                        size = item.getSize();
                    }
                    textLabel.setText(item.getName() + " (" + size + ")");
                    if (item.getItemColor() != null) {
                        colorCircle.setFill(Color.web(item.getItemColor()));
                    }
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
                setTextFill(Paint.valueOf("#00AAFF"));
            }
        });

        // SINGLE selection listener (remove duplicates)
        itemComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedItmPrice = newValue.getPrice();
                System.out.println("Selected item price: " + selectedItmPrice);
                checkOutButton.setDisable(false);
            }
        });

        itemComboBox.setMaxWidth(Double.MAX_VALUE);
        itemComboBox.setPromptText("Select The Item");
        itemComboBox.getStyleClass().add("default-dropdowns");
        itemId = new TextField();
        itemId.setPromptText("Type item id");
        itemId.getStyleClass().add("default-text-areas");
        itemId.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int integerItemId = Integer.parseInt(newValue);
                try {
                    ItemDetail itemDetail = Connection.getInstance().getItemDetail(integerItemId);
                    itemComboBox.getSelectionModel().select(itemDetail);
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        });

        liableAmountLabel = new Label("Liable Amount");

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
        theSpace.setMinHeight(10);
        theSpace.setMaxHeight(10);

        Text customerTxt = new Text("Customer Information");
        try {
            filteredItems = new FilteredList<>(Data.getInstance().getCustomers(), new Predicate<Customer>() {
                @Override
                public boolean test(Customer customer) {
                    return true;
                }
            });

            registeredCustomers = new FormField<>("Select Customer", ComboBox.class, filteredItems);
//            registeredCustomers.getComboBox().setVisibleRowCount(6);
            registeredCustomers.getComboBox().setEditable(true);
//            registeredCustomers.getComboBox().getEditor().setDisable(true);
            registeredCustomers.getComboBox().getEditor().setOpacity(1);
            registeredCustomers.getComboBox().setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
                @Override
                public ListCell<Customer> call(ListView<Customer> param) {
                    return new ListCell<>(){
                        @Override
                        protected void updateItem(Customer item, boolean empty) {
                            super.updateItem(item, empty);
                            if(item != null && !empty){
                                setText(item.getFirstName() + " " + item.getLastName());
                            }else{
                                setText(null);
                                setGraphic(null);
                            }
                        }
                    };
                }
            });

            registeredCustomers.getComboBox().setConverter(new StringConverter<Customer>() {
                @Override
                public String toString(Customer object) {
                    if (object != null){
                        return object.getFirstName() + " " + object.getLastName();
                    }
                    return null;
                }

                @Override
                public Customer fromString(String string) {
                    return filteredItems.stream().filter(customer -> (customer.getFirstName() + ' ' + customer.getLastName()).toLowerCase().contains(string.toLowerCase())).findFirst().orElse(null);
                }
            });

            registeredCustomers.getComboBox().getEditor().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                    System.out.println("New value entered in the text box: " + newValue);
                    filteredItems.setPredicate(new Predicate<Customer>() {
                        @Override
                        public boolean test(Customer customer) {
                            if (newValue == null || newValue.isBlank() || newValue.isEmpty()){
                                return true;
                            }else {
                                return customer.getFirstName().toLowerCase().contains(newValue.toLowerCase()) || customer.getLastName().toLowerCase().contains(newValue.toLowerCase()) || customer.getPhone().contains(newValue);
                            }
                        }
                    });

                    if (registeredCustomers.getComboBox().isShowing()) {
                        registeredCustomers.getComboBox().hide();
                    }
                    registeredCustomers.getComboBox().show();
                }
            });
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        FormField<TextField, String> customerSearchBox = new FormField<>("Search Customer", TextField.class);
        ((TextField)customerSearchBox.getControl()).textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                System.out.println("New value entered in the text box: " + newValue);
                filteredItems.setPredicate(new Predicate<Customer>() {
                    @Override
                    public boolean test(Customer customer) {
                        if (newValue == null || newValue.isBlank() || newValue.isEmpty()){
                            return true;
                        }else {
                            return customer.getFirstName().toLowerCase().contains(newValue.toLowerCase()) || customer.getLastName().toLowerCase().contains(newValue.toLowerCase()) || customer.getPhone().contains(newValue);
                        }
                    }
                });

//                ObservableList<Customer> filteredCopy = FXCollections.observableArrayList(filteredItems);
//                registeredCustomers.getComboBox().setItems(null);
//                registeredCustomers.getComboBox().setItems(filteredCopy);

                registeredCustomers.getComboBox().setItems(filteredItems);
//                        registeredCustomers.getComboBox().show();
                if (registeredCustomers.getComboBox().isShowing()) {
                    registeredCustomers.getComboBox().hide();
                }
                registeredCustomers.getComboBox().show();
            }
        });
        ComboBox<Customer> customerComboBox = registeredCustomers.getComboBox();

        customerComboBox.getStyleClass().add("default-dropdowns");
//        customerComboBox.setPromptText("Select the customer");
        customerComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText("%s %s".formatted(item.getFirstName(), item.getLastName()));
                    payFromPoints.setDisable(item.getPoints() <= 0);
                    payFromRefundAmount.setDisable(item.getRefundAmount() <= 0);
                }
                setTextFill(Paint.valueOf("#0088FF"));
            }
        });

        VBox customerPointsContainer = new VBox();

        customerPointsContainer.setAlignment(Pos.CENTER);

        FontIcon pointsIcon = new FontIcon(FontAwesomeSolid.COINS);
        pointsIcon.setFill(Paint.valueOf("#EECC00"));

        Label pointsLabel = new Label();
        pointsLabel.setTextAlignment(TextAlignment.LEFT);
        pointsLabel.setContentDisplay(ContentDisplay.LEFT);
        pointsLabel.setGraphic(pointsIcon);
        pointsLabel.setGraphicTextGap(5.0D);
        pointsLabel.setStyle("-fx-text-fill: lightGray; font-weight: bold; -fx-font-size: 14px;");

//        customerComboBox.setConverter(new StringConverter<Customer>() {
//            @Override
//            public String toString(Customer object) {
//                if (object == null) return "";
//                return object.getFirstName() + " " + object.getLastName();
//            }
//
//            @Override
//            public Customer fromString(String string) {
//                return customerComboBox.getItems().stream().filter(c -> (c.getFirstName() + " " + c.getLastName()).equals(string)).findFirst().orElse(null);
//            }
//        });

//        customerComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
//            @Override
//            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
//                if (newValue != null && !customerComboBox.getEditor().getText().equals(customerComboBox.getConverter().toString(newValue))){
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            customerComboBox.getEditor().setText(customerComboBox.getConverter().toString(newValue));
//                        }
//                    });
//                }
//            }
//        });

//        customerComboBox.getEditor().focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                System.out.println("Focus changed: " + newValue + ' ' + oldValue);
//                if (!newValue){
//                    String type = customerComboBox.getEditor().getText().trim();
//                    Customer match = customerComboBox.getConverter().fromString(type);
//                    if (match != null){
//                        customerComboBox.getSelectionModel().select(match);
//                        customerComboBox.getEditor().setText(customerComboBox.getConverter().toString(match));
//                    }else{
//                        customerComboBox.getSelectionModel().clearSelection();
//                    }
//
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            customerComboBox.getEditor().requestFocus();
//                            customerComboBox.getEditor().selectAll();
//                        }
//                    });
//                }
//            }
//        });

//        customerComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
//            @Override
//            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
//
//            }
//        });

        customerComboBox.setOnAction(e -> {
            if (customerComboBox.isFocused()) {
                selectedCustomer = customerComboBox.getValue();
                System.out.println("Customer combo box is focused: " + customerComboBox.isFocused());
                if (selectedCustomer != null) {
                    points.setText("%.2f".formatted(selectedCustomer.getPoints()));
                    refunds.setText("%.2f".formatted(selectedCustomer.getRefundAmount()));

                    if (selectedCustomer.getPoints() > 0) {
                        availablePoints = selectedCustomer.getPoints();
                        payFromPoints.setDisable(false);
                    } else {
                        payFromPoints.setDisable(true);
                    }

                    if (selectedCustomer.getRefundAmount() > 0) {
                        refundAmount = selectedCustomer.getRefundAmount();
                        payFromRefundAmount.setDisable(false);
                    } else {
                        payFromRefundAmount.setDisable(true);
                    }

                    try {
                        String fetchPointsSQL = "SELECT points FROM customer WHERE id = ?";
                        try (PreparedStatement stmt = dbConnection.getJdbcConnection().prepareStatement(fetchPointsSQL)) {
                            stmt.setInt(1, selectedCustomer.getId());
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                double points = rs.getDouble("points");
                                pointsLabel.setText(String.format("%.2f", points));
                            }
                        }
                    } catch (SQLException ex) {
                        System.out.println("Error fetching points: " + ex.getMessage());
                        pointsLabel.setText("N/A");
                    }
                } else {
                    pointsLabel.setText("-");
                }
            }else{
                customerComboBox.getSelectionModel().select(selectedCustomer);
            }
        });


        customerPointsContainer.getChildren().addAll(customerComboBox, pointsLabel);
        customerPointsContainer.setSpacing(5.0D);
        customerPointsContainer.setAlignment(Pos.CENTER_LEFT);
        customerPointsContainer.setFillWidth(true);

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

            if (firstNameField.isEmpty()) {
                showMessage("Please fill in all required fields.", Color.ORANGE);
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            String result = dbConnection.addCustomers(
                    firstNameField,
                    lastNameField,
                    phoneField,
                    emailField,
                    now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    null
            );

            // Show message
            if (result.equals("Customer already exists!")) {
                itemMessageContainer.setText(result);
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

        addCustomerSec.setPadding(new Insets(0, 0, 0, 0));
        addCustomerSec.getChildren().addAll(addCustomer, clearForm);

        // For the adding items section
        VBox inputSection = new VBox();
        inputSection.setSpacing(8.0D);
        inputSection.setPadding(new Insets(0, 50, 0, 0));
        inputSection.setAlignment(Pos.TOP_CENTER);
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

        priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        priceCol.setCellFactory(CurrencyCellFactory.withPrefix("Rs."));

        TableColumn<CheckoutItem, Double> sellingPriceCol = new TableColumn<>("Selling Price");
        sellingPriceCol.setCellValueFactory(cellData -> cellData.getValue().sellingPriceProperty().asObject());
        sellingPriceCol.setCellFactory(CurrencyCellFactory.withPrefix("Rs."));

        TableColumn<CheckoutItem, String> totalCostCol = new TableColumn<>("Total Cost");
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("itemTotalCost"));
        totalCostCol.setCellFactory(CurrencyCellFactory.withPrefix("Rs."));

        TableColumn<CheckoutItem, Double> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(cellData -> cellData.getValue().discountProperty().asObject());

        TableColumn<CheckoutItem, Double> costWithDiscountCol = new TableColumn<>("CostWithDiscount");
        costWithDiscountCol.setCellValueFactory(cellData -> cellData.getValue().costWithDiscountProperty().asObject());
        costWithDiscountCol.setCellFactory(CurrencyCellFactory.withPrefix("Rs."));

        mainTable.getColumns().addAll(nameCol, sizeCol, colorCol, amountCol, priceCol, sellingPriceCol, totalCostCol, costWithDiscountCol);
        mainTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mainTable.getColumns().addAll(discountCol);

//        ObservableList<CheckoutItem> itemList = dbConnection.getCheckoutItems(); // contains full item objects
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

                addButton.setDisable(false);

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
        payFromRefundAmount = new CheckBox("Pay From Refund Amount");
        payFromRefundAmount.getStyleClass().add("inverse-texts");
        payFromRefundAmount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (payFromRefundAmount.isSelected()){
                    Customer customer = customerComboBox.getSelectionModel().getSelectedItem();
                    double refundAmount = customer.getRefundAmount();

                    if (refundAmount < cumulativeGrandTotal){
                        if (payFromPoints.isSelected()){
                            double remainingAmount = cumulativeGrandTotal - refundAmount;
                            if (customer.getPoints() > remainingAmount){
                                receivedFund.setText(String.valueOf(refundAmount + remainingAmount));
                            }else{
                                receivedFund.setText(String.valueOf(refundAmount + customer.getPoints()));
                            }
                        }else {
                            receivedFund.setText(String.valueOf(refundAmount));
                        }
                    }else{
                        receivedFund.setText(String.valueOf(cumulativeGrandTotal));
                    }
                }
            }
        });

        payFromPoints = new CheckBox("Pay from Points");
        payFromPoints.getStyleClass().add("inverse-texts");
        payFromPoints.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                double currentEnteredValue = 0.0D;
                if (!receivedFund.getText().isEmpty() && !receivedFund.getText().isBlank()){
                    currentEnteredValue = Double.parseDouble(receivedFund.getText());
                }

                if(payFromPoints.isSelected()){
//                    receivedFund.setText(String.valueOf(cumulativeGrandTotal));
                    Customer customer = customerComboBox.getSelectionModel().getSelectedItem();
                    if (customer != null){
                        if (customer.getPoints() < cumulativeGrandTotal) {
                            if (payFromRefundAmount.isSelected()){
                                double remainingAmount = cumulativeGrandTotal - customer.getPoints();
                                if (customer.getRefundAmount() > remainingAmount){
                                    receivedFund.setText(String.valueOf(customer.getPoints() + remainingAmount));
                                }else{
                                    receivedFund.setText(String.valueOf(customer.getPoints() + customer.getRefundAmount()));
                                }
                            }else{
                                receivedFund.setText(String.valueOf(customer.getPoints()));
                            }
                        }else{
                            receivedFund.setText(String.valueOf(cumulativeGrandTotal));
                        }
                    }
                }else{
                    receivedFund.setText("");
                }
            }
        });

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

        receivedFund.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                receivedFund.setText(newValue);
                receivedFund.positionCaret(newValue.length());
//                payFromPoints.setDisable(!(Double.parseDouble(newValue) <= availablePoints));
//                payFromRefundAmount.setDisable(!(Double.parseDouble(newValue) <= refundAmount));
                double receivedAmount = Double.parseDouble(newValue);
                double remains = receivedAmount - cumulativeGrandTotal;
                balance.setText("%.2f".formatted(remains));
            }
        });

//        receivedFund.textProperty().addListener((obs, oldValue, newValue) -> {
//            if (newValue == null || newValue.isEmpty()) {
//                return;
//            }

//            System.out.println("New value: " + newValue);
//            String digitsOnly = newValue.replaceAll("[^\\d]", "");

//            if (digitsOnly.isEmpty()) {
//                receivedFund.setText("");
//                return;
//            }
//            try {
//                double number = Double.parseDouble(newValue);
//                String formatted = "%.2f".formatted(number);
//
//                if (!formatted.equals(newValue)) {
//                    receivedFund.setText(formatted);
//                    receivedFund.positionCaret(formatted.length());
//                }
//            } catch (NumberFormatException e) {
//                receivedFund.setText(oldValue);
//                receivedFund.positionCaret(oldValue.length());
//            }
//        });

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
        balance.setMinWidth(150.0D);
        balance.setStyle("-fx-font-weight: bold; -fx-font-size: 19px;");
        balanceTxt.getStyleClass().add("information-texts");
        balance.getStyleClass().add("information-label");

        CheckBox savePoints = new CheckBox("Save as Points");
        savePoints.getStyleClass().add("inverse-texts");

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
                    double discountValue = 0;
                    if (discountItemValue != null && !discountItemValue.trim().isEmpty()) {
                        discountValue = Double.parseDouble(discountItemValue);
                    }

                    int price = (int) selectedItemDetail.getPrice();
                    double sellingPrice = selectedItemDetail.getSellingPrice();

                    double totalCostValue = sellingPrice * quantityValue;
                    double totalDiscountValue = totalCostValue * (discountValue / 100);
                    double grandTotalValue = totalCostValue - totalDiscountValue;

                    if (selectedCheckoutItem != null) {
                        selectedCheckoutItem.setItemHasSizeId(selectedItemDetail.getItemHasSizeID());
                        selectedCheckoutItem.setDiscount((double) discountValue);
                        selectedCheckoutItem.amountProperty().set(quantityValue);
                        selectedCheckoutItem.itemTotalCostProperty().set(String.valueOf(totalCostValue));
                        selectedCheckoutItem.sellingPriceProperty().set(sellingPrice);
                        selectedCheckoutItem.priceProperty().set(price);
                        selectedCheckoutItem.nameProperty().set(selectedItemDetail.getName());
                        selectedCheckoutItem.itemSizeProperty().set(selectedItemDetail.getSize());
                        selectedCheckoutItem.costWithDiscountProperty().set(grandTotalValue);
                        mainTable.refresh();
                    } else {
                        CheckoutItem newItem = new CheckoutItem(
                                0,
                                selectedItemDetail.getName(),
                                selectedItemDetail.getSize(),
                                selectedItemDetail.getItemColor(),
                                quantityValue,
                                price,
                                sellingPrice,
                                discountValue,
                                String.valueOf(totalCostValue),
                                grandTotalValue,
                                0
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
                        double itemTotal = Double.parseDouble(item.getItemTotalCost());
                        double itemDiscount = itemTotal - item.getCostWithDiscount();
                        double itemGrandTotal = item.getCostWithDiscount();

                        cumulativeTotalCost += itemTotal;
                        cumulativeTotalDiscount += itemDiscount;
                        cumulativeGrandTotal += itemGrandTotal;
                    }

                    totalCost.setText("Rs." + String.format("%.2f", cumulativeTotalCost));
                    totalDiscount.setText("Rs." + String.format("%.2f", cumulativeTotalDiscount));
                    grandTotal.setText("Rs." + String.format("%.2f", cumulativeGrandTotal));

                    Customer customer = customerComboBox.getSelectionModel().getSelectedItem();
                    if (customer != null){
                        double points = customer.getPoints();
                        double refundAmount = customer.getRefundAmount();
                        payFromPoints.setDisable(points < cumulativeGrandTotal);
                        payFromRefundAmount.setDisable(refundAmount < cumulativeGrandTotal);
                    }

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
                    inputSection.setAlignment(Pos.TOP_CENTER);
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

//        amount.textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                ItemDetail selectedItemDetail = itemComboBox.getSelectionModel().getSelectedItem();
//                int typedAmount = Integer.parseInt(amount.getText().trim());
//                int remainingAmount = selectedItemDetail.getRemainingQty();
//
//                if (typedAmount >= remainingAmount) {
//                    stockMessage.setText("Not enough stock!");
//
//                    if (!(inputSection.getChildren().contains(stockMessageContainer))) {
//                        inputSection.getChildren().add(stockMessageContainer);
//                    }
//                } else {
//                    inputSection.getChildren().remove(stockMessageContainer);
//                }
//            } catch(NumberFormatException numberFormatException){
//                System.out.println("Amount text field is empty");
//            }
//        });

//        FormField<ComboBox, Customer> finalRegisteredCustomers = registeredCustomers;
        ComboBox<Customer> finalRegisteredCustomers = registeredCustomers.getComboBox();

        checkOutButton.setOnAction(e -> {
            try {
                if (itemList.isEmpty()) {
                    System.out.println("No items in the checkout list!");
                    return;
                }

                processedItemIds.clear();

                String receivedInput = receivedFund.getText();
                double receivedFundValue = 0.0;
                if (receivedInput != null && !receivedInput.isEmpty()) {
                    String cleaned = receivedInput.replaceAll("[Rs,\\s]", "");
                    if (!cleaned.isEmpty()) {
                        receivedFundValue = Double.parseDouble(cleaned);
                    }
                }
                cumulativeReceivedFund = receivedFundValue;

                int remainsStatusID = 3;
                if (savePoints.isSelected() && (cumulativeReceivedFund - cumulativeGrandTotal) > 0.0){
                    remainsStatusID = 2;
                }else if(!savePoints.isSelected() && (cumulativeReceivedFund - cumulativeGrandTotal) > 0.0){
                    remainsStatusID = 1;
                }else if(!savePoints.isSelected() && (cumulativeReceivedFund - cumulativeGrandTotal) < 0.0){
                    remainsStatusID = 4;
                }

                double discountValue = 0.0;
                String discountText = discountForAll.getText().trim();
                if (discountText.endsWith("%")) {
                    String numberPart = discountText.substring(0, discountText.length() - 1);
                    if (!numberPart.isEmpty()) {
                        discountValue = Double.parseDouble(numberPart);
                    }
                } else if (!discountText.isEmpty()) {
                    discountValue = Double.parseDouble(discountText);
                }

                Customer selectedCustomer = (Customer) finalRegisteredCustomers.getValue();

                try {
                    boolean result = Connection.getInstance().insertCheckoutItem(
                            selectedCustomer,
                            itemList,
                            cumulativeTotalCost,
                            cumulativeReceivedFund,
                            remainsStatusID,
                            discountValue,
                            payFromPoints.isSelected(),
                            payFromRefundAmount.isSelected(),
                            savePoints.isSelected());

                    System.out.println("Result got by adding items: " + result);
                    customerComboBox.hide();
//                    try {
//                        filteredItems.clear();
//                        filteredItems.addAll(Data.getInstance().getCustomers());
//                    }catch(Exception exception){
//                        exception.printStackTrace();
//                        Logger.logError(exception.getMessage(), exception);
//                    }

                    System.out.println("Executed this part");
                }catch(SQLException sqlException){
                    sqlException.printStackTrace();
                }

                System.out.println("Executed this part before updating the text");
                totalDiscount.setText("Rs." + String.format("%.2f", cumulativeTotalDiscount));
                grandTotal.setText("Rs." + String.format("%.2f", cumulativeGrandTotal));

                double dueBalanceValue = cumulativeReceivedFund - cumulativeGrandTotal;
                balance.setText("Rs." + String.format("%.2f", dueBalanceValue));

                mainTable.refresh();

                if (!itemList.isEmpty()) {
                    checkOutButton.setDisable(true);
                    checkoutJustCompleted = true;
                }

                mainTable.getItems().clear();
                totalCost.setText("-");
                totalDiscount.setText("-");
                grandTotal.setText("-");
                balance.setText("-");
                selectedCustomer = null;

                customerComboBox.getSelectionModel().clearSelection();
                itemComboBox.getSelectionModel().clearSelection();
                itemId.setText("");
                checkOutButton.setDisable(false);

            } catch (NumberFormatException ex) {
                System.out.println("Invalid input in received fund or discount field.");
            }

            payFromPoints.setSelected(false);
            payFromRefundAmount.setSelected(false);
            savePoints.setSelected(false);
            receivedFund.setText("");

            try {
                Data.getInstance().refreshLiableCustomers();
                if(filteredItems != null) {
                    filteredItems.setPredicate(customer -> true);
                }
//                customerComboBox.getSelectionModel().clearSelection();
//                if (!customerComboBox.isFocused()) {
//                    customerComboBox.getEditor().setText("");
//                }
            }catch(SQLException sqlException){
                sqlException.printStackTrace();
            }

            checkoutItems.setPredicate(itemDetail -> itemDetail.getRemainingQty() > 0);
        });

        if (!receivedFund.getText().trim().equals("")) {
            checkOutButton.setDisable(false);
        }

        customerComboBox.getSelectionModel().clearSelection();

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
                itemMessageContainer.setMaxWidth(220);
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
            itemMessageContainer.setMaxWidth(220);
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
        itemMessageContainer.setPrefWidth(220);
        itemMessageContainer.setWrapText(true);
        itemMessageContainer.setStyle("-fx-font-size: 19px; -fx-fill: white;");
        itemMessageContainer.setAlignment(Pos.CENTER_LEFT);
        itemMessageContainer.setMaxWidth(Double.MAX_VALUE);
        itemMessageContainer.setMaxWidth(Double.MAX_VALUE);

        actionSection.setSpacing(10);
        actionSection.setPrefWidth(600);
        actionSection.setAlignment(Pos.TOP_RIGHT);
        actionSection.setPadding(new Insets(0, 0, 10, 0));
        actionSection.getChildren().addAll(deleteRow, remove);
        actionSection.setMaxWidth(Double.MAX_VALUE);
        actionSection.setAlignment(Pos.CENTER_RIGHT);

        HBox pointsAndRefundsContainer = new HBox();
        FontIcon pointsIconLarge = new FontIcon(FontAwesomeSolid.COINS);
        pointsIconLarge.setFill(Paint.valueOf("#BBAA00"));
        pointsIconLarge.setIconSize(20);
        points = new Label("00.0");
        points.setMinWidth(250.05D);
        points.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        points.setGraphic(pointsIconLarge);
        points.setGraphicTextGap(10.0D);

        FontIcon refundIcon = new FontIcon(FontAwesomeSolid.UNDO);
        refundIcon.setFill(Paint.valueOf("#BBAA00"));
        refundIcon.setIconSize(20);
        refunds = new Label("00.0");
        refunds.setMinWidth(250.0D);
        refunds.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        refunds.setGraphic(refundIcon);
        refunds.setGraphicTextGap(10.0D);

        pointsAndRefundsContainer.getChildren().addAll(points, refunds);

        AnchorPane floatingContainer = new AnchorPane();
        AnchorPane.setBottomAnchor(actionSection, 0.0);
        AnchorPane.setRightAnchor(actionSection, 0.0);
        AnchorPane.setLeftAnchor(pointsAndRefundsContainer, 10.0D);
        floatingContainer.setPrefWidth(100);
        floatingContainer.getChildren().addAll(pointsAndRefundsContainer, actionSection);
        floatingContainer.setPadding(new Insets(10, 0, 0, 0));

        // Show the most selling items
        VBox sellingItemContainer = new VBox();
        Text sellingItemText = new Text("The most selling items");
        sellingItemText.getStyleClass().add("heading-texts");
        sellingItemText.setStyle("-fx-font-size: 19px;");

        ScrollPane scrollPane = new ScrollPane();

        HBox sellingItemCardContainer = new HBox();
        sellingItemCardContainer.setPadding(new Insets(8 ));
        List<SoldProducts> soldData = Connection.getInstance().getTop10SellingProducts();
        System.out.println("Top 5 size: " + soldData.size());

        for (SoldProducts sold : soldData) {
            ItemDetail detail = Connection.getInstance().getItemDetail(sold.getItemId());
            if (detail != null) {
                EasyCheckoutItem card = new EasyCheckoutItem(detail, item -> {
                    itemComboBox.getSelectionModel().select(item);
                });
                sellingItemCardContainer.getChildren().add(card);
            }
        }

        sellingItemCardContainer.setMaxWidth(Double.MAX_VALUE);
        sellingItemCardContainer.setAlignment(Pos.CENTER);
        sellingItemCardContainer.setSpacing(10);
        sellingItemCardContainer.setStyle("-fx-background-color: transparent;");

        scrollPane.setContent(sellingItemCardContainer);
        scrollPane.setMinHeight(80.0D);
        scrollPane.setMaxHeight(80.0D);

//        sellingItemContainer.setPadding(new Insets(-30, 0, 0, 0));
//        sellingItemContainer.setAlignment(Pos.CENTER);
//        sellingItemContainer.getChildren().addAll(sellingItemText, sellingItemCardContainer);
//        sellingItemContainer.setMaxHeight(140);
//        sellingItemContainer.setMinHeight(140);
//        sellingItemContainer.setStyle("-fx-background-color: #222;");

        // The Footer
        VBox wholeBottomSec = new VBox();
        VBox mainFooterSec = new VBox();
        mainFooterSec.setAlignment(Pos.CENTER);
        mainFooterSec.getStyleClass().add("footer-section");
        mainFooterSec.setPadding(new Insets(10, 0, 10, 20));
        HBox bottomSection = new HBox();

        bottomSection.setPrefHeight(30);
        bottomSection.setSpacing(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setMaxWidth(Double.MAX_VALUE);

        HBox balanceSec = new HBox();
        balanceSec.setAlignment(Pos.CENTER);
        balanceSec.setPadding(new Insets(10, 0, 0, 0));
        balanceSec.setSpacing(10);
        balanceSec.getChildren().addAll(savePoints, balanceTxt, balance, checkOutButton);

        // container for check boxes to align them vertically for easy use and reserve space to display numbers
        VBox checkBoxContainer = new VBox();
        checkBoxContainer.getChildren().addAll(payFromRefundAmount, payFromPoints);

        totalCost.setMinWidth(150.0D);
        totalDiscount.setMinWidth(150.0D);
        grandTotal.setMinWidth(150.0D);

        GridPane formAndDataContainer = new GridPane();

        formAndDataContainer.add(payFromRefundAmount, 0, 0);
        formAndDataContainer.add(payFromPoints, 0, 1);

        formAndDataContainer.add(discountForAll, 1, 0);
        formAndDataContainer.add(receivedFund, 1, 1);

        formAndDataContainer.add(savePoints, 2, 1);

        formAndDataContainer.add(totalCostTxt, 3, 0);
        formAndDataContainer.add(totalCost, 4, 0);

        formAndDataContainer.add(totalDiscountTxt, 3, 1);
        formAndDataContainer.add(totalDiscount, 4, 1);

        formAndDataContainer.add(grandTotalTxt, 5, 0);
        formAndDataContainer.add(grandTotal, 6, 0);

        formAndDataContainer.add(checkOutButton, 5, 1);

        formAndDataContainer.setHgap(10.0D);
        formAndDataContainer.setVgap(5.0D);

//        FlowPane checkoutDetailsContainer = new FlowPane();
//        checkoutDetailsContainer.getChildren().addAll(totalCostTxt, totalCost, totalDiscountTxt, totalDiscount, grandTotalTxt, grandTotal);

        VBox balanceContainer = new VBox();
        balanceContainer.setPadding(new Insets(0.0D, 100.0D, 0.0D, 0.0D));
        balanceContainer.getChildren().addAll(balanceTxt, balance);
        balanceContainer.setAlignment(Pos.CENTER_LEFT);

        bottomSection.getChildren().addAll(formAndDataContainer, balanceContainer);
        bottomSection.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(formAndDataContainer, Priority.ALWAYS);
        mainFooterSec.getChildren().addAll(bottomSection, balanceSec);
        wholeBottomSec.getChildren().addAll(mainFooterSec);
        headerSection.getChildren().addAll(navbar);
        inputSection.getChildren().addAll(itemTxt, itemComboBox,itemId, amount, discount, addButton, theSpace, customerSearchBox, customerTxt, customerPointsContainer, firstName, lastName, phone, eMail, addCustomerSec, itemMessageContainer);
        inputSection.setAlignment(Pos.CENTER_LEFT);
        Region theFormMessageSpace = new Region();
        theFormMessageSpace.setPrefHeight(5);
        inputVerticalSec.getChildren().addAll(inputSection, theFormMessageSpace);

        // The center complete container
        HBox mainCenterContainerParent = new HBox( );
        mainCenterContainerParent.setAlignment(Pos.CENTER);
        VBox mainCenterContainer = new VBox();
        mainCenterContainer.getChildren().addAll(mainTable, floatingContainer, scrollPane);
        mainCenterContainer.setPadding(new Insets(0.0D, 0.0D, 20.0D, 0.0D));
        VBox.setVgrow(mainTable, Priority.ALWAYS);

        centerContainer.getChildren().addAll(inputVerticalSec);
        mainCenterContainerParent.getChildren().addAll(centerContainer, mainCenterContainer);

        // Assigning each sections to the main section
        mainLayout.setTop(headerSection);
        mainLayout.setCenter(mainCenterContainerParent);
        mainLayout.setBottom(wholeBottomSec);

        Session.addListener(this);
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
        itemMessageContainer.setText(message);
        itemMessageContainer.setVisible(true);
        itemMessageContainer.setManaged(true);
        itemMessageContainer.setMaxWidth(220);
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
        itemMessageContainer.setAlignment(Pos.CENTER_LEFT);

        itemsDeletingMsgTimer = new PauseTransition(Duration.seconds(2));
        itemsDeletingMsgTimer.setOnFinished(ev -> {
            itemMessageContainer.setText("");
            itemMessageContainer.setStyle("");
        });
        itemsDeletingMsgTimer.play();

        if (!inputVerticalSec.getChildren().contains(itemMessageContainer)) {
            inputVerticalSec.getChildren().add(itemMessageContainer);
        }
        if (messageTimer != null) {
            messageTimer.stop();
        }

        messageTimer = new PauseTransition(Duration.seconds(2));
        messageTimer.setOnFinished(ev -> itemMessageContainer.setText(""));
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


    @Override
    public void onLoggedIn() {
        User user = Session.getInstance().getSessionUser();
        if (user != null){
            priceCol.setVisible(user.getRole().equals("Admin"));
        }
    }

    @Override
    public void onLoggedOut() {
        User user = Session.getInstance().getSessionUser();
        if (user != null) {
            priceCol.setVisible(user.getRole().equals("Admin"));
        }
    }
}