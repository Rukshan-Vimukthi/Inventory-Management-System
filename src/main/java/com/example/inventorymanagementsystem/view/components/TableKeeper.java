package com.example.inventorymanagementsystem.view.components;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.RevenueData;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.db.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableKeeper {
    public static TableView<StockRow> getStockLevelTable() {
        Connection connection = Connection.getInstance();
        ObservableList<StockRow> stockData = FXCollections.observableArrayList();

        for (ItemHasSize item : connection.getAllItemHasSizes()) {
            int remainingQty = item.getRemainingQuantity();

            if (remainingQty > 40) {
                int productId = item.getItemID();
                String itemName = connection.getItemNameById(productId);

                stockData.add(new StockRow(itemName, remainingQty));
            }
        }

        TableView<StockRow> table = new TableView<>();
        table.setItems(stockData);

        TableColumn<StockRow, String> idCol = new TableColumn<>("Product Name");
        idCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<StockRow, Integer> qtyCol = new TableColumn<>("Remaining Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(idCol, qtyCol);
        table.setPrefHeight(400);

        return table;
    }

    public static class StockRow {
        private final String ItemName;
        private final int quantity;

        public StockRow(String ItemName, int quantity) {
            this.ItemName = ItemName;
            this.quantity = quantity;
        }

        public String getItemName() {
            return ItemName;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static TableView<ItemHasSize> getReorderAlertTable() {
        Connection connection = Connection.getInstance();

        TableView<ItemHasSize> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefWidth(800);
        table.setMaxWidth(Double.MAX_VALUE);

        TableColumn<ItemHasSize, Integer> itemIdCol = new TableColumn<>("Item ID");
        itemIdCol.setCellValueFactory(new PropertyValueFactory<>("itemID"));

        TableColumn<ItemHasSize, Integer> stockQtyCol = new TableColumn<>("Remaining Quantity");
        stockQtyCol.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));

        table.getColumns().addAll(itemIdCol, stockQtyCol);

        ArrayList<ItemHasSize> allItems = connection.getAllItemHasSizes();
        ObservableList<ItemHasSize> reorderItems = FXCollections.observableArrayList();

        for (ItemHasSize item : allItems) {
            if (item.getRemainingQuantity() < 20) {
                reorderItems.add(item);
            }
        }

        table.setItems(reorderItems);
        return table;
    }

    public static TableView<SalesRow> getSalesTable() {
        Connection connection = Connection.getInstance(); // Your DB connection

        TableView<SalesRow> salesTable = new TableView<>();
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        salesTable.setPrefWidth(800);
        salesTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<SalesRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<SalesRow, Integer> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        salesTable.getColumns().addAll(dateCol, itemCol);

        ObservableList<SalesRow> salesData = FXCollections.observableArrayList();

        String query = "SELECT chs.date, chs.item_has_size_id FROM customer_has_item_has_size chs";

        try (PreparedStatement stmt = connection.getJdbcConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String date = rs.getString("date");
                int itemId = rs.getInt("item_has_size_id");
                String itemName = connection.getItemNameById(itemId);
                salesData.add(new SalesRow(date, itemName));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        salesTable.setItems(salesData);
        return salesTable;
    }
    public static class SalesRow {
        private final String date;
        private final String itemName;

        public SalesRow(String date, String itemName) {
            this.date = date;
            this.itemName = itemName;
        }

        public String getDate() {
            return date;
        }

        public String getItemName() {
            return itemName;
        }
    }

    public static TableView<RevenueData> getRevenueTable() {
        Connection connection = Connection.getInstance(); // Your singleton DB connection

        TableView<RevenueData> revenueTable = new TableView<>();
        revenueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        revenueTable.setPrefWidth(800);
        revenueTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<RevenueData, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<RevenueData, Integer> unitsSoldCol = new TableColumn<>("Units Sold");
        unitsSoldCol.setCellValueFactory(new PropertyValueFactory<>("unitsSold"));

        TableColumn<RevenueData, Double> revenueCol = new TableColumn<>("Revenue");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));

        revenueTable.getColumns().addAll(nameCol, unitsSoldCol, revenueCol);

        ObservableList<RevenueData> revenueDataList = FXCollections.observableArrayList();

        String query = """
        SELECT i.name AS product_name,
               SUM(chs.amount) AS total_units_sold,
               SUM(chs.amount * chs.price) AS total_revenue
        FROM customer_has_item_has_size chs
        JOIN item_has_size ihs ON chs.item_has_size_id = ihs.id
        JOIN item i ON ihs.item_id = i.id
        GROUP BY i.name
        ORDER BY total_revenue DESC
    """;

        try (PreparedStatement stmt = connection.getJdbcConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("product_name");
                int unitsSold = rs.getInt("total_units_sold");
                double revenue = rs.getDouble("total_revenue");

                revenueDataList.add(new RevenueData(name, unitsSold, revenue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        revenueTable.setItems(revenueDataList);
        return revenueTable;
    }
}
