package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.InventoryManagementApplicationController;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.view.components.*;
import javafx.animation.ScaleTransition;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.util.*;

import static javafx.application.Application.launch;

public class Analytics extends VBox implements ThemeObserver {
    // Containers
    private static VBox mainLayout;
    HBox salesSection;
    VBox navbar;
    HBox stockContainer;
    HBox revenueUnitSec;
    GridPane allChartsContainer;
    // Card Labels
    Label totalProducts;
    Label inventoryValue;
    Label lowStoke;
    private Text fastMoving;
    private Text slowMovingItems;
    Text soldUnits;
    Text customersAmount;
    Text revenue;
    // Components in used
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
    Button refresh;
    private ListView<String> lowStockListView;
    private final InventoryManagementApplicationController.NavigationHandler navigationHandler;
    private LineChart<String, String> currentSalesChart;
    private TableView<TableKeeper.SalesRow> currentSalesTable;
    private ChartTableToggleComponent stockToggleComponent;
    private ChartTableToggleComponent reorderToggleComponent;
    ChartTableToggleComponent revenueToggleComponent;
    // Charts and tables in the main view
    ChartTableToggleComponent stockToggleComponent2;
    ChartTableToggleComponent reorderToggleComponent2;
    ChartTableToggleComponent salesToggleComponent2;
    ChartTableToggleComponent revenueToggleComponent2;
    ComboBox<String> dateRange;

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

        refresh = new Button("Refresh ↻");
        refresh.getStyleClass().add("default-buttons");
        HoverTooltip refreshTooltip = new HoverTooltip("Refresh data and charts");
        refreshTooltip.attachTo(refresh);

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
        totalProducts = new Label(totalProductSum + " Products are Available");
        totalProducts.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region percentage = new Region();
        Card productCard = new Card(totalProductsTxt, totalProducts, percentage);
        productCard.getStyleClass().add("summary-cards");

        int totalProductValue = dbConnection.getTotalProductValue();
        int remainingItemAmount = dbConnection.getRemainingProductsSum();
        int totalInventoryValue = totalProductValue * remainingItemAmount;
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedTotalInventoryValue = formatter.format(totalInventoryValue);
        // Total Inventory Value card
        Text inventoryValueTxt = new Text("💰 Total Inventory Value");
        inventoryValueTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; ");
        inventoryValueTxt.getStyleClass().add("sub-cards-heading");
        inventoryValue = new Label("$" + formattedTotalInventoryValue + " of Value");
        inventoryValue.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region expectedValue = new Region();
        Card inventoryValueCard = new Card(inventoryValueTxt, inventoryValue, expectedValue);
        inventoryValueCard.getStyleClass().add("summary-cards");

        int lowStokeProducts = Connection.getLowStockItemCount(dbConnection);
        // Total sales Card
        Text lowStokeTxt = new Text("🔸 Low Stokes");
        lowStokeTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        lowStokeTxt.getStyleClass().add("sub-cards-heading");
        lowStoke = new Label(lowStokeProducts + " Items");
        lowStoke.setStyle("-fx-font-size: 15px; -fx-text-fill: gray; -fx-font-weight: bold;");
        Region currentAmount = new Region();
        Card lowStockCard = new Card(lowStokeTxt, lowStoke, currentAmount);
        lowStockCard.getStyleClass().add("summary-cards");
        // Tooltip
        List<String> mainLowStockItems = Connection.getLowStockitemNames(dbConnection);
        HoverTooltip mainLowStokesTooltip = new HoverTooltip("Low Stocks: \n" + String.join("\n⚫ ", mainLowStockItems));
        mainLowStokesTooltip.attachTo(lowStockCard);

        Text fastTxt = new Text("✈ Fast Moving Items");
        fastTxt.getStyleClass().add("sub-cards-heading");
        fastTxt.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        fastMoving = new Text();
        fastMoving.setFill(Color.WHITE);
        refreshFastMovingItems(connection);
        Region emptySpace = new Region();
        Card fastCard = new Card(fastTxt, fastMoving, emptySpace);
        fastCard.getStyleClass().add("summary-cards");

        Text slowText = new Text("🐌 Slow Moving Items");
        slowText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        slowText.getStyleClass().add("sub-cards-heading");
        slowMovingItems = new Text();
        slowMovingItems.setFill(Color.WHITE);
        refreshSlowMovingItems(connection);
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

