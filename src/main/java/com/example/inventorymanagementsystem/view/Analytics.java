package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.ChartKeeper;
import com.example.inventorymanagementsystem.view.components.ChartTableToggleComponent;
import com.example.inventorymanagementsystem.view.components.TableKeeper;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Flow;

import static com.example.inventorymanagementsystem.view.components.ChartKeeper.*;

public class Analytics {
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

    public Analytics() {

        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        Connection dbConnection = Connection.getInstance();
        connection = Connection.getInstance();

        VBox navbar = new VBox();
        navbar.setMaxWidth(Double.MAX_VALUE);
        navbar.setPadding(new Insets(20, 0, 20, 0));
        navbar.setSpacing(5.5);
        navbar.setStyle("-fx-background-color: lightGray;");
        navbar.setAlignment(Pos.CENTER);

        Text heading = new Text("Inventory Analytics");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        heading.setFill(Color.web("#444444"));
        heading.setTextAlignment(TextAlignment.CENTER);

        Text subHeading = new Text("Real-time insights into your inventory, sales, and operations performance.");
        subHeading.setStyle("-fx-font-size: 15px;");
        subHeading.setFill(Color.web("#777777"));

        HBox headerContainer = new HBox();
        headerContainer.setPadding(new Insets(20, 0, 0, 0));
        headerContainer.setSpacing(30);
        headerContainer.setAlignment(Pos.CENTER);

        ComboBox<String> category = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                            "T-Shirts", "Shirts", "Jeans", "Trousers", "Jackets", "Coats",
                            "Dresses", "Skirts", "Shorts", "Sweaters", "Hoodies", "Activewear",
                            "Underwear", "Sleepwear", "Swimwear", "Accessories", "Shoes")
                )
        );
        category.setPromptText("Category");

        ComboBox<String> dateRange = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Today", "Yesterday", "Last 7 Days", "Last 30 Days", "This Month",
                                "Last Month", "Custom Range"
                        )
                )
        );
        dateRange.setPromptText("Date Range");

        ComboBox<String> export = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Export as PDF",
                                "Export as Excel",
                                "Export as CSV"
                        )
                )
        );
        export.setPromptText("Export");

        Button refresh = new Button("Refresh ↻");

        FlowPane summaryCardContainer = new FlowPane();
        summaryCardContainer.setHgap(20);
        summaryCardContainer.setVgap(20);
        summaryCardContainer.setPadding(new Insets(30, 0, 0, 0));
        summaryCardContainer.setAlignment(Pos.CENTER);
        summaryCardContainer.setMaxWidth(Double.MAX_VALUE);

        int totalProductSum = dbConnection.getTotalProducts();
        // Total product Card
        Text totalProductsTxt = new Text("Total Products in inventory");
        totalProductsTxt.setTextAlignment(TextAlignment.CENTER);
        totalProductsTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label totalProducts = new Label(totalProductSum + " Products are Available");
        totalProducts.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text percentage = new Text("40% from Expected");
        Card productCard = new Card(totalProductsTxt, totalProducts, percentage);
        productCard.getStyleClass().add("summary-cards");

        int totalProductValue = dbConnection.getTotalProductValue();
        int remainingItemAmount = dbConnection.getRemainingProductsSum();
        // Total Inventory Value card
        Text inventoryValueTxt = new Text("Total Inventory Value");
        inventoryValueTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label inventoryValue = new Label("$ " + remainingItemAmount * totalProductValue + " of Value");
        inventoryValue.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text expectedValue = new Text("Expecting $100");
        Card inventoryValueCard = new Card(inventoryValueTxt, inventoryValue, expectedValue);
        inventoryValueCard.getStyleClass().add("summary-cards");

        int totalOrderedQty = dbConnection.getOrderedQuantityTotal();
        // Total sales Card
        Text totalSaleTxt = new Text("Total Sales");
        totalSaleTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label totalSales = new Label(totalOrderedQty + " Sales");
        totalSales.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text maxSales = new Text("Maximum $100");
        Card salesCard = new Card(totalSaleTxt, totalSales, maxSales);
        salesCard.getStyleClass().add("summary-cards");

        int lowStokeProducts = 0;
        // Total sales Card
        Text lowStokeTxt = new Text("Low Stokes");
        lowStokeTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label lowStoke = new Label(lowStokeProducts + " Items");
        lowStoke.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text currentAmount = new Text("Maximum $100");
        Card lowStockCard = new Card(lowStokeTxt, lowStoke, currentAmount);
        lowStockCard.getStyleClass().add("summary-cards");


        Text fastVsSlowTxt = new Text("Fast vs Slow Moving Items");
        fastVsSlowTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label fastMoving = new Label("1, 0000 items");
        fastMoving.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text slowMoving = new Text("Maximum $100");
        Card fastVsSlowCard = new Card(fastVsSlowTxt, fastMoving, slowMoving);
        fastVsSlowCard.getStyleClass().add("summary-cards");

        summaryCardContainer.getChildren().addAll(productCard, inventoryValueCard, salesCard, lowStockCard, fastVsSlowCard);

        VBox stockAnalytics = new VBox();
        stockAnalytics.setMaxWidth(Double.MAX_VALUE);

        stockAnalytics.setAlignment(Pos.TOP_CENTER);
        stockAnalytics.setPadding(new Insets(40, 0, 20, 30));

        // Current Stock Section
        Text currentStockTxt = new Text("Current Stock Levels");
        currentStockTxt.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        currentStockTxt.setFill(Color.web("#333333"));
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
        saleHeading.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        saleHeading.setFill(Color.web("#333333"));
        saleHeading.setTextAlignment(TextAlignment.CENTER);

        // Top Selling items card
        List<ItemHasSize> topItems = connection.getTopThreeOrderedItems();
        StringBuilder topText = new StringBuilder();

        for (ItemHasSize item : topItems) {
            String itemName = dbConnection.getItemNameById(item.getItemID());
            topText.append("")
                    .append(itemName)
                    .append(", ");
        }

        VBox topSoldItemCard = new VBox();
        Text topSellingTxt= new Text("\uD83E\uDD47 Top-Selling Product");
        topSellingTxt.getStyleClass().add("sub-cards-heading");
        Text topSoldProduct = new Text(topText.toString());
        topSoldProduct.getStyleClass().add("sub-cards-value");
        topSoldItemCard.getStyleClass().add("sub-cards");
        topSoldItemCard.setAlignment(Pos.CENTER);
        topSoldItemCard.getChildren().addAll(topSellingTxt, topSoldProduct);

        // Top Selling items card
        VBox soldUnitCard = new VBox();
        Text unitSoldTxt= new Text("\uD83D\uDD01 Units Sold");
        unitSoldTxt.getStyleClass().add("sub-cards-heading");
        Text soldUnits = new Text("Total Ordered Quantity " + totalOrderedQty);
        soldUnits.getStyleClass().add("sub-cards-value");
        soldUnitCard.getStyleClass().add("sub-cards");
        soldUnitCard.setAlignment(Pos.CENTER);
        soldUnitCard.getChildren().addAll(unitSoldTxt, soldUnits);

        int totalCustomers = dbConnection.getTotalCustomers();
        // Total customers card
        VBox totalCustomersCard = new VBox();
        Text totalCustomersTxt= new Text("\uD83D\uDC65 Total Customers");
        totalCustomersTxt.getStyleClass().add("sub-cards-heading");
        Text customersAmount = new Text(totalCustomers + " Customers");
        customersAmount.getStyleClass().add("sub-cards-value");
        totalCustomersCard.getStyleClass().add("sub-cards");
        totalCustomersCard.setAlignment(Pos.CENTER);
        totalCustomersCard.getChildren().addAll(totalCustomersTxt, customersAmount);

        // Total customers card
        VBox orderFullFilledCard = new VBox();
        Text revenueTxt= new Text("\uD83D\uDCE6 Revenue");
        revenueTxt.getStyleClass().add("sub-cards-heading");
        Text revenue = new Text("$ " +( totalOrderedQty * totalProductValue) + " of revenue");
        revenue.getStyleClass().add("sub-cards-value");
        orderFullFilledCard.getStyleClass().add("sub-cards");
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
        saleAnalyticsSec.getChildren().addAll(saleHeading, salesCardContainer);

        VBox salesOverTimeSec = new VBox();

        // Shows the sales
        ChartTableToggleComponent salesToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getSalesChart(connection),
                TableKeeper.getSalesTable()
        );

        salesToggleComponent.setMaxWidth(1200);
        salesOverTimeSec.getChildren().addAll(salesToggleComponent);
        salesOverTimeSec.setAlignment(Pos.CENTER);

        // Shows the revenue and the unit sold table & chart
        ChartTableToggleComponent revenueToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getRevenueChart(connection),
                TableKeeper.getRevenueTable()
        );
        revenueToggleComponent.setPrefWidth(1200);

        HBox revenueUnitSec = new HBox();
        revenueUnitSec.setAlignment(Pos.TOP_CENTER);
        revenueUnitSec.setMaxWidth(Double.MAX_VALUE);
        revenueUnitSec.setPadding(new Insets(30, 0, 20, 0));
        revenueUnitSec.setAlignment(Pos.CENTER);
        revenueUnitSec.getChildren().addAll(revenueToggleComponent);

        // Alerts section
        VBox alertsSec = new VBox();
        Text alertsTxt = new Text("\uD83D\uDEA8 Alerts & Exceptions");
        alertsTxt.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        alertsTxt.setFill(Color.web("#333333"));
        alertsSec.setAlignment(Pos.CENTER);

        FlowPane alertsContainer = new FlowPane();
        alertsContainer.setHgap(10.0);
        alertsContainer.setVgap(10.0);
        alertsContainer.setPadding(new Insets(20, 0, 20, 0));
        alertsContainer.setStyle("-fx-background-color: lightGray");
        alertsContainer.setMaxWidth(Double.MAX_VALUE);
        alertsContainer.setAlignment(Pos.CENTER);

        // Alerts Low Stoke Card
        VBox lowStockAlertCard = new VBox();
        Text lowStockTxt = new Text("Alert! Low Stokes");
        lowStockTxt.getStyleClass().add("alert-heading");
        Text lowStocks = new Text("You currently have " + lowStokeProducts + " low stokes");
        lowStocks.getStyleClass().add("alert-content");
        lowStocks.setFill(Color.web("#333333"));
        lowStockAlertCard.getChildren().addAll(lowStockTxt, lowStocks);
        lowStockAlertCard.getStyleClass().add("alert-cards");

        // Alerts Over Stoke Card
        VBox overStockAlertCard = new VBox();
        Text overStockTxt = new Text("Alert! OverStock Warning!");
        overStockTxt.getStyleClass().add("alert-heading");
        HBox overStockMessageContainer = new HBox();
        Text overStocks = new Text(topText.toString());
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
        userAnalyticsTxt.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        userAnalyticsTxt.setFill(Color.web("#333333"));
        userAnalyticsTxt.setTextAlignment(TextAlignment.CENTER);

        HBox searchBarContainer = new HBox();
        searchBar = new TextField();
        searchBar.setPromptText("Search name...");
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(e -> searchUser());
        Region spaceForShowAll = new Region();
        spaceForShowAll.setMinWidth(30);
        Button showAllBtn = new Button("Show All");
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
        visualAreaTxt.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        visualAreaTxt.setFill(Color.web("#333333"));
        visualAreaTxt.setTextAlignment(TextAlignment.CENTER);

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
        //
        ChartTableToggleComponent salesToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getSalesChart(connection),
                TableKeeper.getSalesTable()
        );
        // Shows the revenue and the unit sold table & chart
        ChartTableToggleComponent revenueToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getRevenueChart(connection),
                TableKeeper.getRevenueTable()
        );

        stockToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        stockToggleComponent2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        reorderToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        reorderToggleComponent2.setPrefHeight(Region.USE_COMPUTED_SIZE);
        salesToggleComponent2.setMaxWidth(1200);
        revenueToggleComponent2.setMaxWidth(Double.MAX_VALUE);

        // Stock & Reorder row
        HBox stockReorderRow = new HBox(20);
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
        HBox.setHgrow(salesToggleComponent2, Priority.ALWAYS);
        HBox.setHgrow(revenueToggleComponent2, Priority.ALWAYS);
        stockToggleComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));
        reorderToggleComponent2.prefWidthProperty().bind(salesRevenueRow.widthProperty().divide(2));
        salesRevenueRow.getChildren().addAll(salesToggleComponent2, revenueToggleComponent2);

        VBox allChartsContainer = new VBox(20);
        allChartsContainer.setPadding(new Insets(20, 0, 0, 0));
        allChartsContainer.setAlignment(Pos.CENTER);

        allChartsContainer.setMaxWidth(Double.MAX_VALUE);
        allChartsContainer.getChildren().addAll(stockReorderRow, salesRevenueRow);

        refresh.setOnAction(event -> {
            allChartsContainer.getChildren().clear();
            allChartsContainer.getChildren().addAll(getStockLevelChart(), getReorderAlertChart(connection), getSalesChart(dbConnection), getRevenueChart(connection));
        });

        visualArea.setAlignment(Pos.CENTER);
        visualArea.setPadding(new Insets(30, 0, 20, 0));
        visualArea.setSpacing(20);
        visualArea.getChildren().addAll(visualAreaTxt, allChartsContainer);

        VBox mainFooterSec = new VBox();
        HBox footerSEc = new HBox();
        Button exportPDFBtn = new Button("Export to PDF");
        exportPDFBtn.setAlignment(Pos.CENTER);

        footerSEc.getChildren().addAll(exportPDFBtn);
        mainFooterSec.setMaxWidth(Double.MAX_VALUE);
        mainFooterSec.setStyle("-fx-background-color: lightGray; -fx-padding: 10;");
        mainFooterSec.setAlignment(Pos.CENTER);
        footerSEc.setAlignment(Pos.CENTER);
        AnchorPane.setBottomAnchor(mainFooterSec, 0.0);
        mainFooterSec.getChildren().addAll(exportPDFBtn);

        ScrollPane mainScrlSec = new ScrollPane(mainLayout);
        mainScrlSec.setFitToWidth(true);
        headerContainer.getChildren().addAll(category, dateRange, export, refresh);
        navbar.getChildren().addAll(heading, subHeading, headerContainer);

        mainLayout.getChildren().addAll(navbar, summaryCardContainer, stockAnalytics, saleAnalyticsSec, salesOverTimeSec, revenueUnitSec, alertsSec, userAnalyticsSec, visualArea, mainFooterSec);
        ScrollPane mainContainer = new ScrollPane();
        mainContainer.setContent(mainLayout);
        mainContainer.setFitToWidth(true);
        mainContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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

    private void showAllUsers() {
        searchBar.clear(); // Clear search box
        userTable.setItems(fullUserList); // Reset table to show all users
    }
    public VBox getLayout() {
        return mainLayout;
    }
}
