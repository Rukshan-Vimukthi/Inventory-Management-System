package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.view.Analytics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChartKeeper {
    private Connection connection;
    private VBox mainLayout;

    // Stocks level chart (left side)
    public static BarChart<String, Number> getStockLevelChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Products");
        for (Node label : xAxis.lookupAll(".axis-label")) {
            label.setRotate(90);
        }
        yAxis.setLabel("Stock Quantity");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Product Stock Level");
        chart.setBarGap(10);
        chart.setCategoryGap(20);
        chart.setStyle("-fx-font-size: 17px;");
        xAxis.setTickLabelRotation(45);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stocks");

        Connection connection = Connection.getInstance();
        ArrayList<ItemHasSize> items = connection.getAllItemHasSizes();

        Map<String, Integer> aggregatedStocks = new HashMap<>();
        for (ItemHasSize item : items) {
            int remainingQty = item.getRemainingQuantity();

            if (remainingQty > 40) {
                int itemId = item.getItemID();
                String itemName = connection.getItemNameById(itemId);

                aggregatedStocks.put(itemName, aggregatedStocks.getOrDefault(itemName, 0) + remainingQty);
            }
        }
        for (Map.Entry<String, Integer> entry : aggregatedStocks.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
        return chart;
    }

    // Reorder Alert Table (right side)
    public static BarChart<String, Number> getReorderAlertChart(Connection connection) {
        CategoryAxis reordrXAxis = new CategoryAxis();
        NumberAxis reordrYAxis = new NumberAxis();

        BarChart<String, Number> reOrdrdColumnmChart = new BarChart<>(reordrXAxis, reordrYAxis);
        reOrdrdColumnmChart.setTitle("Reorder Product Alerts");
        reOrdrdColumnmChart.getStyleClass().add("charts");
        reOrdrdColumnmChart.setBarGap(10);
        reOrdrdColumnmChart.setCategoryGap(20);
        reOrdrdColumnmChart.lookupAll(".chart-bar").forEach(node -> node.setStyle("-fx-bar-fill: blue;"));
        reordrXAxis.setTickLabelRotation(45);

        ArrayList<ItemHasSize> reorderItems = connection.getAllItemHasSizes();
        XYChart.Series<String, Number> reOrderFullStocks = new XYChart.Series<>();
        reOrderFullStocks.setName("Restock Needed");

        if (reorderItems.isEmpty()) {
            System.out.println("No stock data found in the database!");
        } else {
            for (ItemHasSize reorderItem : reorderItems) {
                int quantity = reorderItem.getRemainingQuantity();

                if (quantity < 20) {
                    int displayValue = (quantity == 0) ? 1 : quantity;

                    String itemName = connection.getItemNameById(reorderItem.getItemID());

                    XYChart.Data<String, Number> data = new XYChart.Data<>(itemName, displayValue);
                    reOrderFullStocks.getData().add(data);

                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            String color = (quantity == 0) ? "red" : "#3B82F6";
                            newNode.setStyle("-fx-bar-fill: " + color + ";");

                            Tooltip.install(newNode, new Tooltip(
                                    itemName + " - " + (quantity == 0 ? "Out of Stock" : "Low Stock: " + quantity)
                            ));
                        }
                    });
                }
            }
        }
        reOrdrdColumnmChart.getData().clear();
        reOrdrdColumnmChart.getData().add(reOrderFullStocks);
        reOrdrdColumnmChart.setLegendVisible(false);
        reOrdrdColumnmChart.setCategoryGap(30);
        reOrdrdColumnmChart.setBarGap(10);
        reOrdrdColumnmChart.setStyle("-fx-font-size: 16px;");
        return reOrdrdColumnmChart;
    }

    // Shows the sales
    public static LineChart<String, String> getSalesChart(Connection connection, String filter) {
        CategoryAxis xAxis = new CategoryAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setLabel("Items");
        yAxis.setLabel("Sale Dates");

        LineChart<String, String> salesLineChart = new LineChart<>(xAxis, yAxis);
        salesLineChart.setTitle("Sales Dates by Item");
        salesLineChart.setAnimated(false);
        salesLineChart.getStyleClass().add("charts");

        XYChart.Series<String, String> salesSeries = new XYChart.Series<>();
        salesSeries.setName("Sales");

        Set<String> itemLabels = new LinkedHashSet<>();
        Set<String> uniqueDates = new LinkedHashSet<>();
        Map<XYChart.Data<String, String>, Tooltip> tooltipMap = new HashMap<>();

        try {
            ResultSet rs = connection.getFilteredSalesData(filter);

            while (rs.next()) {
                int itemId = rs.getInt("item_has_size_id");
                String date = rs.getString("date");
                String price = rs.getString("price");
                String amount = rs.getString("amount");

                if (date == null || date.isBlank()) continue;

                String itemName = connection.getItemNameById(itemId);
                String itemSize = connection.getItemSizeById(itemId);
                String fullItemLabel = itemName + " - " + itemSize;

                itemLabels.add(fullItemLabel);
                uniqueDates.add(date);

                XYChart.Data<String, String> dataPoint = new XYChart.Data<>(fullItemLabel, date);
                salesSeries.getData().add(dataPoint);

                String popupText = itemName + "\nAmount: " + amount + "\nPrice: $" + price;
                Tooltip tooltip = new Tooltip(popupText);
                tooltip.setStyle("""
                -fx-background-color: #1f2937;
                -fx-text-fill: white;
                -fx-padding: 8px;
                -fx-background-radius: 6px;
                -fx-font-size: 13px;
                -fx-border-color: #93c5fd;
                -fx-border-radius: 6px;
            """);
                tooltipMap.put(dataPoint, tooltip);

                dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setOnMouseClicked(event -> {
                            Tooltip current = tooltipMap.get(dataPoint);
                            if (current.isShowing()) {
                                current.hide();
                            } else {
                                current.show(newNode, event.getScreenX() + 10, event.getScreenY() - 30);
                            }
                        });
                        newNode.setOnMouseEntered(event -> {
                            Tooltip current = tooltipMap.get(dataPoint);
                            if (current.isShowing()) {
                                current.hide();
                            } else {
                                current.show(newNode, event.getScreenX() + 10, event.getScreenY() - 30);
                            }
                        });
                        newNode.setOnMouseExited(event -> {
                            Tooltip current = tooltipMap.get(dataPoint);
                            if (current.isShowing()) {
                                current.hide();
                            } else {
                                current.hide();
                            }
                        });

                        VBox mainLayout = Analytics.getMainLayout();
                        if (mainLayout != null) {
                            mainLayout.addEventFilter(ScrollEvent.SCROLL, scrollEvent -> {
                                Tooltip current = tooltipMap.get(dataPoint);
                                if (current != null && current.isShowing()) {
                                    current.hide();
                                }
                            });
                        }
                    }
                });
            }

            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (salesSeries.getData().isEmpty()) {
            Label noDataMsg = new Label("No data found related to the selected dates");
            noDataMsg.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            return salesLineChart;
        }

        xAxis.setCategories(FXCollections.observableArrayList(itemLabels));
        yAxis.setCategories(FXCollections.observableArrayList(uniqueDates));
        salesLineChart.getData().add(salesSeries);

        return salesLineChart;
    }

    // For setting the dates as filters for showing the sales
    private static String getFilterCondition(String filter) {
        switch (filter) {
            case "Today":
                return " WHERE DATE(date) = CURDATE()";
            case "Yesterday":
                return " WHERE DATE(date) = CURDATE() - INTERVAL 1 DAY";
            case "Last 7 Days":
                return " WHERE date >= CURDATE() - INTERVAL 7 DAY";
            case "This Month":
                return " WHERE MONTH(date) = MONTH(CURDATE()) AND YEAR(date) = YEAR(CURDATE())";
            case "Last Month":
                return " WHERE MONTH(date) = MONTH(CURDATE() - INTERVAL 1 MONTH) AND YEAR(date) = YEAR(CURDATE() - INTERVAL 1 MONTH)";
            case "This Year":
                return " WHERE YEAR(date) = YEAR(CURDATE())";
            case "Last Year":
                return " WHERE YEAR(date) = YEAR(CURDATE() - INTERVAL 1 YEAR)";
            default:
                return "";
        }
    }

    // Shows the revenue
    public static BarChart<String, Number> getRevenueChart(Connection connection) {
        CategoryAxis revenueXAxis = new CategoryAxis();
        NumberAxis revenueYAxis = new NumberAxis();
        revenueXAxis.setLabel("Product");
        revenueYAxis.setLabel("Quantity");

        BarChart<String, Number> revenueUnitChart = new BarChart<>(revenueXAxis, revenueYAxis);
        revenueUnitChart.setTitle("Revenue vs Units Sold");
        revenueUnitChart.getStyleClass().add("charts");

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
