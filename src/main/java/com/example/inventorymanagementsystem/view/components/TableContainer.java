package com.example.inventorymanagementsystem.view.components;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class TableContainer<T> extends VBox {
    private T model;
    private TableView<T> tableView;
//    private final ObservableList<TableColumn<T, ?>> tableColumns = FXCollections.observableArrayList();
    public TableContainer(){
        this.setPadding(new Insets(5.0D));

        HBox toolBar = new HBox();
        Button addItemButton = new Button("Add");
        toolBar.getChildren().add(addItemButton);
        toolBar.setPadding(new Insets(2.5D, 0.0D, 2.5D, 0.0D));

        tableView = new TableView<>();

//        tableView.getColumns().addAll(tableColumns);
        this.getChildren().addAll(toolBar, tableView);
    }

    public <DT> void addColumn(String name, DT dataType){
        TableColumn<T, DT> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(name));
        tableView.getColumns().add(column);
    }

    public void addItems(ObservableList<T> data){
        tableView.getItems().addAll(data);
    }
}
