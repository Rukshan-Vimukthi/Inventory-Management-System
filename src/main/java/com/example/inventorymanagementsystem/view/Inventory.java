package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Data;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Inventory extends HBox {
    public Inventory(){
        super();

        VBox itemTableContainer = new VBox();
        VBox otherTablesContainer = new VBox();

        TableView<Stock> stockTableView = new TableView<>();
        stockTableView.getItems().addAll(Data.getInstance().getStocks());

        TableColumn<Stock, Integer> idColumn = new TableColumn<>("id");
        TableColumn<Stock, String> dateColumn = new TableColumn<>("date");
        TableColumn<Stock, String> nameColumn = new TableColumn<>("name");

        stockTableView.getColumns().addAll(idColumn, dateColumn, nameColumn);

        otherTablesContainer.getChildren().add(stockTableView);

        this.getChildren().addAll(itemTableContainer, otherTablesContainer);
    }
}
