package com.example.inventorymanagementsystem.view.components;
import com.example.inventorymanagementsystem.models.CheckoutItem;
import com.example.inventorymanagementsystem.models.RevenueData;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableCell;
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
                String itemSize = connection.getItemSizeById(productId);
                String itemLabel = itemName + "-" + itemSize;

                stockData.add(new StockRow(itemLabel, remainingQty));
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

        TableColumn<ItemHasSize, Integer> itemIdCol = new TableColumn<>("Item Name");
        itemIdCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getItemID()).asObject()
        );

        itemIdCol.setCellFactory(col -> new TableCell<ItemHasSize, Integer>() {
            @Override
            protected void updateItem(Integer itemId, boolean empty) {
                super.updateItem(itemId, empty);
                if (empty || itemId == null) {
                    setText(null);
                } else {
                    String itemName = connection.getItemNameById(itemId) + " | " + connection.getItemSizeById(itemId);
                    setText(itemName);
                }
            }
        });

        TableColumn<ItemHasSize, Integer> stockQtyCol = new TableColumn<>("Remaining Quantity");
        stockQtyCol.setCellValueFactory(new PropertyValueFactory<>("remainingQuantity"));

        TableColumn<ItemHasSize, Integer> orderedQtyCol = new TableColumn<>("Ordered Quantity");
        orderedQtyCol.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));

        TableColumn<ItemHasSize, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(itemIdCol, orderedQtyCol, stockQtyCol, priceCol);

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

    public static TableView<SalesRow> getSalesTable(String filter) {
        Connection connection = Connection.getInstance(); // Your DB connection

        TableView<SalesRow> salesTable = new TableView<>();
        salesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        salesTable.setPrefWidth(800);
        salesTable.setMaxWidth(Double.MAX_VALUE);

        TableColumn<SalesRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<SalesRow, Integer> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<SalesRow, Integer> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SalesRow, Integer> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        salesTable.getColumns().addAll(dateCol, itemCol, priceCol, amountCol);

        ObservableList<SalesRow> salesData = FXCollections.observableArrayList();

        try (ResultSet rs = connection.getFilteredSalesData(filter)) {

            while (rs.next()) {
                String date = rs.getString("date");
                int itemId = rs.getInt("item_has_size_id");
                int priceValue = rs.getInt("price");
                int amountValue = rs.getInt("amount");
                String itemName = connection.getItemNameById(itemId);
                String itemSize = connection.getItemSizeById(itemId);
                String itemLabel = itemName + "-" + itemSize;
                salesData.add(new SalesRow(itemLabel, date, priceValue, amountValue));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        salesTable.setItems(salesData);
        salesTable.lookupAll(".scroll-bar").forEach(sb -> {
            System.out.println("Scrollbar: " + sb);
            sb.lookupAll(".thumb").forEach(thumb -> {
                System.out.println("Thumb: " + thumb + ", opacity: " + thumb.getOpacity());
            });
        });
        salesTable.lookupAll(".scroll-bar").forEach(node -> System.out.println(node));
        return salesTable;
    }

    public static class SalesRow {
        private final String date;
        private final String itemName;
        private final Integer price;
        private final Integer amount;

        public SalesRow(String date, String itemName, Integer price, Integer amount) {
            this.date = date;
            this.itemName = itemName;
            this.price = price;
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }
        public String getItemName() {
            return itemName;
        }
        public Integer getPrice(){return price;}
        public Integer getAmount(){return amount;}
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