        stockContainer = new HBox(10);
        double halfStockContainer = stockContainer.getPrefWidth() / 2;

        stockToggleComponent = createStockComponent();
        stockToggleComponent.setMaxWidth(Double.MAX_VALUE);
        stockToggleComponent.setPrefWidth(halfStockContainer);
        stockToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);

        reorderToggleComponent = createReorderComponent();
        reorderToggleComponent.setMaxWidth(Double.MAX_VALUE);
        reorderToggleComponent.setPrefWidth(halfStockContainer);
        reorderToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);

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
                            .append(" :- Sold: ").append(ps.getTotalSold()).append(", ");
                }
                if (textBuilder.length() > 2) {
                    textBuilder.setLength(textBuilder.length() - 2);
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
        soldUnits = new Text("Total Ordered Quantity " + totalSoldQty);
        soldUnits.getStyleClass().add("sub-cards-value");
        soldUnitCard.getStyleClass().add("summary-cards");
        soldUnitCard.setAlignment(Pos.CENTER);
        soldUnitCard.getChildren().addAll(unitSoldTxt, soldUnits);

        int totalCustomers = dbConnection.getTotalCustomers();
        // Total customers card
        VBox totalCustomersCard = new VBox();
        Text totalCustomersTxt= new Text("\uD83D\uDC65 Total Customers");
        totalCustomersTxt.getStyleClass().add("sub-cards-heading");
        customersAmount = new Text(totalCustomers + " Customers");
        customersAmount.getStyleClass().add("sub-cards-value");
        totalCustomersCard.getStyleClass().add("summary-cards");
        totalCustomersCard.setAlignment(Pos.CENTER);
        totalCustomersCard.getChildren().addAll(totalCustomersTxt, customersAmount);

        // Total customers card
        int orderedItemsValue = dbConnection.getOrderedQuantityValue();
        int revenueValue = totalSoldQty * orderedItemsValue;
        DecimalFormat revenueFormatter = new DecimalFormat("#,###");
        String formattedRevenue = revenueFormatter.format(revenueValue);
        VBox orderFullFilledCard = new VBox();
        Text revenueTxt= new Text("\uD83D\uDCE6 Revenue");
        revenueTxt.getStyleClass().add("sub-cards-heading");
        revenue = new Text("$ " + formattedRevenue + " of revenue");
        revenue.getStyleClass().add("sub-cards-value");
        orderFullFilledCard.getStyleClass().add("summary-cards");
        orderFullFilledCard.setAlignment(Pos.CENTER);
        orderFullFilledCard.getChildren().addAll(revenueTxt, revenue);

        applyHoverZoomEffect(productCard, inventoryValueCard, lowStockCard, fastCard, slowCard, topSoldItemCard, soldUnitCard, totalCustomersCard, orderFullFilledCard);

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
        salesSection = new HBox();
        revenueUnitSec = new HBox();

        dateRange.setOnAction(e -> {
            if (currentSalesChart != null && currentSalesTable != null) {
                updateSalesSectionUI(currentSalesChart, currentSalesTable);
            } else {
                loadDataAndRefreshUI(dateRange.getValue());
            }
            loadDataAndRefreshUI(dateRange.getValue());
        });
        loadDataAndRefreshUI(dateRange.getValue());

        saleAnalyticsSec.getChildren().addAll(saleHeading, salesCardContainer, salesOverTimeSec);

        // Shows the revenue and the unit sold table & chart | Alert section
        HBox revenueAlertSection = new HBox();
        revenueAlertSection.setPadding(new Insets(20, 0,0, 0));
        revenueAlertSection.setAlignment(Pos.CENTER);
        revenueAlertSection.setMaxWidth(Double.MAX_VALUE);

        revenueToggleComponent = createRevenueToggleComponent();
        revenueToggleComponent.setMaxWidth(800);
        reorderToggleComponent.setPrefWidth(500);
        revenueAlertSection.setMinWidth(800);

        revenueUnitSec.getChildren().addAll(revenueToggleComponent);
        salesOverTimeSec.getChildren().addAll(salesSection);

        // Alerts section
        VBox alertsSec = new VBox();
        Text alertsTxt = new Text("\uD83D\uDD14 Alerts & ⚠ Exceptions");
        alertsTxt.getStyleClass().add("heading-texts");
        alertsTxt.setTextAlignment(TextAlignment.CENTER);
        alertsSec.setAlignment(Pos.TOP_CENTER);
        alertsSec.setPadding(new Insets(30, 0, 10 ,0));
        alertsSec.setMaxWidth(Double.MAX_VALUE);
        alertsSec.setSpacing(30);

        FlowPane alertsContainer = new FlowPane();
        alertsContainer.setHgap(20.0);
        alertsContainer.setVgap(20.0);
        alertsContainer.setPadding(new Insets(20, 0, 20, 0));
        alertsContainer.setMaxWidth(Double.MAX_VALUE);
        alertsContainer.setAlignment(Pos.CENTER_LEFT);
        alertsContainer.setMaxWidth(1300);
        alertsContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15); -fx-background-radius: 20;");

        // Alerts Low Stoke Card
        VBox lowStockAlertCard = new VBox();
        Text lowStockTxt = new Text("LOW-STOKES ALERT");
        lowStockTxt.getStyleClass().add("alert-heading");
        Text lowStocks = new Text("You currently have " + lowStokeProducts + " low stokes");
        lowStocks.getStyleClass().add("alert-content");
        lowStockAlertCard.getChildren().addAll(lowStockTxt, lowStocks);
        lowStockAlertCard.setSpacing(10);
        lowStockAlertCard.getStyleClass().add("alert-cards");
        // The tooltip
        List<String> lowStockItems = Connection.getLowStockitemNames(dbConnection);
        HoverTooltip lowStokesTooltip = new HoverTooltip("Low Stocks: \n" + String.join("\n⚫ ", lowStockItems));
        lowStokesTooltip.attachTo(lowStockAlertCard);

        // Alerts Low Stoke Card
        int outofStokesItems = Connection.getOutofStokeItems(dbConnection);
        VBox outofStokeCard = new VBox();
        Text outOfStockTxt = new Text("\uD83D\uDD34 OUT-OF-STOCK ALERT");
        outOfStockTxt.getStyleClass().add("alert-heading");
        Text outOfStocks = new Text("You have " + outofStokesItems + " out of stokes");
        outOfStocks.getStyleClass().add("alert-content");
        outofStokeCard.getChildren().addAll(outOfStockTxt, outOfStocks);
        outofStokeCard.setSpacing(10);
        outofStokeCard.getStyleClass().add("alert-cards");
        // The tooltip
        List<String> outOfStockItems = Connection.getOutOfStockItemNames(dbConnection);
        HoverTooltip outOfStokesTooltip = new HoverTooltip("Out-of-Stocks: \n" + String.join("\n⚫ ", outOfStockItems));
        outOfStokesTooltip.attachTo(outofStokeCard);

        // Alerts Over Stoke Card
        int overStokesItems = Connection.getOverStockItems(dbConnection);
        VBox overStockAlertCard = new VBox();
        Text overStockTxt = new Text("OVER-STOCKS ALERT");
        overStockTxt.getStyleClass().add("alert-heading");
        Text overStocks = new Text("You have " + overStokesItems + " out of stokes");
        overStocks.getStyleClass().add("alert-content");
        overStockAlertCard.setSpacing(10);
        overStocks.getStyleClass().add("alert-content");
        overStocks.setFill(Color.web("#333333"));
        overStockAlertCard.getChildren().addAll(overStockTxt, overStocks);
        overStockAlertCard.getStyleClass().add("alert-cards");
        // The tooltip
        List<String> overStockItems = Connection.getOverStockitemNames(dbConnection);
        HoverTooltip overStokesTooltip = new HoverTooltip("Over Stocks: \n" + String.join("\n⚫ ", overStockItems));
        overStokesTooltip.attachTo(overStockAlertCard);

        alertsContainer.getChildren().addAll(lowStockAlertCard, overStockAlertCard, outofStokeCard);
        alertsSec.setPrefWidth(750);
        alertsSec.setAlignment(Pos.TOP_CENTER);
        alertsSec.getChildren().addAll(alertsTxt, alertsContainer);

        revenueAlertSection.setMaxWidth(Double.MAX_VALUE);
        revenueAlertSection.setAlignment(Pos.CENTER);
        revenueAlertSection.setSpacing(35);
        revenueAlertSection.getChildren().addAll(revenueToggleComponent, alertsSec);

        // updating all the cards data
        List<Runnable> refreshTasks = new ArrayList<>();
        refreshTasks.add(() -> {
            // Updating low stokes
            int updatedLowStock = Connection.getLowStockItemCount(dbConnection);
            lowStoke.setText(updatedLowStock + " Items");
            lowStocks.setText("You currently have " + updatedLowStock + " low stokes");
            // Updating out of stocks
            int updatedOutOfStocks = Connection.getOutofStokeItems(dbConnection);
            outOfStocks.setText("You have " + updatedOutOfStocks + " out of stocks");
            // Updating over stocks
            int updatedOverStocks = Connection.getOverStockItems(dbConnection);
            overStocks.setText("You got " + updatedOverStocks + " over stocks");

            // Updating low stock item names
            List<String> updatedItems = Connection.getLowStockitemNames(dbConnection);
            mainLowStokesTooltip.setText("Low Stocks: \n" + String.join("\n⚫ ", updatedItems));
            lowStokesTooltip.setText("Low Stokes: \n" + String.join("\n⚫ ", updatedItems));
            // Updating out of stocks names
            List<String> updatedOutOfStokes = Connection.getOutOfStockItemNames(dbConnection);
            outOfStokesTooltip.setText("Out of Stocks: \n" + String.join("\n⚫ ", updatedOutOfStokes));
            // Updating over stock item names
            List<String> updatedOverStockNames = Connection.getOverStockitemNames(dbConnection);
            overStokesTooltip.setText("Over Stocks: \n" + String.join("\n⚫ ", updatedOverStockNames));
        });

        VBox visualArea = new VBox();
        visualArea.setSpacing(30.0);
        Text visualAreaTxt = new Text("\uD83D\uDCC8 Charts and Visuals Area");
        visualAreaTxt.getStyleClass().add("heading-texts");
        visualAreaTxt.setFill(Color.web("#333333"));
        visualAreaTxt.setTextAlignment(TextAlignment.CENTER);

        HBox stockReorderRow = new HBox(20);

        // Stock level & chart
        stockToggleComponent2 = createStockComponent();

        // Reorder chart & table
        reorderToggleComponent2 = createReorderComponent();

        // Sales chart and Table
        salesToggleComponent2 = new ChartTableToggleComponent(
                ChartKeeper.getSalesChart(connection, ""),
                TableKeeper.getSalesTable("")
        );
        salesToggleComponent2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(salesToggleComponent2, Priority.ALWAYS);

        // Shows the revenue and the unit sold table & chart
        revenueToggleComponent2 = createRevenueToggleComponent();

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
        dateRange.setValue("This Year");
        dateRange.setOnAction(e -> {
            String selected = dateRange.getValue();
            LineChart<String, String> chart = ChartKeeper.getSalesChart(connection, selected);
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

                salesRevenueRow.getChildren().addAll(newSalesComponent2, revenueToggleComponent2);
            }
        });
        dateRange.getOnAction().handle(new ActionEvent());

        allChartsContainer = new GridPane();
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
            refreshTasks.forEach(Runnable::run);
            loadDataAndRefreshUI(dateRange.getValue());
            refreshAllComponents();

            // Refreshing the cards
            int updatedTotalItemsSum = dbConnection.getTotalProducts();
            totalProducts.setText(updatedTotalItemsSum + " Products are Available");

            int updatedTotalItemsValue = dbConnection.getTotalProductValue();
            int updatedRemainingAmount = dbConnection.getRemainingProductsSum();
            int updatedInventoryValue = updatedTotalItemsValue * updatedRemainingAmount;
            DecimalFormat inventoryFormatter = new DecimalFormat("#,###");
            String updateValue = inventoryFormatter.format(updatedInventoryValue);
            inventoryValue.setText("$ " + updateValue + " of Value");

            int updatedTotalSoldQty = dbConnection.getTotalSoldQuantity();
            soldUnits.setText("Total Ordered Quantity " + updatedTotalSoldQty);

            int updatedTotalCustomers = dbConnection.getTotalCustomers();
            customersAmount.setText(updatedTotalCustomers + " Customers");

            int updatedOrderedItemsValue = dbConnection.getOrderedQuantityValue();
            int updatedRevenueValue = (updatedTotalSoldQty * updatedOrderedItemsValue);
            DecimalFormat updatedRevenueFormatter = new DecimalFormat("#,###");
            String updatedCompletedRevenue = updatedRevenueFormatter.format(updatedRevenueValue);
            revenue.setText("$ " + updatedCompletedRevenue + " of revenue");

            // Updating the items with the sold amount
            Map<String, List<SoldProducts>> updatedTopSalesData = connection.getTopAndBottomSellingProducts();

            if (topSalesData != null && topSalesData.containsKey("top")) {
                List<SoldProducts> topList = topSalesData.get("top");
                if (topList != null && !topList.isEmpty()) {
                    StringBuilder textBuilder = new StringBuilder();
                    for (SoldProducts ps : topList) {
                        String itemName = connection.getItemNameById(ps.getItemId());
                        textBuilder.append(itemName)
                                .append(" :- Sold: ").append(ps.getTotalSold()).append(", ");
                    }
                    if (textBuilder.length() > 2) {
                        textBuilder.setLength(textBuilder.length() -2);
                    }
                    topSoldProduct.setText(textBuilder.toString());
                } else {
                    topSoldProduct.setText("0");
                }
            } else {
                topSoldProduct.setText("0");
            }
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

        mainLayout.getChildren().addAll(navbar, summaryCardContainer, stockAnalytics, saleAnalyticsSec, revenueAlertSection, visualArea, mainFooterSec);
        ScrollPane mainContainer = new ScrollPane();
        mainContainer.setContent(mainLayout);
        mainContainer.setFitToWidth(true);
        mainContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    public static VBox getMainLayout() {
        return mainLayout;
    }

    private ChartTableToggleComponent createStockComponent() {
        ChartTableToggleComponent chartTableToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getStockLevelChart(),
                TableKeeper.getStockLevelTable()
        );
        chartTableToggleComponent.setMaxWidth(Double.MAX_VALUE);
        chartTableToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(chartTableToggleComponent, Priority.ALWAYS);
        return chartTableToggleComponent;
    }

    private void refreshStockChart(HBox stockContainer) {
        stockContainer.getChildren().clear();
        ChartTableToggleComponent refreshedComponent = createStockComponent(); // fetches fresh chart data
        stockContainer.getChildren().add(refreshedComponent);
    }

    private ChartTableToggleComponent createReorderComponent() {
        ChartTableToggleComponent chartTableToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getReorderAlertChart(connection),
                TableKeeper.getReorderAlertTable()
        );
        chartTableToggleComponent.setMaxWidth(Double.MAX_VALUE);
        chartTableToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(chartTableToggleComponent, Priority.ALWAYS);
        return chartTableToggleComponent;
    }

    private ChartTableToggleComponent createRevenueToggleComponent() {
        ChartTableToggleComponent chartTableToggleComponent = new ChartTableToggleComponent(
                ChartKeeper.getRevenueChart(connection),
                TableKeeper.getRevenueTable()
        );
        chartTableToggleComponent.setMaxWidth(Double.MAX_VALUE);
        chartTableToggleComponent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(chartTableToggleComponent, Priority.ALWAYS);
        return chartTableToggleComponent;
    }

    public void refreshAllComponents() {
        stockContainer.getChildren().removeAll(stockToggleComponent, reorderToggleComponent);
        revenueUnitSec.getChildren().removeAll(revenueToggleComponent);
        allChartsContainer.getChildren().removeAll(stockToggleComponent2, reorderToggleComponent2, salesToggleComponent2, revenueToggleComponent2);

        stockToggleComponent = createStockComponent();

        stockToggleComponent.setMaxWidth(Double.MAX_VALUE);
        reorderToggleComponent = createReorderComponent();
        reorderToggleComponent.setMaxWidth(Double.MAX_VALUE);
        revenueToggleComponent = createRevenueToggleComponent();
        revenueToggleComponent.setMaxWidth(1400);

        stockToggleComponent2 = createStockComponent();
        reorderToggleComponent2 = createReorderComponent();
        revenueToggleComponent2 = createRevenueToggleComponent();
        GridPane.setHgrow(stockToggleComponent2, Priority.ALWAYS);
        GridPane.setHgrow(reorderToggleComponent2, Priority.ALWAYS);
        GridPane.setHgrow(revenueToggleComponent2, Priority.ALWAYS);

        GridPane.setVgrow(stockToggleComponent2, Priority.ALWAYS);
        GridPane.setVgrow(reorderToggleComponent2, Priority.ALWAYS);
        GridPane.setVgrow(revenueToggleComponent2, Priority.ALWAYS);
        allChartsContainer.add(stockToggleComponent2, 0, 0);
        allChartsContainer.add(reorderToggleComponent2, 1, 0);
        allChartsContainer.add(revenueToggleComponent2, 0, 1);


        stockContainer.getChildren().add(0, reorderToggleComponent);
        stockContainer.getChildren().add(0, stockToggleComponent);
        revenueUnitSec.getChildren().add(0, revenueToggleComponent);
    }

    private void loadDataAndRefreshUI(String selectedDateRange) {
        currentSalesChart = ChartKeeper.getSalesChart(connection, selectedDateRange);
        currentSalesTable = TableKeeper.getSalesTable(selectedDateRange);

        updateSalesSectionUI(currentSalesChart, currentSalesTable);
    }

    private void updateSalesSectionUI(LineChart<String, String> chart, TableView<TableKeeper.SalesRow> table) {
        salesSection.getChildren().clear();

        boolean chartEmpty = chart.getData().isEmpty();
        boolean tableEmpty = table.getItems().isEmpty();

        if (chartEmpty && tableEmpty) {
            Label noDataMessage = new Label("No data found related to the selected date!");
            StackPane noDataPane = new StackPane(noDataMessage);
            noDataPane.setPrefSize(600, 600);
            salesSection.getChildren().add(noDataPane);

        } else {
            ChartTableToggleComponent salesToggleComponent = new ChartTableToggleComponent(chart, table);
            salesToggleComponent.setMaxWidth(Double.MAX_VALUE);
            salesToggleComponent.setMinHeight(700);
            salesToggleComponent.setMaxWidth(1300);
            salesToggleComponent.setAlignment(Pos.CENTER);
            HBox.setHgrow(salesToggleComponent, Priority.ALWAYS);
            salesToggleComponent.prefHeightProperty().bind(salesSection.heightProperty().multiply(0.9));
            salesSection.getChildren().add(salesToggleComponent);
            salesSection.setAlignment(Pos.CENTER);
        }
    }

    // For showing the fast moving items
    public void refreshFastMovingItems(Connection connection) {
        Map<String, List<SoldProducts>> salesData = connection.getTopAndBottomSellingProducts();

        if (salesData != null && salesData.containsKey("top")) {
            List<SoldProducts> topList = salesData.get("top");
            if (topList != null && !topList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder();
                for (SoldProducts ps : topList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName).append(", ");
                }
                if (textBuilder.length() > 2) {
                    textBuilder.setLength(textBuilder.length() - 2);
                }
                fastMoving.setText(textBuilder.toString());
                fastMoving.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            } else {
                fastMoving.setText("0");
                fastMoving.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            }
        } else {
            fastMoving.setText("0");
            fastMoving.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
        }
    }
    // For showing the slow moving items
    public void refreshSlowMovingItems(Connection connection) {
        Map<String, List<SoldProducts>> salesData = connection.getTopAndBottomSellingProducts();

        if (salesData != null && salesData.containsKey("bottom")) {
            List<SoldProducts> bottomList = salesData.get("bottom");
            if (bottomList != null && !bottomList.isEmpty()) {
                StringBuilder textBuilder = new StringBuilder();
                for (SoldProducts ps : bottomList) {
                    String itemName = connection.getItemNameById(ps.getItemId());
                    textBuilder.append(itemName).append(", ");
                }
                if (textBuilder.length() > 2) {
                    textBuilder.setLength(textBuilder.length() - 2);
                }
                slowMovingItems.setText(textBuilder.toString());
                slowMovingItems.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            } else {
                slowMovingItems.setText("0");
                slowMovingItems.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
            }
        } else {
            slowMovingItems.setText("0");
            slowMovingItems.setStyle("-fx-font-size: 15px; -fx-fill: gray; -fx-font-weight: bold;");
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

    // Adding smooth hover effect
    public void applyHoverZoomEffect(Region... cards) {
        for (Region card : cards) {
            card.setOnMouseEntered(event -> {
                ScaleTransition transform = new ScaleTransition(Duration.millis(150), card);
                transform.setToX(1.02);
                transform.setToY(1.02);
                transform.play();
            });
            card.setOnMouseExited(event -> {
                ScaleTransition transform = new ScaleTransition(Duration.millis(150), card);
                transform.setToX(1);
                transform.setToY(1);
                transform.play();
            });
        }
    }

    public VBox getLayout() {
        return mainLayout;
    }
    private void showAllUsers() {
        searchBar.clear(); // Clear search box
        userTable.setItems(fullUserList); // Reset table to show all users
    }

    public static void main(String[] args) {
        launch(args);
    }
}
