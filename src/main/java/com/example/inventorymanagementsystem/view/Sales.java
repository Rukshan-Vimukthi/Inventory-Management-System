package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.CustomerSale;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.services.utils.Logger;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.ItemPreview;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Sales extends VBox implements ThemeObserver {
    FormField<DatePicker, String> datePicker;
    FormField<ComboBox, Customer> customer;

    FormField<DatePicker, String> saleDatePicker;
    FormField<ComboBox, Customer> customerSelector;
    CustomerSale selectedCustomerSale;
    FormField<TextField, String> itemID;
    ItemPreview itemPreview;
    TableView<CustomerSale> customerSaleTableView;

    Customer selectedCustomer;

    Label messageLabel;

    Button refreshSalesData = new Button("Refresh");
    public Sales() throws SQLException {
        super();
        messageLabel = new Label("");
        messageLabel.setStyle(
                "-fx-background-color: #002288; " +
                        "-fx-border: solid; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-color: #0055FF; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-padding: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 20px;");
        messageLabel.setTextFill(Paint.valueOf("#00CCFF"));
        messageLabel.setVisible(false);
        this.setPadding(new Insets(10.0D));

        datePicker = new FormField<>("Date", DatePicker.class);
        try {
            customer = new FormField<>("Customer", ComboBox.class, Data.getInstance().getCustomers());
        }catch(SQLException e){
            customer = new FormField<>("Customer", ComboBox.class);
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }

        customer.getComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
                try {
                    Data.getInstance().refreshCustomerSales((String)saleDatePicker.getValue(), newValue.getId());
                    selectedCustomer = newValue;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        customer.getComboBox().setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
            @Override
            public ListCell<Customer> call(ListView<Customer> param) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty && item != null){
                            setText(item.getFirstName() + " " + item.getLastName());
                        }
                    }
                };
            }
        });

        FontIcon refreshIcon = new FontIcon(FontAwesomeSolid.UNDO);
        refreshIcon.setFill(Paint.valueOf("#FFF"));
        refreshSalesData = new Button("Refresh");
        refreshSalesData.setGraphic(refreshIcon);
        refreshSalesData.getStyleClass().add("button-primary");
        refreshSalesData.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    if (saleDatePicker != null && selectedCustomer != null) {
                        Data.getInstance().refreshCustomerSales((String) saleDatePicker.getValue(), selectedCustomer.getId());
                    }else{
                        Data.getInstance().refreshCustomerSales(null, 0);
                    }
                }catch(SQLException exception){
                    exception.printStackTrace();
                }
            }
        });

        ((DatePicker)datePicker.getControl()).valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                try {
                    int customerID = 0;
                    Customer customer = customerSelector.getComboBox().getSelectionModel().getSelectedItem();
                    if (customer != null){
                        customerID = customer.getId();
                    }
                    Data.getInstance().refreshCustomerSales(newValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerID);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        HBox header = new HBox();
        header.setSpacing(10.0D);
        header.getChildren().addAll(datePicker, customer, refreshSalesData);
        HBox body = new HBox();

        VBox tableAndPreviewContainer = new VBox();
        itemPreview = new ItemPreview();

        customerSaleTableView = new TableView<>();

        TableColumn<CustomerSale, String> customerName = new TableColumn<>("Name");
        customerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(param.getValue().getCustomer().getFirstName() + " " + param.getValue().getCustomer().getLastName());
            }
        });

        TableColumn<CustomerSale, String> saleDateColumn = new TableColumn<>("Date");
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CustomerSale, String> itemIDColumn = new TableColumn<>("Item ID");
        itemIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getItemDetails().getItemHasSizeID()));
            }
        });

        TableColumn<CustomerSale, String> itemNameColumn = new TableColumn<>("Item Name");
        itemNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getItemDetails().getName()));
            }
        });

        TableColumn<CustomerSale, String> customerHasItemHasSizeIDColumn = new TableColumn<>("Checkout ID");
        customerHasItemHasSizeIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getCheckoutItem().getId()));
            }
        });

        TableColumn<CustomerSale, String> saleIDColumn = new TableColumn<>("Sale ID");
        saleIDColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getSaleID()));
            }
        });

        TableColumn<CustomerSale, String> itemQtyColumn = new TableColumn<>("Item Qty");
        itemQtyColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getQuantity()));
            }
        });

        TableColumn<CustomerSale, Double> discountColumn = new TableColumn<>("Discount");
        discountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<CustomerSale, Double> param) {
                return new SimpleObjectProperty<>(param.getValue().getCheckoutItem().getDiscount());
            }
        });


        TableColumn<CustomerSale, String> paidAmountColumn = new TableColumn<>("Paid amount");
        paidAmountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getReceivedAmount()));
            }
        });


        TableColumn<CustomerSale, String> costColumn = new TableColumn<>("Item Cost");
        costColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CustomerSale, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CustomerSale, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getCost()));
            }
        });

        customerSaleTableView.getColumns().addAll(customerName, saleDateColumn, itemIDColumn, itemNameColumn, itemQtyColumn, paidAmountColumn, costColumn, discountColumn);

        VBox formContainer = new VBox();
        formContainer.setPadding(new Insets(5.0D));
        formContainer.setSpacing(10.0D);
        formContainer.setMaxWidth(200.0D);
        formContainer.setMinWidth(200.0D);

        saleDatePicker = new FormField<>("Date Picker", DatePicker.class);
        customerSelector = new FormField<>("Customer", ComboBox.class);

        try{
            customerSelector = new FormField<>("Customer", ComboBox.class, Data.getInstance().getCustomers());
        }catch(SQLException exception){
            exception.printStackTrace();
        }

        itemID = new FormField<>("Item", TextField.class);
        FormField<TextField, String> amount = new FormField<>("Returning Amount", TextField.class);
        Button returnItem = new Button("Return");
        returnItem.getStyleClass().add("button-info");
        returnItem.setOnAction(actionEvent -> {
            String date = String.valueOf(saleDatePicker.getValue());
            int itemHasSizeID = Integer.parseInt(String.valueOf(itemID.getValue()));
            int returnedQty = Integer.parseInt(String.valueOf(amount.getValue()));
            try {
                Connection.getInstance().returnItem(itemHasSizeID, date, selectedCustomerSale, returnedQty);
                messageLabel.setStyle(
                        "-fx-background-color: #002288; " +
                                "-fx-border: solid; " +
                                "-fx-border-width: 1px; " +
                                "-fx-border-color: #0055FF; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-background-radius: 10px; " +
                                "-fx-padding: 10px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 20px;");
                messageLabel.setTextFill(Paint.valueOf("#00CCFF"));
                showToast("Item Returned Successfully!");
            }catch(SQLException e){
                e.printStackTrace();
                messageLabel.setStyle(
                        "-fx-background-color: #050000; " +
                                "-fx-border: solid; " +
                                "-fx-border-width: 1px; " +
                                "-fx-border-color: #FF0000; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-background-radius: 10px; " +
                                "-fx-padding: 10px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 20px;");
                messageLabel.setTextFill(Paint.valueOf("#FF0000"));
                showToast("Item Return Failed!");
            }
        });

        returnItem.getStyleClass().add("button-primary");
        formContainer.setFillWidth(true);
        formContainer.getChildren().addAll(saleDatePicker, customerSelector, itemID, amount, returnItem, messageLabel);

        customerSaleTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CustomerSale>() {
            @Override
            public void changed(ObservableValue<? extends CustomerSale> observable, CustomerSale oldValue, CustomerSale newValue) {
                if (newValue != null) {
                    try {
                        selectedCustomerSale = newValue;
                        itemPreview.update(newValue.getItemDetails());
                        loadData(newValue.getCustomer(), newValue, String.valueOf(newValue.getItemDetails().getItemHasSizeID()), newValue.getDate());
                        newValue.getCheckoutItem().getId();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(selectedCustomerSale != null){
                    customerSaleTableView.getSelectionModel().select(selectedCustomerSale);
                }
            }
        });
        customerSaleTableView.setItems(Data.getInstance().getCustomerSales());

        tableAndPreviewContainer.getChildren().addAll(customerSaleTableView, itemPreview);
        tableAndPreviewContainer.setSpacing(10.0D);
        VBox.setVgrow(customerSaleTableView, Priority.ALWAYS);
        body.getChildren().addAll(tableAndPreviewContainer, formContainer);
        HBox.setHgrow(tableAndPreviewContainer, Priority.ALWAYS);
        VBox.setVgrow(body, Priority.ALWAYS);

        this.getChildren().addAll(header, body);
        this.setSpacing(10.0D);
    }

    public void loadData(Customer customer, CustomerSale customerSale, String itemID, String date){
        this.itemID.setValue(itemID);
        this.customerSelector.getComboBox().getSelectionModel().select(customer);
        this.customerSaleTableView.getSelectionModel().clearSelection();
        this.customerSaleTableView.getSelectionModel().select(customerSale);
        this.saleDatePicker.setValue(date);
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: #EEE; ");
        this.getStylesheets().remove(Constants.DARK_THEME_CSS);
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #222; ");
        this.getStylesheets().remove(Constants.LIGHT_THEME_CSS);
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }

    public void showToast(String message){
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), messageLabel);
                        fadeOut.setFromValue(1.0D);
                        fadeOut.setToValue(0.0D);
                        fadeOut.play();
                    }
                });
            }
        });

        thread.start();
    }
}
