package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.InventoryManagementApplicationController;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.ChartKeeper;
import com.example.inventorymanagementsystem.view.components.ChartTableToggleComponent;
import com.example.inventorymanagementsystem.view.components.TableKeeper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.net.ConnectException;
import java.security.cert.PolicyNode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Flow;

import static com.example.inventorymanagementsystem.view.components.ChartKeeper.*;

public class Analytics extends VBox implements ThemeObserver {
    private VBox mainLayout;
    @FXML
    private CategoryAxis xAxis; // Date axis
    @FXML
    private NumberAxis yAxis;   // Price axis
    @FXML
    private LineChart<String, Number> salesLineChart;
    private Connection connection;
    @FXML
    TableView<User> userTable = new TableView<>();
    @FXML
    private ObservableList<User> fullUserList;
    @FXML
    private TextField searchBar;
    @FXML
    private Button searchBtn;
    @FXML
    private Button showAllBtn;
    private final InventoryManagementApplicationController.NavigationHandler navigationHandler;
    VBox salesOverTimeSec;
    ComboBox<String> dateRange;
    VBox navbar;

    public Analytics(InventoryManagementApplicationController.NavigationHandler navigationHandler) {
        super();
        this.navigationHandler = navigationHandler;

        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );
        Connection dbConnection = Connection.getInstance();
        connection = Connection.getInstance();

        navbar = new VBox();
        navbar.setMaxWidth(Double.MAX_VALUE);
        navbar.setPadding(new Insets(20, 0, 20, 0));
        navbar.setStyle("-fx-background-radius: 10px;");
        navbar.getStyleClass().add("nav-bar");
        navbar.setSpacing(5.5);
        navbar.setAlignment(Pos.CENTER);

        Text heading = new Text("Inventory Analytics");
        heading.getStyleClass().add("heading-texts");
        heading.setTextAlignment(TextAlignment.CENTER);

        Text subHeading = new Text("Real-time insights into your inventory, sales, and operations performance.");
        subHeading.getStyleClass().add("paragraph-texts");

        HBox headerContainer = new HBox();
        headerContainer.setPadding(new Insets(20, 0, 0, 0));
        headerContainer.setSpacing(30);
        headerContainer.setAlignment(Pos.CENTER);

