package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.LiableCustomers;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class Liabilities extends HBox implements ThemeObserver {

    private double totalAmountOwedToShop = 0.0D;
    Label totalLiabilitiesAmount;
    Label totalLiableCustomersValue;
    Label totalPointsValue;
    Label accountsReceivableOnDate;
    Label pointsOnDate;
    ComboBox<String> pointsTimePicker;
    ComboBox<String> accountReceivableTimePicker;
    Label messageLabel;
    public Liabilities() throws SQLException {
        super();
        this.setPadding(new Insets(10.0D));
        this.setSpacing(10.0D);

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

        VBox cardsAndTableContainer = new VBox();
        HBox cardContainer = new HBox();

        Label totalLiabilitiesHeader = new Label("Total Liabilities");
        totalLiabilitiesHeader.getStyleClass().add("card-heading");
        totalLiabilitiesHeader.setStyle("-fx-text-fill: #FF0000;");
        totalLiabilitiesAmount = new Label("0");
        totalLiabilitiesAmount.getStyleClass().add("card-content");
        totalLiabilitiesAmount.setStyle("-fx-text-fill: #FF0000;");

        Card totalLiabilities = new Card(totalLiabilitiesHeader, totalLiabilitiesAmount, null);
        totalLiabilities.getStyleClass().add("card");
        totalLiabilities.setStyles("-fx-background-color: #000000; ");

        Label totalLiableCustomersHeader = new Label("Customers Liable");
        totalLiableCustomersHeader.getStyleClass().add("card-heading");
        totalLiableCustomersValue = new Label("0");
        totalLiableCustomersValue.getStyleClass().add("card-content");
        Card totalLiableCustomers = new Card(totalLiableCustomersHeader, totalLiableCustomersValue, null);
        totalLiableCustomers.getStyleClass().add("card");

        FontIcon pointsIcon = new FontIcon(FontAwesomeSolid.COINS);
        pointsIcon.setFill(Paint.valueOf("#FFDD00"));
//        pointsIcon.setIconSize(50);

        Label totalPointsHeader = new Label("Total Points");
        totalPointsHeader.setGraphic(pointsIcon);
        totalPointsHeader.setGraphicTextGap(10.0D);
        totalPointsHeader.getStyleClass().add("card-heading");
        totalPointsValue = new Label("0");
        totalPointsValue.getStyleClass().add("card-content");
//        totalPointsValue.setGraphic(pointsIcon);
//        totalPointsValue.setContentDisplay(ContentDisplay.TOP);
        Card totalPoints = new Card(totalPointsHeader, totalPointsValue, null);
        totalPoints.getStyleClass().add("card");

        HBox header = new HBox();
        Label accountReceivableOnSpecificDateLabel = new Label("Account Receivable");
        accountReceivableOnSpecificDateLabel.getStyleClass().add("card-heading");
        accountReceivableTimePicker = new ComboBox<>();

        accountReceivableTimePicker.getItems().addAll("Today", "Yesterday", "This week", "This month", "This year","Last week", "Last month", "Last year");
        accountReceivableTimePicker.getSelectionModel().select("Today");
        header.getChildren().addAll(accountReceivableOnSpecificDateLabel, accountReceivableTimePicker);
        header.setSpacing(10.0D);

        accountsReceivableOnDate = new Label(String.valueOf(Connection.getInstance().filterLiabilities("Today")));
        accountsReceivableOnDate.getStyleClass().add("card-content");

        accountReceivableTimePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Value changed!");
                try {
                    double totalAccountsReceivable = Connection.getInstance().filterLiabilities(accountReceivableTimePicker.getSelectionModel().getSelectedItem());
                    accountsReceivableOnDate.setText(String.valueOf(totalAccountsReceivable));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Card accountsReceivableOnSpecificDateCard = new Card(header, accountsReceivableOnDate, null);
        accountsReceivableOnSpecificDateCard.getStyleClass().add("card");

        HBox pointsReceivedOnHeader = new HBox();
        Label pointsOnSpecificDateLabel = new Label("Points Received");
        pointsOnSpecificDateLabel.getStyleClass().add("card-heading");
        pointsTimePicker = new ComboBox<>();
        pointsTimePicker.getItems().addAll("Today", "Yesterday", "This week", "This month", "This year","Last week", "Last month", "Last year");
        pointsTimePicker.getSelectionModel().select("Today");
        pointsReceivedOnHeader.getChildren().addAll(pointsOnSpecificDateLabel, pointsTimePicker);
        pointsReceivedOnHeader.setSpacing(10.0D);
        pointsOnDate = new Label(String.valueOf(Connection.getInstance().filterPoints("Today")));
        pointsOnDate.getStyleClass().add("card-content");

        pointsTimePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    double totalAccountsReceivable = Connection.getInstance().filterPoints(pointsTimePicker.getSelectionModel().getSelectedItem());
                    pointsOnDate.setText("%.2f".formatted(totalAccountsReceivable));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Card pointsOnSpecificDateCard = new Card(pointsReceivedOnHeader, pointsOnDate, null);
        pointsOnSpecificDateCard.getStyleClass().add("card");

        cardContainer.getChildren().addAll(
                totalLiabilities,
                totalLiableCustomers,
                totalPoints,
                accountsReceivableOnSpecificDateCard,
                pointsOnSpecificDateCard
        );
        cardContainer.setSpacing(10.0D);

        HBox refreshAllDataContainer = new HBox();

        TableView<LiableCustomers> liableCustomersTableView = new TableView<>();

        TableColumn<LiableCustomers, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(param.getValue().customerObject.get().getFirstName());
            }
        });

        TableColumn<LiableCustomers, String> lastNameColumn = new TableColumn<>("First Name");
        lastNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(param.getValue().customerObject.get().getLastName());
            }
        });

        TableColumn<LiableCustomers, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getCustomerObject().getPhone()));
            }
        });

        TableColumn<LiableCustomers, String> liableAmountColumn = new TableColumn<>("Liable Amount");
        liableAmountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty("%.2f".formatted(param.getValue().getLiableAmount()));
            }
        });

        TableColumn<LiableCustomers, Customer> pointsAvailableColumn = new TableColumn<>("Points Available");
        pointsAvailableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, Customer>, ObservableValue<Customer>>() {
            @Override
            public ObservableValue<Customer> call(TableColumn.CellDataFeatures<LiableCustomers, Customer> param) {
                return new SimpleObjectProperty<>(param.getValue().getCustomerObject());
            }
        });

        TableColumn<LiableCustomers, String> refundsAvailableColumn = new TableColumn<>("Refunds Available");
        refundsAvailableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getCustomerObject().getRefundAmount()));
            }
        });

        pointsAvailableColumn.setCellFactory(new Callback<TableColumn<LiableCustomers, Customer>, TableCell<LiableCustomers, Customer>>() {
            @Override
            public TableCell<LiableCustomers, Customer> call(TableColumn<LiableCustomers, Customer> param) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            FontIcon coinsIcon = new FontIcon(FontAwesomeSolid.COINS);
                            coinsIcon.setFill(Paint.valueOf("#FFDD00"));
                            this.setText(String.valueOf(item.getPoints()));
                            this.setGraphic(coinsIcon);
                            this.setGraphicTextGap(5.0D);
                        }
                    }
                };
            }
        });

        totalLiabilitiesAmount.setText(Constants.CURRENCY_UNIT + Data.getInstance().getTotalAccountsReceivable());
        totalLiableCustomersValue.setText(String.valueOf(Data.getInstance().getLiableCustomers().size()));
        liableCustomersTableView.getColumns().addAll(firstNameColumn, lastNameColumn, phoneNumberColumn, liableAmountColumn, pointsAvailableColumn, refundsAvailableColumn);

        liableCustomersTableView.setItems(Data.getInstance().getLiableCustomers());
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        LineChart<Number, Number> accountsReceivableProgress = new LineChart<>(xAxis, yAxis);
        accountsReceivableProgress.setTitle("Accounts Receivables and Points");
        accountsReceivableProgress.getStyleClass().add("account-receivable-and-points");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");

        series.getData().add(new XYChart.Data<>(1, 10));
        series.getData().add(new XYChart.Data<>(2, 15));
        series.getData().add(new XYChart.Data<>(3, 5));
        series.getData().add(new XYChart.Data<>(4, 40));
        series.getData().add(new XYChart.Data<>(5, 35));
        series.getData().add(new XYChart.Data<>(6, 20));
        series.getData().add(new XYChart.Data<>(7, 1));

        accountsReceivableProgress.getData().add(series);

        Button refresh = new Button("Refresh");
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshAllData();
            }
        });
        refresh.getStyleClass().add("primary-button");
        refreshAllDataContainer.getChildren().add(refresh);


        HBox tableAndFormContainer = new HBox();

        VBox formContainer = new VBox();
        formContainer.setSpacing(10.0D);

        FormField<ComboBox, Customer> customer = new FormField<>("Customer", ComboBox.class, Data.getInstance().getCustomers());
        FormField<TextField, String> amountToClear = new FormField<>("Amount To Reduce", TextField.class);
