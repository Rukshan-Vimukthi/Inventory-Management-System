package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.Item;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.ItemPreview;
import com.example.inventorymanagementsystem.view.components.TableContainer;
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

        HBox itemTableToolContainer = new HBox();
        TextField itemSearchBox = new TextField();
        Button searchButton = new Button("search");
        itemTableToolContainer.getChildren().addAll(itemSearchBox, searchButton);

        TableContainer<Item> itemsTable = new TableContainer<>();
        itemsTable.addColumn("id", Integer.class);
        itemsTable.addColumn("name", Integer.class);
        itemsTable.addColumn("price", Integer.class);
        itemsTable.addColumn("sellingPrice", Integer.class);
        itemsTable.addColumn("stockID", Integer.class);

        ItemPreview itemPreview = new ItemPreview(null);

        itemTableContainer.getChildren().addAll(itemTableToolContainer, itemsTable, itemPreview);
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

        TableContainer<Color> colorTableContainer = new TableContainer<>();
        colorTableContainer.addColumn("id", Integer.class);
        colorTableContainer.addColumn("color", String.class);
        colorTableContainer.addItems(Data.getInstance().getColors());

        TableContainer<Size> itemSizeTableContainer = new TableContainer<>();
        itemSizeTableContainer.addColumn("id", Integer.class);
        itemSizeTableContainer.addColumn("size", String.class);
        itemSizeTableContainer.addItems(Data.getInstance().getSize());

        otherTablesContainer.getChildren().addAll(stockTableContainer, colorTableContainer, itemSizeTableContainer);

        this.getChildren().addAll(itemTableContainer, otherTablesContainer);
    }
}
