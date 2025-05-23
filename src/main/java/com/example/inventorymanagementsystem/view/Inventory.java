package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.services.interfaces.TableContainerInterface;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.ItemPreview;
import com.example.inventorymanagementsystem.view.components.TableContainer;
import com.example.inventorymanagementsystem.view.dialogs.AddNewColor;
import com.example.inventorymanagementsystem.view.dialogs.AddNewSize;
import com.example.inventorymanagementsystem.view.dialogs.AddNewStock;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Inventory extends HBox {
    public Inventory(){
        super();

        // this contains the form to search item (filter) and the table of items and item preview component
        VBox itemTableContainer = new VBox();


        TableContainer<ItemDetail> itemsTable = new TableContainer<>();
        itemsTable.addColumn("id", Integer.class);
        itemsTable.addColumn("name", String.class);
        itemsTable.addColumn("price", Double.class);
        itemsTable.addColumn("sellingPrice", Double.class);
        itemsTable.addColumn("stockDate", Integer.class);
        itemsTable.addColumn("stockName", Integer.class);
        itemsTable.addColumn("size", Integer.class);
        itemsTable.addColumn("itemColor", Integer.class);
        itemsTable.addItems(Data.getInstance().getItemDetails());


        ItemPreview itemPreview = new ItemPreview(null);

        itemTableContainer.getChildren().addAll(itemsTable, itemPreview);
        HBox.setHgrow(itemTableContainer, Priority.ALWAYS);

        // this contains other tables which are connected to item like, color, stock, size
        VBox otherTablesContainer = new VBox();
        otherTablesContainer.setMaxWidth(300.0D);

        HBox.setHgrow(otherTablesContainer, Priority.NEVER);

        TableContainer<Stock> stockTableContainer = new TableContainer<>();
        stockTableContainer.addColumn("id", Integer.class);
        stockTableContainer.addColumn("date", String.class);
        stockTableContainer.addColumn("name", String.class);
        stockTableContainer.addItems(Data.getInstance().getStocks());
        stockTableContainer.setOnActionPerformed(new TableContainerInterface<Stock>() {
            @Override
            public void addItem() {
                AddNewStock addNewStock = new AddNewStock(null);
                addNewStock.show();
            }

            @Override
            public void refresh() {

            }

            @Override
            public void update(Stock item) {
                AddNewStock addNewStock = new AddNewStock(item);
                addNewStock.show();
            }

            @Override
            public void delete(Stock item) {

            }

        });

        TableContainer<Color> colorTableContainer = new TableContainer<>();
        colorTableContainer.addColumn("id", Integer.class);
        colorTableContainer.addColumn("color", String.class);
        colorTableContainer.addItems(Data.getInstance().getColors());
        colorTableContainer.setOnActionPerformed(new TableContainerInterface<Color>() {
            @Override
            public void addItem() {
                AddNewColor addNewColor = new AddNewColor(null);
                addNewColor.show();
            }

            @Override
            public void refresh() {

            }

            @Override
            public void update(Color item) {
                AddNewColor addNewColor = new AddNewColor(item);
                addNewColor.show();
            }

            @Override
            public void delete(Color item) {

            }

        });

        TableContainer<Size> itemSizeTableContainer = new TableContainer<>();
        itemSizeTableContainer.addColumn("id", Integer.class);
        itemSizeTableContainer.addColumn("size", String.class);
        itemSizeTableContainer.addItems(Data.getInstance().getSize());
        itemSizeTableContainer.setOnActionPerformed(new TableContainerInterface<Size>() {
            @Override
            public void addItem() {
                AddNewSize addNewSize = new AddNewSize(null);
                addNewSize.show();
            }

            @Override
            public void refresh() {

            }

            @Override
            public void update(Size item) {
                AddNewSize addNewSize = new AddNewSize(item);
                addNewSize.show();
            }

            @Override
            public void delete(Size item) {

            }

        });

        otherTablesContainer.getChildren().addAll(stockTableContainer, colorTableContainer, itemSizeTableContainer);

        this.getChildren().addAll(itemTableContainer, otherTablesContainer);
    }
}