        ComboBox<String> dateRange = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Today", "Yesterday", "Last 7 Days", "This Month",
                                "Last Month", "This Year", "Last Year"
                        )
                )
        );
        dateRange.setPromptText("Date Range");
        dateRange.getStyleClass().add("default-dropdowns");

        ComboBox<String> export = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Export as PDF",
                                "Export as Excel"
                        )
                )
        );
        export.setPromptText("Export");
        export.getStyleClass().add("default-dropdowns");

        Button refresh = new Button("Refresh ↻");
        refresh.getStyleClass().add("default-buttons");

        FlowPane summaryCardContainer = new FlowPane();
        summaryCardContainer.setHgap(20);
        summaryCardContainer.setVgap(20);
        summaryCardContainer.setPadding(new Insets(30, 0, 0, 0));
        summaryCardContainer.setAlignment(Pos.CENTER);
        summaryCardContainer.setMaxWidth(Double.MAX_VALUE);

        int totalProductSum = dbConnection.getTotalProducts();
        // Total product Card
        Text totalProductsTxt = new Text("📦 Total Products in inventory");
        totalProductsTxt.setTextAlignment(TextAlignment.CENTER);
        totalProductsTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        totalProductsTxt.getStyleClass().add("sub-cards-heading");
        Label totalProducts = new Label(totalProductSum + " Products are Available");
        totalProducts.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region percentage = new Region();
        Card productCard = new Card(totalProductsTxt, totalProducts, percentage);
        productCard.getStyleClass().add("summary-cards");

        int totalProductValue = dbConnection.getTotalProductValue();
        int remainingItemAmount = dbConnection.getRemainingProductsSum();
        // Total Inventory Value card
        Text inventoryValueTxt = new Text("💰 Total Inventory Value");
        inventoryValueTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; ");
        inventoryValueTxt.getStyleClass().add("sub-cards-heading");
        Label inventoryValue = new Label("$ " + remainingItemAmount * totalProductValue + " of Value");
        inventoryValue.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region expectedValue = new Region();
        Card inventoryValueCard = new Card(inventoryValueTxt, inventoryValue, expectedValue);
        inventoryValueCard.getStyleClass().add("summary-cards");

        int lowStokeProducts = Connection.getLowStockItemCount(dbConnection);
        // Total sales Card
        Text lowStokeTxt = new Text("🔸 Low Stokes");
        lowStokeTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        lowStokeTxt.getStyleClass().add("sub-cards-heading");
        Label lowStoke = new Label(lowStokeProducts + " Items");
        lowStoke.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region currentAmount = new Region();
        Card lowStockCard = new Card(lowStokeTxt, lowStoke, currentAmount);
        lowStockCard.getStyleClass().add("summary-cards");

        Text fastTxt = new Text("✈ Fast Moving Items");
        fastTxt.getStyleClass().add("sub-cards-heading");
        fastTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Text fastMoving = new Text();
        fastMoving.setFill(Color.WHITE);

        Map<String, List<SoldProducts>> salesData = connection.getTopAndBottomSellingProducts();
        if (salesData != null && salesData.containsKey("top")) {
            List<SoldProducts> topList = salesData.get("top");
            if (topList != null && !topList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder("");
                for (SoldProducts ps : topList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName)
                            .append(", ");

                }
                fastMoving.setText(textBuilder.toString());
                fastMoving.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            } else {
                fastMoving.setText("No top selling products found.");
            }
        }
        Region slowMoving = new Region();
        Card fastCard = new Card(fastTxt, fastMoving, slowMoving);
        fastCard.getStyleClass().add("summary-cards");

        Text slowText = new Text("🐌 Slow Moving Items");
        slowText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        slowText.getStyleClass().add("sub-cards-heading");
        Text slowMovingItems = new Text();
        slowMovingItems.setFill(Color.WHITE);

        if (salesData != null && salesData.containsKey("bottom")) {
            List<SoldProducts> bottomList = salesData.get("bottom");
            if (bottomList != null && !bottomList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder("");
                for (SoldProducts ps : bottomList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName)
                            .append(", ");

                }
                slowMovingItems.setText(textBuilder.toString());
                slowMovingItems.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            } else {
                slowMovingItems.setText("No top selling products found.");
            }
        }
        Region slowMovingItemSpace = new Region();
        Card slowCard = new Card(slowText, slowMovingItems, slowMovingItemSpace);
        slowCard.getStyleClass().add("summary-cards");

        summaryCardContainer.getChildren().addAll(productCard, inventoryValueCard, lowStockCard, fastCard, slowCard);

        VBox stockAnalytics = new VBox();
        stockAnalytics.setMaxWidth(Double.MAX_VALUE);

        stockAnalytics.setAlignment(Pos.TOP_CENTER);
        stockAnalytics.setPadding(new Insets(40, 0, 20, 30));

        // Current Stock Section
        Text currentStockTxt = new Text("Current Stock Levels");
        currentStockTxt.getStyleClass().add("heading-texts");
        currentStockTxt.setTextAlignment(TextAlignment.CENTER);

        ChartTableToggleComponent stockToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getStockLevelChart(),
                TableKeeper.getStockLevelTable()
        );
        stockToggleComponent.setMaxWidth(Double.MAX_VALUE);
        stockToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        ChartTableToggleComponent reorderToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getReorderAlertChart(connection),
                TableKeeper.getReorderAlertTable()
        );
        reorderToggleComponent.setMaxWidth(Double.MAX_VALUE);
        reorderToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        HBox stockContainer = new HBox(10);
        HBox.setHgrow(stockToggleComponent, Priority.ALWAYS);
        HBox.setHgrow(reorderToggleComponent, Priority.ALWAYS);
        stockContainer.setAlignment(Pos.TOP_CENTER);
        stockContainer.setMaxWidth(Double.MAX_VALUE);
        stockContainer.setPadding(new Insets(30, 0, 20, 0));
        stockContainer.setAlignment(Pos.CENTER);
        stockContainer.getChildren().addAll(stockToggleComponent, reorderToggleComponent);

        stockAnalytics.getChildren().addAll(currentStockTxt, stockContainer);
        stockAnalytics.setMaxWidth(Double.MAX_VALUE);

        // Current Stock Section
        Text saleHeading = new Text("Sales Analytics");
        saleHeading.getStyleClass().add("heading-texts");
        saleHeading.setTextAlignment(TextAlignment.CENTER);

        // Top Selling items card

        VBox topSoldItemCard = new VBox();
        Text topSellingTxt= new Text("\uD83E\uDD47 Top-Selling Product");
        topSellingTxt.getStyleClass().add("sub-cards-heading");

        Text topSoldProduct = new Text();

        Map<String, List<SoldProducts>> topSalesData = connection.getTopAndBottomSellingProducts();
        if (topSalesData != null && topSalesData.containsKey("top")) {
            List<SoldProducts> topList = topSalesData.get("top");
            if (topList != null && !topList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder();
                for (SoldProducts ps : topList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName)
                            .append(" :- Sold: ").append(ps.getTotalSold());
                }
                topSoldProduct.setText(textBuilder.toString());
            } else {
                topSoldProduct.setText("No top selling products found.");
            }
        }

        topSoldProduct.getStyleClass().add("sub-cards-value");
        topSoldItemCard.getStyleClass().add("summary-cards");
        topSoldItemCard.setAlignment(Pos.CENTER);
        topSoldItemCard.getChildren().addAll(topSellingTxt, topSoldProduct);

        // Top Selling items card
        int totalSoldQty = dbConnection.getTotalSoldQuantity();
        VBox soldUnitCard = new VBox();
        Text unitSoldTxt= new Text("\uD83D\uDD01 Units Sold");
        unitSoldTxt.getStyleClass().add("sub-cards-heading");
        Text soldUnits = new Text("Total Ordered Quantity " + totalSoldQty);
        soldUnits.getStyleClass().add("sub-cards-value");
        soldUnitCard.getStyleClass().add("summary-cards");
        soldUnitCard.setAlignment(Pos.CENTER);
        soldUnitCard.getChildren().addAll(unitSoldTxt, soldUnits);

        int totalCustomers = dbConnection.getTotalCustomers();
        // Total customers card
        VBox totalCustomersCard = new VBox();
        Text totalCustomersTxt= new Text("\uD83D\uDC65 Total Customers");
        totalCustomersTxt.getStyleClass().add("sub-cards-heading");
        Text customersAmount = new Text(totalCustomers + " Customers");
        customersAmount.getStyleClass().add("sub-cards-value");
        totalCustomersCard.getStyleClass().add("summary-cards");
        totalCustomersCard.setAlignment(Pos.CENTER);
        totalCustomersCard.getChildren().addAll(totalCustomersTxt, customersAmount);

        // Total customers card
        int orderedItemsValue = dbConnection.getOrderedQuantityValue();
        VBox orderFullFilledCard = new VBox();
        Text revenueTxt= new Text("\uD83D\uDCE6 Revenue");
        revenueTxt.getStyleClass().add("sub-cards-heading");
        Text revenue = new Text("$ " +( totalSoldQty * orderedItemsValue) + " of revenue");
        revenue.getStyleClass().add("sub-cards-value");
        orderFullFilledCard.getStyleClass().add("summary-cards");
        orderFullFilledCard.setAlignment(Pos.CENTER);
        orderFullFilledCard.getChildren().addAll(revenueTxt, revenue);

        FlowPane salesCardContainer = new FlowPane();
        salesCardContainer.setHgap(20);
        salesCardContainer.setVgap(20);
        salesCardContainer.setMaxWidth(Double.MAX_VALUE);
        salesCardContainer.setAlignment(Pos.CENTER);
        salesCardContainer.setPadding(new Insets(30, 0, 30, 0));
        salesCardContainer.getChildren().addAll(topSoldItemCard, soldUnitCard ,totalCustomersCard, orderFullFilledCard);

        VBox saleAnalyticsSec = new VBox();
        saleAnalyticsSec.setAlignment(Pos.CENTER);
        saleAnalyticsSec.setMaxWidth(Double.MAX_VALUE);

        VBox salesOverTimeSec = new VBox();
        HBox salesSection = new HBox();
        HBox revenueUnitSec = new HBox();

        dateRange.setValue("This Year");
        // Shows the sales
        dateRange.setOnAction(e -> {
            String selected = dateRange.getValue();
            LineChart<String, Number> chart = ChartKeeper.getSalesChart(connection, selected);
            TableView<TableKeeper.SalesRow> table = TableKeeper.getSalesTable(selected);

            salesSection.getChildren().clear();

            boolean chartEmpty = chart.getData().isEmpty();
            boolean tableEmpty = table.getItems().isEmpty();

            if (chartEmpty && tableEmpty) {
                Label noDataMessage = new Label("No data found related to the selected date!");
                StackPane noDataPane = new StackPane(noDataMessage);
                noDataPane.setPrefSize(600, 600);
                salesSection.getChildren().clear();
                salesSection.getChildren().add(noDataPane);
            } else {
                ChartTableToggleComponent salesToggleComponent = new ChartTableToggleComponent(chart, table);
                salesToggleComponent.setMaxWidth(Double.MAX_VALUE);
                salesToggleComponent.setMinHeight(700);
                salesToggleComponent.setMaxWidth(1300);
                salesToggleComponent.setAlignment(Pos.CENTER);
                HBox.setHgrow(salesToggleComponent, Priority.ALWAYS);
                salesToggleComponent.prefHeightProperty().bind(salesSection.heightProperty().multiply(0.9));
                salesSection.getChildren().clear();
                salesSection.getChildren().add(salesToggleComponent);
                salesSection.setAlignment(Pos.CENTER);
            }
        });
        dateRange.getOnAction().handle(new ActionEvent());


        // Shows the revenue and the unit sold table & chart
        ChartTableToggleComponent revenueToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getRevenueChart(connection),
                TableKeeper.getRevenueTable()
        );
        revenueToggleComponent.setPrefWidth(1200);

        revenueUnitSec.setAlignment(Pos.TOP_CENTER);
        revenueUnitSec.setMaxWidth(Double.MAX_VALUE);
        revenueUnitSec.setPadding(new Insets(30, 0, 20, 0));
        revenueUnitSec.setAlignment(Pos.CENTER);
        revenueUnitSec.getChildren().addAll(revenueToggleComponent);
        salesOverTimeSec.getChildren().addAll(salesSection, revenueUnitSec);

        saleAnalyticsSec.getChildren().addAll(saleHeading, salesCardContainer, salesOverTimeSec);

        // Alerts section
        VBox alertsSec = new VBox();
        Text alertsTxt = new Text("\uD83D\uDEA8 Alerts & Exceptions");
        alertsTxt.getStyleClass().add("heading-texts");
        alertsSec.setAlignment(Pos.CENTER);
        alertsSec.setPadding(new Insets(30, 0, 10 ,0));
        alertsSec.setMaxWidth(Double.MAX_VALUE);

        FlowPane alertsContainer = new FlowPane();
        alertsContainer.setHgap(10.0);
        alertsContainer.setVgap(10.0);
        alertsContainer.setPadding(new Insets(20, 0, 20, 0));
        alertsContainer.setMaxWidth(Double.MAX_VALUE);
        alertsContainer.setAlignment(Pos.CENTER);
        alertsContainer.setMaxWidth(1300);
        alertsContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15); -fx-background-radius: 20;");

        // Alerts Low Stoke Card
        VBox lowStockAlertCard = new VBox();
        Text lowStockTxt = new Text("Alert! Low Stokes");
        lowStockTxt.getStyleClass().add("alert-heading");
        Text lowStocks = new Text("You currently have " + lowStokeProducts + " low stokes");
        lowStocks.getStyleClass().add("alert-content");
        lowStockAlertCard.getChildren().addAll(lowStockTxt, lowStocks);
        lowStockAlertCard.setSpacing(10);
        lowStockAlertCard.getStyleClass().add("alert-cards");

        // Alerts Over Stoke Card
        VBox overStockAlertCard = new VBox();
        Text overStockTxt = new Text("Alert! OverStock Warning!");
        overStockTxt.getStyleClass().add("alert-heading");
        overStockAlertCard.setSpacing(10);
        HBox overStockMessageContainer = new HBox();

        Text overStocks = new Text();

        Map<String, List<SoldProducts>> overStockData = connection.getTopAndBottomSellingProducts();
        if (overStockData != null && overStockData.containsKey("top")) {
            List<SoldProducts> topList = overStockData.get("top");
            if (topList != null && !topList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder();
                for (SoldProducts ps : topList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName)
                            .append(" :- Sold: ").append(ps.getTotalSold()).append(", ");
                }
                overStocks.setText(textBuilder.toString());
            } else {
                overStocks.setText("No top selling products found.");
            }
        }

        overStocks.getStyleClass().add("alert-content");
        overStocks.setFill(Color.web("#333333"));
        overStockMessageContainer.getChildren().addAll(overStocks);
        overStockAlertCard.getChildren().addAll(overStockTxt, overStockMessageContainer);
        overStockAlertCard.getStyleClass().add("alert-cards");

        alertsContainer.getChildren().addAll(lowStockAlertCard, overStockAlertCard);
        alertsSec.setSpacing(30.0);
        alertsSec.getChildren().addAll(alertsTxt, alertsContainer);

        // User analytics section
        VBox userAnalyticsSec = new VBox();
        userAnalyticsSec.setSpacing(30.0);

        Text userAnalyticsTxt = new Text("\uD83D\uDC64 User/Staff Analytics");
        userAnalyticsTxt.getStyleClass().add("heading-texts");
        userAnalyticsTxt.setFill(Color.web("#333333"));
        userAnalyticsTxt.setTextAlignment(TextAlignment.CENTER);

        HBox searchBarContainer = new HBox();
        searchBar = new TextField();
        searchBar.getStyleClass().add("default-text-areas");
        searchBar.setPromptText("Search name...");
        searchBar.setStyle("-fx-border-radius: 8 0 0 8;");
        searchBar.setPrefWidth(300);
        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().add("default-buttons");
        searchBtn.setStyle("-fx-background-radius: 0 8 8 0;");
        searchBtn.setOnAction(e -> searchUser());
        Region spaceForShowAll = new Region();
        spaceForShowAll.setMinWidth(30);
        Button showAllBtn = new Button("Show All");
        showAllBtn.getStyleClass().add("default-buttons");
        showAllBtn.setOnAction(e -> showAllUsers());
        searchBarContainer.getChildren().addAll(searchBar, searchBtn, spaceForShowAll, showAllBtn);
        searchBarContainer.setAlignment(Pos.CENTER);

        fullUserList = dbConnection.getUserDetail();
        userTable.setItems(fullUserList);

        // Creating the user table
        userTable.setMaxWidth(1300);
        userTable.setMaxHeight(200);

        TableColumn<User, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> emailCol = new TableColumn<>("E Mail");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> passwordCol = new TableColumn<>("Password");
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));

        userTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, emailCol, passwordCol);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        userAnalyticsSec.getChildren().addAll(userAnalyticsTxt, searchBarContainer, userTable);
        userAnalyticsSec.setMaxWidth(Double.MAX_VALUE);
        userAnalyticsSec.setAlignment(Pos.CENTER);
        userAnalyticsSec.setPadding(new Insets(50, 0, 20, 0));

        VBox visualArea = new VBox();
        visualArea.setSpacing(30.0);

        Text visualAreaTxt = new Text("\uD83D\uDCC8 Charts and Visuals Area");
        visualAreaTxt.getStyleClass().add("heading-texts");
        visualAreaTxt.setFill(Color.web("#333333"));
        visualAreaTxt.setTextAlignment(TextAlignment.CENTER);

        HBox stockReorderRow = new HBox(20);

        // Stock level & chart
        ChartTableToggleComponent stockToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getStockLevelChart(),
                TableKeeper.getStockLevelTable()
        );
        // Reorder chart & table
        ChartTableToggleComponent reorderToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getReorderAlertChart(connection),
                TableKeeper.getReorderAlertTable()
        );

        ChartTableToggleComponent salesToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getSalesChart(connection, ""),
                TableKeeper.getSalesTable("")
        );
        salesToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(salesToggleComponent2, Priority.ALWAYS);

        // Shows the revenue and the unit sold table & chart
        ChartTableToggleComponent revenueToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getRevenueChart(connection),
                TableKeeper.getRevenueTable()
        );

        stockToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        stockToggleComponent2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        reorderToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        reorderToggleComponent2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        revenueToggleComponent2.setMaxWidth(Double.MAX_VALUE);

        // Stock & Reorder row
        stockReorderRow.setAlignment(Pos.CENTER);
        stockReorderRow.setPadding(new Insets(10));
        HBox.setHgrow(stockToggleComponent2, Priority.ALWAYS);
        HBox.setHgrow(reorderToggleComponent2, Priority.ALWAYS);
        stockToggleComponent2.prefWidthProperty().bind(stockReorderRow.widthProperty().divide(2));
        reorderToggleComponent2.prefWidthProperty().bind(stockReorderRow.widthProperty().divide(2));
        stockReorderRow.getChildren().addAll(stockToggleComponent2, reorderToggleComponent2);

        // Sales & Revenue row
        HBox salesRevenueRow = new HBox(20);
        salesRevenueRow.setAlignment(Pos.CENTER);
        salesRevenueRow.setPadding(new Insets(10));
        HBox.setHgrow(revenueToggleComponent2, Priority.ALWAYS);
        stockToggleComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));
        reorderToggleComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));
        revenueToggleComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));

        // Show all the sales
        dateRange.setOnAction(e -> {
            String selected = dateRange.getValue();
            LineChart<String, Number> chart = ChartKeeper.getSalesChart(connection, selected);
            TableView<TableKeeper.SalesRow> table = TableKeeper.getSalesTable(selected);

            boolean chartEmpty = chart.getData().isEmpty();
            boolean tableEmpty = table.getItems().isEmpty();

            if (chartEmpty && tableEmpty) {
                Label noDataMessage = new Label("No data found related to the selected date");
                noDataMessage.setStyle("-fx-font-size: 20px; -fx-text-fill: black; -fx-font-weight: 600; -fx-background-color: white; -fx-background-radius: 10; -fx-padding: 20 10 10 10; -fx-spacing: 20; -fx-border-color: red; -fx-border-width: 2;-fx-border-radius: 5; -fx-border-style: solid;");
                noDataMessage.setAlignment(Pos.CENTER);
                StackPane noDataPane = new StackPane(noDataMessage);
                noDataPane.setPrefSize(600, 400);
                salesRevenueRow.getChildren().set(0, noDataPane);

            } else {
                ChartTableToggleComponent newSalesComponent2 = new ChartTableToggleComponent(chart, table);
                newSalesComponent2.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(newSalesComponent2, Priority.ALWAYS);
                newSalesComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));
                HBox.setHgrow(newSalesComponent2, Priority.ALWAYS);

                salesRevenueRow.getChildren().setAll(newSalesComponent2);
            }
        });

        salesRevenueRow.getChildren().setAll(revenueToggleComponent2);
        GridPane allChartsContainer = new GridPane();
        allChartsContainer.setPadding(new Insets(20, 0, 0, 0));
        allChartsContainer.setHgap(20);
        allChartsContainer.setVgap(20);
        allChartsContainer.setAlignment(Pos.CENTER);
        allChartsContainer.setMaxWidth(Double.MAX_VALUE);

        allChartsContainer.add(stockToggleComponent2, 0, 0);
        allChartsContainer.add(reorderToggleComponent2, 1, 0);
        allChartsContainer.add(revenueToggleComponent2, 0, 1);
        allChartsContainer.add(salesRevenueRow, 1, 1);

        GridPane.setHgrow(stockToggleComponent2, Priority.ALWAYS);
        GridPane.setHgrow(reorderToggleComponent2, Priority.ALWAYS);
        GridPane.setHgrow(revenueToggleComponent2, Priority.ALWAYS);
        GridPane.setHgrow(salesRevenueRow, Priority.ALWAYS);

        GridPane.setVgrow(stockToggleComponent2, Priority.ALWAYS);
        GridPane.setVgrow(reorderToggleComponent2, Priority.ALWAYS);
        GridPane.setVgrow(revenueToggleComponent2, Priority.ALWAYS);
        GridPane.setVgrow(salesRevenueRow, Priority.ALWAYS);

        refresh.setOnAction(event -> {
            allChartsContainer.getChildren().clear();
            allChartsContainer.getChildren().addAll(getStockLevelChart(), getReorderAlertChart(connection), getRevenueChart(connection));
        });

        visualArea.setAlignment(Pos.CENTER);
        visualArea.setPadding(new Insets(30, 0, 20, 0));
        visualArea.setSpacing(20);
        visualArea.getChildren().addAll(visualAreaTxt, allChartsContainer);

        VBox mainFooterSec = new VBox();
        HBox sellInfoFooterSec = new HBox();
        Text soldText = new Text("Total items sold: " + totalSoldQty + " items");
        soldText.setStyle("-fx-font-size: 19px; -fx-font-weight: 600;");
        soldText.getStyleClass().add("paragraph-texts");
        soldText.setTextAlignment(TextAlignment.CENTER);
        Text horizontalLine = new Text(" | ");
        horizontalLine.setStyle("-fx-font-weight: 900; -fx-font-size: 20px;");
        horizontalLine.getStyleClass().add("paragraph-texts");

        Text lowStocksMsg = new Text( lowStokeProducts+ " Low stock items");
        lowStocksMsg.setStyle("-fx-font-size: 19px; -fx-font-weight: 600;");
        lowStocksMsg.getStyleClass().add("paragraph-texts");

        sellInfoFooterSec.setSpacing(10);
        sellInfoFooterSec.setAlignment(Pos.CENTER);
        sellInfoFooterSec.getChildren().addAll(soldText, horizontalLine, lowStocksMsg);

        Button inventoryLink = new Button("View Inventory");
        inventoryLink.setOnAction(e -> navigationHandler.goToInventory());
        inventoryLink.getStyleClass().add("default-buttons");

        HBox footerSEc = new HBox();
        ComboBox<String> footerExports = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Export as PDF",
                                "Export as Excel"
                        )
                )
        );
        footerExports.setPromptText("Export");
        footerExports.getStyleClass().add("default-dropdowns");

        footerSEc.getChildren().addAll(footerExports, inventoryLink);
        footerSEc.setSpacing(30);
        footerSEc.setPadding(new Insets(10, 0, 0 ,0));

        Text helpText = new Text("Need help?  Contact rukshanse.info@gmail.com / sandunsathyajith1@gmail.com");
        helpText.setStyle("-fx-font-size: 17px; -fx-font-weight: 500;");
        helpText.getStyleClass().add("paragraph-texts");

        mainFooterSec.setMaxWidth(Double.MAX_VALUE);
        mainFooterSec.getStyleClass().add("footer-section");
        mainFooterSec.setPadding(new Insets(30, 0, 30, 0));
        mainFooterSec.setSpacing(20);
        mainFooterSec.setAlignment(Pos.CENTER);
        footerSEc.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(mainFooterSec, 0.0);
        mainFooterSec.getChildren().addAll(sellInfoFooterSec, footerSEc, helpText);

        ScrollPane mainScrlSec = new ScrollPane(mainLayout);
        mainScrlSec.setFitToWidth(true);
        headerContainer.getChildren().addAll(dateRange, export, refresh);
        navbar.getChildren().addAll(heading, subHeading, headerContainer);

        mainLayout.getChildren().addAll(navbar, summaryCardContainer, stockAnalytics, saleAnalyticsSec, alertsSec, userAnalyticsSec, visualArea, mainFooterSec);
        ScrollPane mainContainer = new ScrollPane();
        mainContainer.setContent(mainLayout);
        mainContainer.setFitToWidth(true);
        mainContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    private void updateSalesSection() {
        String selected = dateRange.getValue();
        LineChart<String, Number> chart = ChartKeeper.getSalesChart(connection, selected);
        TableView<TableKeeper.SalesRow> table = TableKeeper.getSalesTable(selected);

        salesOverTimeSec.getChildren().clear();

        if (chart.getData().isEmpty() && table.getItems().isEmpty()) {
            Label noDataMessage = new Label("No data found related to the selected date!");
            noDataMessage.setStyle("-fx-font-size: 20px; -fx-text-fill: darkGray;");
            StackPane noDatapane = new StackPane(noDataMessage);
            noDatapane.setPrefSize(600, 600);
            salesOverTimeSec.getChildren().add(noDatapane);
        } else {
            ChartTableToggleComponent salesToggleComponent = new ChartTableToggleComponent(chart, table);
            salesToggleComponent.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(salesToggleComponent, Priority.ALWAYS);
            salesToggleComponent.prefHeightProperty().bind(salesOverTimeSec.heightProperty().multiply(0.9));
            salesOverTimeSec.getChildren().add(salesToggleComponent);
        }
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

    // Search button searching action
    private void searchUser() {
        String searchedUser = searchBar.getText().trim();

        if (!searchedUser.isEmpty()) {
            ObservableList<User> filteredUsers = FXCollections.observableArrayList();

            for (User user : fullUserList) {
                if (user.getFirstName().toLowerCase().contains(searchedUser)) {
                    filteredUsers.add(user);
                }
            }
            userTable.setItems(filteredUsers);
        }
    }
    public VBox getLayout() {
        return mainLayout;
    }
    private void showAllUsers() {
        searchBar.clear(); // Clear search box
        userTable.setItems(fullUserList); // Reset table to show all users
    }
}
