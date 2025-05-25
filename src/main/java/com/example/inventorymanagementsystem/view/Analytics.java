package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.view.components.Card;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Analytics {
    private VBox mainLayout;

    public Analytics() {

        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));
        mainLayout.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

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

        // Total product Card
        Text totalProductsTxt = new Text("Total Products in inventory");
        totalProductsTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label totalProducts = new Label("1, 0000 items");
        totalProducts.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text percentage = new Text("40% from Expected");
        Card productCard = new Card(totalProductsTxt, totalProducts, percentage);
        productCard.getStyleClass().add("summary-cards");

        // Total Inventory Value card
        Text inventoryValueTxt = new Text("Total Inventory Value");
        inventoryValueTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label inventoryValue = new Label("$1, 000, 000");
        inventoryValue.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text expectedValue = new Text("Expecting $100");
        Card inventoryValueCard = new Card(inventoryValueTxt, inventoryValue, expectedValue);
        inventoryValueCard.getStyleClass().add("summary-cards");

        // Total sales Card
        Text totalSaleTxt = new Text("Total Sales");
        totalSaleTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label totalSales = new Label("1, 0000 items");
        totalSales.setStyle("-fx-font-size: 17px; -fx-text-fill: white; -fx-font-weight: bold;");
        Text maxSales = new Text("Maximum $100");
        Card salesCard = new Card(totalSaleTxt, totalSales, maxSales);
        salesCard.getStyleClass().add("summary-cards");

        // Total sales Card
        Text lowStokeTxt = new Text("Low Stokes");
        lowStokeTxt.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label lowStoke = new Label("1, 0000 items");
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

        GridPane stockContainer = new GridPane();
        stockContainer.setAlignment(Pos.TOP_CENTER);

        // Current Stock Section
        Text currentStockTxt = new Text("Current Stock Levels");
        currentStockTxt.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
        currentStockTxt.setFill(Color.web("#333333"));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Product");
        yAxis.setLabel("Stock Quantity");

        BarChart<String, Number> columnChart = new BarChart<>(xAxis, yAxis);
        columnChart.setTitle("Product Stock Level");
        columnChart.setBarGap(10);
        columnChart.setCategoryGap(20);

        Connection connection = Connection.getInstance();
        ArrayList<ItemHasSize> items = connection.getAllItemHasSizes();

        XYChart.Series<String, Number> itemHasSizeStock = new XYChart.Series<>();
        itemHasSizeStock.setName("Stocks");

        if (items.isEmpty()) {
            System.out.println("No stock data found in the database!");
        } else {
            // Add stock details to the chart
            for (ItemHasSize item : items) {
                System.out.println("Product: " + item.getItemID() + ", Remaining Qty: " + item.getRemainingQuantity());
                itemHasSizeStock.getData().add(new XYChart.Data<>(String.valueOf(item.getItemID()), item.getRemainingQuantity()));
            }
        }

        columnChart.getData().add(itemHasSizeStock);
        Text reorderTxt = new Text("Reorder Alerts");
        reorderTxt.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        stockContainer.setHgap(10);
        stockContainer.setVgap(10);
        ColumnConstraints currentStocks = new ColumnConstraints();
        currentStocks.setPercentWidth(50);
        currentStocks.setMaxWidth(Double.MAX_VALUE);
        VBox stockMainContainer = new VBox();
        stockMainContainer.getChildren().addAll(columnChart);

        ColumnConstraints reorderSec = new ColumnConstraints();
        reorderSec.setPercentWidth(50);

        stockContainer.getColumnConstraints().addAll(reorderSec);

        GridPane.setHgrow(currentStockTxt, Priority.ALWAYS);
        GridPane.setHgrow(reorderTxt, Priority.ALWAYS);

        stockContainer.add(stockMainContainer, 0, 0);
        stockContainer.add(reorderTxt, 1, 0);
        currentStockTxt.setTextAlignment(TextAlignment.CENTER);
        reorderTxt.setTextAlignment(TextAlignment.CENTER);

        stockAnalytics.getChildren().addAll(currentStockTxt, stockContainer);

        headerContainer.getChildren().addAll(category, dateRange, export, refresh);
        navbar.getChildren().addAll(heading, subHeading, headerContainer);
        mainLayout.getChildren().addAll(navbar, summaryCardContainer, stockAnalytics);
    }
    public VBox getLayout() {
        return mainLayout;
    }
}