//        FormField<ComboBox, ItemDetail> boughtItem = new FormField<>("Item", ComboBox.class, Data.getInstance().getCustomerLiableItems());

//        ComboBox<ItemDetail> boughtItems = new ComboBox<>();
//        boughtItems.setItems(Data.getInstance().getCustomerLiableItems());

        Button clearDebt = new Button("Pay Debt");
        clearDebt.setStyle("-fx-background-color: #00AA00; -fx-text-fill: #FFF");
        clearDebt.setOnAction(actionEvent -> {
            try{
                String message = Connection.getInstance().clearCustomerDebt(customer.getComboBox().getSelectionModel().getSelectedItem(), Double.parseDouble(String.valueOf(amountToClear.getValue())), 0, false);
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
                showToast(message);
            }catch(SQLException e){
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
                showToast("Clearing Debt Unsuccessful!");
                e.printStackTrace();
            }
//            Customer selectedCustomer = (Customer)customer.getValue();
//            double amount = Double.parseDouble(String.valueOf(amountToClear.getValue()));
//            System.out.println("Selected customer: " + selectedCustomer.getFirstName());
//            System.out.println("Amount to be clear: " + amount);
        });

        Button clearDebtUsingPoints = new Button("Pay Debt From Points");
        clearDebtUsingPoints.setStyle("-fx-background-color: #FFBB00; -fx-text-fill: #150500");
        clearDebtUsingPoints.setDisable(true);
        clearDebtUsingPoints.setOnAction(actionEvent -> {
            try{
                String message = Connection.getInstance().clearCustomerDebt(customer.getComboBox().getSelectionModel().getSelectedItem(), Double.parseDouble(String.valueOf(amountToClear.getValue())), 0, true);
                showToast(message);
            }catch(SQLException e){
                e.printStackTrace();
            }
//            Customer selectedCustomer = (Customer)customer.getValue();
//            double amount = Double.parseDouble(String.valueOf(amountToClear.getValue()));
//            System.out.println("Selected customer: " + selectedCustomer.getFirstName());
//            System.out.println("Amount to be clear: " + amount);
        });

        VBox footer = new VBox();
        footer.setFillWidth(true);
        footer.getChildren().addAll(clearDebt, clearDebtUsingPoints);
        footer.setSpacing(10.0D);

        customer.getComboBox().getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
                if (newValue != null) {
                    System.out.println("Selected Customer name to pay off debt: " + newValue.getFirstName());
                    clearDebtUsingPoints.setDisable(!(newValue.getPoints() > 0.0D));
                }
            }
        });

        formContainer.getChildren().addAll(customer, amountToClear, footer, messageLabel);
        formContainer.setMinWidth(200.0D);
        formContainer.setMaxWidth(200.0D);
        formContainer.setSpacing(10.0D);
        tableAndFormContainer.getChildren().addAll(liableCustomersTableView, formContainer);
        tableAndFormContainer.setSpacing(10.0D);

        HBox.setHgrow(liableCustomersTableView, Priority.ALWAYS);

        cardsAndTableContainer.getChildren().addAll(cardContainer, refreshAllDataContainer, tableAndFormContainer);
        cardsAndTableContainer.setSpacing(10.0D);
        VBox.setVgrow(tableAndFormContainer, Priority.ALWAYS);

        VBox leftSideBar = new VBox();
        leftSideBar.setMinWidth(300.0D);
        leftSideBar.setMaxWidth(300.0D);
        this.getChildren().addAll(cardsAndTableContainer);
        HBox.setHgrow(cardsAndTableContainer, Priority.ALWAYS);

        refreshAllData();
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

    private void refreshAllData(){
        try {
            Data.getInstance().refreshLiableCustomers();

            totalLiableCustomersValue.setText(String.valueOf(Data.getInstance().getTotalLiableCustomers()));
            accountsReceivableOnDate.setText(
                    String.valueOf(
                            Connection.getInstance()
                                    .filterLiabilities(
                                            accountReceivableTimePicker
                                                    .getSelectionModel()
                                                    .getSelectedItem())));

            double totalAccountsReceivable = Connection.getInstance()
                    .filterPoints(
                            pointsTimePicker
                                    .getSelectionModel()
                                    .getSelectedItem());
            pointsOnDate.setText("%.2f".formatted(totalAccountsReceivable));

            double totalPoints = 0.0D;
            for (Customer customer: Data.getInstance().getCustomers()){
                totalPoints += customer.getPoints();
            }
            System.out.println("Total points of all customer: " + totalPoints);
            totalPointsValue.setText(String.valueOf(totalPoints));
            totalLiabilitiesAmount.setText(String.valueOf(Data.getInstance().getTotalAccountsReceivable()));
        }catch(SQLException e){
            e.printStackTrace();
        }
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
