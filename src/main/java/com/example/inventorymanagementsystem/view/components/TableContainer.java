package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import com.example.inventorymanagementsystem.services.interfaces.TableContainerInterface;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;


public class TableContainer<T> extends VBox {
    private T model;
    private TableView<T> tableView;
    private TableContainerInterface<T> tableContainerInterface;

    TextField searchTextField;
    Button search;

    List<FormField<? extends Control, ?>> formFields;

    HBox extraButtonsContainer;
    HBox filters;

//    private final ObservableList<TableColumn<T, ?>> tableColumns = FXCollections.observableArrayList();
    public TableContainer(boolean advancedSearchBar, String defaultSearchBarLabelText, String placeHolder){
        this.setPadding(new Insets(5.0D));
        formFields = new ArrayList<>();
        VBox toolBarContainer = new VBox();
        toolBarContainer.setFillWidth(true);

        FontIcon addIcon = new FontIcon(FontAwesomeSolid.PLUS);
        addIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon refreshIcon = new FontIcon(FontAwesomeSolid.RECYCLE);
        refreshIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
        deleteIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setFill(Paint.valueOf("#FFF"));

        HBox searchBarContainer = new HBox();
        searchBarContainer.setSpacing(10.0D);
        VBox searchFieldContainer = new VBox();
        HBox searchField = new HBox();
        searchField.setSpacing(10.0D);

        HBox toolBar = new HBox();
        toolBar.setSpacing(10.0D);

        filters = new HBox();
        filters.setSpacing(10.0D);

        Label searchLabel;
        searchTextField = new TextField();
        searchTextField.getStyleClass().add("default-text-areas");
        search = new Button("Search", searchIcon);
        search.getStyleClass().add("default-buttons");
        search.setOnAction(actionEvent -> {
            tableContainerInterface.onSearch(formFields, searchTextField.getText());
        });

        searchField.getChildren().addAll(searchTextField, search);
        HBox.setHgrow(searchTextField, Priority.ALWAYS);
        HBox.setHgrow(searchFieldContainer, Priority.ALWAYS);

        if (defaultSearchBarLabelText != null) {
            searchLabel = new Label("Search");
            searchFieldContainer.getChildren().addAll(searchLabel, searchField);
        }else{
            if (placeHolder != null) {
                searchTextField.setPromptText(placeHolder);
            }

            searchFieldContainer.getChildren().add(searchField);
        }

        Button addItemButton = new Button("Add", addIcon);
        addItemButton.getStyleClass().add("add-button");
        addItemButton.setOnAction((actionEvent) -> {
            tableContainerInterface.addItem();
        });

        Button refresh = new Button("Refresh", refreshIcon);
        refresh.getStyleClass().add("default-buttons");
        refresh.setOnAction(event -> {
            tableContainerInterface.refresh();
        });

        Button delete = new Button("Delete", deleteIcon);
        delete.getStyleClass().add("delete-button");
        delete.setOnAction(event -> {
            tableContainerInterface.delete(tableView.getSelectionModel().getSelectedItem());
        });

        Button update = new Button("Update");
        update.getStyleClass().add("update-button");
        update.setOnAction(event -> {
            tableContainerInterface.update(tableView.getSelectionModel().getSelectedItem());
        });
        update.setDisable(true);
        delete.setDisable(true);

        HBox.setHgrow(addItemButton, Priority.ALWAYS);
        HBox.setHgrow(refresh, Priority.ALWAYS);
        HBox.setHgrow(delete, Priority.ALWAYS);
        HBox.setHgrow(update, Priority.ALWAYS);

        searchBarContainer.getChildren().addAll(filters, searchFieldContainer);

        extraButtonsContainer = new HBox();
        extraButtonsContainer.setSpacing(10.0D);

        toolBar.getChildren().addAll(addItemButton, refresh, delete, update, extraButtonsContainer);

        toolBarContainer.getChildren().addAll(searchBarContainer, toolBar);

        toolBar.setPadding(new Insets(2.5D, 0.0D, 2.5D, 0.0D));

        tableView = new TableView<>();
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                update.setDisable(false);
                delete.setDisable(false);
                tableContainerInterface.onSelectItem(newValue);
            }
        });

//        tableView.getColumns().addAll(tableColumns);
        this.getChildren().addAll(toolBarContainer, tableView);
    }

    public <DT> void addColumn(String name, DT dataType){
        TableColumn<T, DT> column = new TableColumn<>(name);
        column.setCellValueFactory(new PropertyValueFactory<>(name));
        tableView.getColumns().add(column);
    }

    public void addItems(ObservableList<T> data){
        tableView.setItems(data);
    }

    public void setOnActionPerformed(TableContainerInterface<T> item){
        this.tableContainerInterface = item;
    }

    public void addFilter(FormField<? extends Control, ?> formField){
        filters.getChildren().add(formField);
        formFields.add(formField);
    }

    public void setFilters(String color, String size, String stock, String price, String name, String sellingPrice){

    }

    public void addExtraButton(Button button){
        extraButtonsContainer.getChildren().add(button);
    }

}
