package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.services.interfaces.TableContainerInterface;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;


public class TableContainer<T> extends VBox {
    private T model;
    private TableView<T> tableView;

    private TableContainerInterface tableContainerInterface;

//    private final ObservableList<TableColumn<T, ?>> tableColumns = FXCollections.observableArrayList();
    public TableContainer(){
        this.setPadding(new Insets(5.0D));

        VBox toolBarContainer = new VBox();
        toolBarContainer.setFillWidth(true);

        HBox searchBarContainer = new HBox();
        HBox toolBar = new HBox();
        toolBar.setSpacing(10.0D);

        TextField searchTextField = new TextField();
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        Button search = new Button("Search");

        Button addItemButton = new Button("Add");
        addItemButton.setOnAction((actionEvent) -> {
            tableContainerInterface.addItem();
        });

        Button refresh = new Button("Refresh");
        refresh.setOnAction(event -> {
            tableContainerInterface.refresh();
        });

        Button delete = new Button("Delete");
        delete.setOnAction(event -> {
            tableContainerInterface.delete(tableView.getSelectionModel().getSelectedItem());
        });

        Button update = new Button("Update");
        update.setOnAction(event -> {
            tableContainerInterface.update(tableView.getSelectionModel().getSelectedItem());
        });

        HBox.setHgrow(addItemButton, Priority.ALWAYS);
        HBox.setHgrow(refresh, Priority.ALWAYS);
        HBox.setHgrow(delete, Priority.ALWAYS);
        HBox.setHgrow(update, Priority.ALWAYS);

        searchBarContainer.getChildren().addAll(searchTextField, search);
        toolBar.getChildren().addAll(addItemButton, refresh, delete, update);
        toolBarContainer.getChildren().addAll(searchBarContainer, toolBar);
        toolBar.setPadding(new Insets(2.5D, 0.0D, 2.5D, 0.0D));

        tableView = new TableView<>();

//        tableView.getColumns().addAll(tableColumns);
        this.getChildren().addAll(toolBarContainer, tableView);
    }

    public <DT> void addColumn(String name, DT dataType){
        TableColumn<T, DT> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(name));
        tableView.getColumns().add(column);
    }

    public void addItems(ObservableList<T> data){
        tableView.getItems().addAll(data);
    }

    public void setOnActionPerformed(TableContainerInterface item){
        this.tableContainerInterface = item;
    }

}
