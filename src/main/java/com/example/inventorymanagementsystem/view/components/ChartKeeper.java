package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChartKeeper {
    private Connection connection;

    // Stocks level chart (left side)
    public static BarChart<String, Number> getStockLevelChart() {
        // Stocks level chart (left side)
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Products");
        yAxis.setLabel("Stock Quantity");

        BarChart<String, Number> columnChart = new BarChart<>(xAxis, yAxis);
        columnChart.setTitle("Product Stock Level");
        columnChart.setBarGap(10);
        columnChart.setCategoryGap(20);
        columnChart.lookupAll(".chart-bar").forEach(node -> node.setStyle("-fx-bar-fill: blue;"));
        xAxis.setTickLabelRotation(45);

        Connection connection = Connection.getInstance();
        ArrayList<ItemHasSize> items = connection.getAllItemHasSizes();

        XYChart.Series<String, Number> itemHasSizeStock = new XYChart.Series<>();
        itemHasSizeStock.setName("Stocks");

        if (items.isEmpty()) {
            System.out.println("No stock data found in the database!");
        } else {
            // Add stock details to the chart
            for (ItemHasSize item : items) {
                int remainingQty = item.getRemainingQuantity();

                if (item.getRemainingQuantity() > 40) {
                    int itemId = item.getItemID();
                    String itemName = connection.getItemNameById(itemId);

                    itemHasSizeStock.getData().add(new XYChart.Data<>(String.valueOf(itemName), item.getRemainingQuantity()));

                } else {
                    System.out.println("Skipping Item ID " + item.getItemID() + " (Qty = 0)");
                }
            }
        }

        columnChart.getData().add(itemHasSizeStock);

        return columnChart;
    }

    // Reorder Alert Table (right side)
    public static BarChart<String, Number> getReorderAlertChart(Connection connection) {
        CategoryAxis reOrdrXAxis = new CategoryAxis();
        NumberAxis reOrdrYAxis = new NumberAxis();
        reOrdrXAxis.setLabel("Product");
        reOrdrYAxis.setLabel("Stock Quantity");

        BarChart<String, Number> reOrdrdColumnmChart = new BarChart<>(reOrdrXAxis, reOrdrYAxis);
        reOrdrdColumnmChart.setTitle("Reorder Product Alerts");
        reOrdrdColumnmChart.setBarGap(10);
        reOrdrdColumnmChart.setCategoryGap(20);
        reOrdrdColumnmChart.lookupAll(".chart-bar").forEach(node -> node.setStyle("-fx-bar-fill: blue;"));
        reOrdrXAxis.setTickLabelRotation(45);

        ArrayList<ItemHasSize> reorderItems = connection.getAllItemHasSizes();
        XYChart.Series<String, Number> reOrderFullStocks = new XYChart.Series<>();
        reOrderFullStocks.setName("Stocks");

        if (reorderItems.isEmpty()) {
            System.out.println("No stock data found in the database!");
        } else {
            for (ItemHasSize reorderItem : reorderItems) {
                int remainingLowQty = reorderItem.getRemainingQuantity();

                if (reorderItem.getRemainingQuantity() < 20) {
                    reOrderFullStocks.getData().add(new XYChart.Data<>(String.valueOf(reorderItem.getItemID()), reorderItem.getRemainingQuantity()));
                } else {
                    System.out.println("Skipping Item ID " + reorderItem.getItemID() + " (Qty = 0)");
                    reOrdrXAxis.setLabel("You have no items to order");
                    reOrdrXAxis.setStyle("-fx-font-size: 20;");
                }
            }
        }
        reOrdrdColumnmChart.getData().add(reOrderFullStocks);
        return reOrdrdColumnmChart;
    }

    // Shows the sales
    public static LineChart<String, Number> getSalesChart(Connection connection) {
        CategoryAxis salesXAxis = new CategoryAxis();
        NumberAxis salesYAxis = new NumberAxis();
        salesXAxis.setLabel("Date");
        salesYAxis.setLabel("Items");

        LineChart<String, Number> salesLineChart = new LineChart<>(salesXAxis, salesYAxis);
        salesLineChart.setTitle("Sales Trends Over Time");
        salesLineChart.setAnimated(false);

        XYChart.Series<String, Number> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");

        Map<String, Integer> itemNameToIndex = new HashMap<>();
        Map<Integer, String> indexToName = new HashMap<>();
        int indexCounter = 1;

        try {
            Statement stmt = connection.getJdbcConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT item_has_size_id, date FROM customer_has_item_has_size ORDER BY date");

            while (rs.next()) {
                int itemId = rs.getInt("item_has_size_id");
                String date = rs.getString("date");

                String itemName = connection.getItemNameById(itemId);
                if (!itemNameToIndex.containsKey(itemName)) {
                    itemNameToIndex.put(itemName, indexCounter);
                    indexToName.put(indexCounter, itemName);
                    indexCounter++;
                }

                int itemIndex = itemNameToIndex.get(itemName);
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(date, itemIndex);
                salesSeries.getData().add(dataPoint);
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (XYChart.Data<String, Number> data : salesSeries.getData()) {
            String itemLabel = indexToName.get(data.getYValue().intValue());
            Tooltip.install(data.getNode(), new Tooltip(itemLabel));
        }

        salesYAxis.setTickLabelFormatter(new StringConverter<Number>() {

            public String toString(Number object) {
                return indexToName.getOrDefault(object.intValue(), "Unknown");
            }

            public Number fromString(String string) {
                return null;
            }
        });

        salesLineChart.getData().add(salesSeries);
        return salesLineChart;
    }

    // Shows the revenue
    public static BarChart<String, Number> getRevenueChart(Connection connection) {
        CategoryAxis revenueXAxis = new CategoryAxis();
        NumberAxis revenueYAxis = new NumberAxis();
        revenueXAxis.setLabel("Product");
        revenueYAxis.setLabel("Quantity");

        BarChart<String, Number> revenueUnitChart = new BarChart<>(revenueXAxis, revenueYAxis);
        revenueUnitChart.setTitle("Revenue vs Units Sold");

        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Revenue");

        XYChart.Series<String, Number> unitsSoldSeries = new XYChart.Series<>();
        unitsSoldSeries.setName("Units Sold");

        Map<String, Integer> totalUnitsSold = new HashMap<>();
        Map<String, Double> totalRevenue = new HashMap<>();

        ObservableList<CheckoutItem> checkOutItems = connection.getCheckoutItemWithoutColor();

        for (CheckoutItem item : checkOutItems) {
            String name = item.getName();
            int quantity = item.getAmount();
            double price = item.getSellingPrice();
            double revenueAmount = quantity * price;

            totalUnitsSold.put(name, totalUnitsSold.getOrDefault(name, 0) + quantity);
            totalRevenue.put(name, totalRevenue.getOrDefault(name, 0.0) + revenueAmount);
        }

        for (String name : totalUnitsSold.keySet()) {;
            unitsSoldSeries.getData().add(new XYChart.Data<>(name, totalUnitsSold.get(name)));
            revenueSeries.getData().add(new XYChart.Data<>(name, totalRevenue.get(name)));
        }

        revenueUnitChart.getData().addAll(revenueSeries, unitsSoldSeries);
        revenueXAxis.setTickLabelRotation(45);
        revenueUnitChart.setCategoryGap(20);
        revenueUnitChart.setBarGap(10);
        revenueUnitChart.setPrefSize(800, 600);

        return revenueUnitChart;
    }
}
