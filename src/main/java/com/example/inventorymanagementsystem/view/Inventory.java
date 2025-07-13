package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.controllers.dialogs.AddNewItemController;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.*;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.services.interfaces.TableContainerInterface;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.ItemPreview;
import com.example.inventorymanagementsystem.view.components.TableContainer;
import com.example.inventorymanagementsystem.view.dialogs.AddNewColor;
import com.example.inventorymanagementsystem.view.dialogs.AddNewItem;
import com.example.inventorymanagementsystem.view.dialogs.AddNewSize;
import com.example.inventorymanagementsystem.view.dialogs.AddNewStock;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class Inventory extends HBox implements ThemeObserver {
    ItemPreview itemPreview;
    public Inventory() throws SQLException {
        super();

        // this contains the form to search item (filter) and the table of items and item preview component
        VBox itemTableContainer = new VBox();
        itemTableContainer.setSpacing(10.0D);
        itemTableContainer.setPadding(new Insets(10.0D));

        TableContainer<ItemDetail> itemsTable = new TableContainer<>(true, "Item Name", null);
        itemsTable.addColumn("itemHasSizeID", "Item ID");
        itemsTable.addColumn("name", String.class);
        itemsTable.addColumn("price", Double.class);
        itemsTable.addColumn("sellingPrice", Double.class);
        itemsTable.addColumn("stockDate", Integer.class);
        itemsTable.addColumn("stockName", Integer.class);
        itemsTable.addColumn("size", Integer.class);
        itemsTable.addColumn("itemColor", Integer.class);
        itemsTable.addColumn("itemColor", "Color View");
        itemsTable.addColumn("orderedQty", "OrderedQty");
        itemsTable.addColumn("remainingQty", "Remaining Qty");
        itemsTable.addItems(Data.getInstance().getItemDetails());

//        Button addSizeButton = new Button("Add Size");
//        addSizeButton.getStyleClass().add("add-button");
//        addSizeButton.getStyleClass().add("default-buttons");
//
//        Button addColorButton = new Button("Add Color");
//        addColorButton.getStyleClass().add("add-button");
//        addColorButton.getStyleClass().add("default-buttons");
//
//        itemsTable.addExtraButton(addSizeButton);
//        itemsTable.addExtraButton(addColorButton);

        FormField<ComboBox, Size> sizeFilter = new FormField<>("Size", ComboBox.class, Data.getInstance().getSize());
        sizeFilter.setColumnName("`size`.`size`");
        sizeFilter.setMaxWidth(50.0D);

        FormField<ColorPicker, String> colorFilter = new FormField<>("Color", ColorPicker.class);
        colorFilter.setColumnName("`color`.`color`");

        FormField<ComboBox, Stock> stockFilter = new FormField<>("Stock", ComboBox.class, Data.getInstance().getStocks());
        stockFilter.setColumnName("`stock`.`name`");

        FormField<TextField, Double> costFilter = new FormField<>("Cost", TextField.class);
        costFilter.setColumnName("`item_has_size`.`cost`");
        costFilter.setMaxWidth(100.0D);

        FormField<TextField, Double> priceFilter = new FormField<>("Price", TextField.class);
        priceFilter.setColumnName("`item_has_size`.`cost`");
        priceFilter.setMaxWidth(100.0D);

        itemsTable.addFilter(sizeFilter);
        itemsTable.addFilter(colorFilter);
        itemsTable.addFilter(stockFilter);
        itemsTable.addFilter(costFilter);
        itemsTable.addFilter(priceFilter);
        itemsTable.setTableTitle("Items");

        itemsTable.showDeleteButton(false);

        itemsTable.setOnActionPerformed(new TableContainerInterface<ItemDetail>() {

            @Override
            public void addItem() {
                AddNewItem addNewItem = new AddNewItem(null);
                addNewItem.show();
            }

            @Override
            public void refresh() {
                try {
                    Data.getInstance().refreshItemDetails();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void update(ItemDetail itemDetail) {
                AddNewItem addNewItem = new AddNewItem(itemDetail);
                addNewItem.show();
            }

            @Override
            public void delete(ItemDetail itemDetail) {
            }

            @Override
            public void onSelectItem(ItemDetail item) throws SQLException {
//                System.out.println(item.getName());
                if (itemPreview != null){
                    itemPreview.update(item);
                    itemsTable.showUpdateButton(true);
                }
            }

            @Override
            public void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText) {
                double cost = 0.0D;
                double sellingPrice = 0.0D;

                try{
                    cost = Double.parseDouble((String)costFilter.getValue());
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }

                try{
                    sellingPrice = Double.parseDouble((String)priceFilter.getValue());
                }catch(NumberFormatException e){
                    e.printStackTrace();
                }

                try{
                    Connection.getInstance().filterItems(
                            (String)colorFilter.getValue(),
                            ((Size)sizeFilter.getValue()).getSize(),
                            (Stock)stockFilter.getValue(),
                            cost,
                            searchBoxText,
                            sellingPrice
                    );
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

        });

        itemPreview = new ItemPreview();

        VBox.setVgrow(itemsTable, Priority.ALWAYS);
        itemTableContainer.setPadding(new Insets(5.0D, 0.0D, 10.0D, 0.0D));

        itemTableContainer.getChildren().addAll(itemsTable, itemPreview);
        HBox.setHgrow(itemTableContainer, Priority.ALWAYS);

        // this contains other tables which are connected to item like, color, stock, size
        VBox otherTablesContainer = new VBox();
        otherTablesContainer.setMaxWidth(300.0D);

        HBox.setHgrow(otherTablesContainer, Priority.NEVER);

        TableContainer<Stock> stockTableContainer = new TableContainer<>(false, null, "Enter stock date or name");
        stockTableContainer.addColumn("id", Integer.class);
        stockTableContainer.addColumn("date", String.class);
        stockTableContainer.addColumn("name", String.class);
        stockTableContainer.addItems(Data.getInstance().getStocks());
        stockTableContainer.setTableTitle("Stock");
        stockTableContainer.showDeleteButton(false);
        stockTableContainer.showUpdateButton(false);
        stockTableContainer.setOnActionPerformed(new TableContainerInterface<Stock>() {
            @Override
            public void addItem() {
                AddNewStock addNewStock = new AddNewStock(null);
                addNewStock.show();
            }

            @Override
            public void refresh() {
                try{
                    Data.getInstance().refreshStock();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void update(Stock item) {
                AddNewStock addNewStock = new AddNewStock(item);
                addNewStock.show();
            }

            @Override
            public void delete(Stock item) {
                try{
                    Connection.getInstance().deleteStock(item.getId());
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSelectItem(Stock item) {

            }

            @Override
            public void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText) {
                if (formFields.size() > 0){
                    for (FormField<?, ?> formField : formFields){
                        System.out.println(formField.getColumnName() + ": " + formField.getValue());
                    }
                }
                System.out.println(searchBoxText);
                try {
                    Data.getInstance().setStocks(Connection.getInstance().filterStocks(searchBoxText));
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

        });

        TableContainer<Color> colorTableContainer = new TableContainer<>(false, null, "Enter color code");
        colorTableContainer.addColumn("id", Integer.class);
        colorTableContainer.addColumn("color", String.class);
        colorTableContainer.addColumn("color", "color view");
        colorTableContainer.addItems(Data.getInstance().getColors());
        colorTableContainer.setTableTitle("Color");
        colorTableContainer.showDeleteButton(false);
        colorTableContainer.showUpdateButton(false);
        colorTableContainer.setOnActionPerformed(new TableContainerInterface<Color>() {
            @Override
            public void addItem() {
                AddNewColor addNewColor = new AddNewColor(null);
                addNewColor.show();
            }

            @Override
            public void refresh() {
                try {
                    Data.getInstance().refreshColors();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void update(Color item) {
                AddNewColor addNewColor = new AddNewColor(item);
                addNewColor.show();
            }

            @Override
            public void delete(Color item) {
                try {
                    Connection.getInstance().deleteColor(item.getId());
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSelectItem(Color item) {

            }

            @Override
            public void onSearch(List<FormField<?, ?>> formFields, String searchBoxText) {
                try {
                    Data.getInstance().setColors(Connection.getInstance().filterColors(searchBoxText));
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        });

        TableContainer<Size> itemSizeTableContainer = new TableContainer<>(false, null, "Enter Size");
        itemSizeTableContainer.addColumn("id", Integer.class);
        itemSizeTableContainer.addColumn("size", String.class);
        itemSizeTableContainer.addItems(Data.getInstance().getSize());
        itemSizeTableContainer.setTableTitle("Size");
        itemSizeTableContainer.showDeleteButton(false);
        itemSizeTableContainer.showUpdateButton(false);

        itemSizeTableContainer.setOnActionPerformed(new TableContainerInterface<Size>() {
            @Override
            public void addItem() {
                AddNewSize addNewSize = new AddNewSize(null);
                addNewSize.show();
            }

            @Override
            public void refresh() {
                try {
                    Data.getInstance().refreshSize();
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void update(Size item) {
                AddNewSize addNewSize = new AddNewSize(item);
                addNewSize.show();
            }

            @Override
            public void delete(Size item) {
                try {
                    Connection.getInstance().deleteSize(item.getId());
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSelectItem(Size item) {

            }

            @Override
            public void onSearch(List<FormField<?, ?>> formFields, String searchBoxText) {
                try {
                    Data.getInstance().setSize(Connection.getInstance().filterSizes(searchBoxText));
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }

        });

        otherTablesContainer.getChildren().addAll(stockTableContainer, colorTableContainer, itemSizeTableContainer);

        this.getChildren().addAll(itemTableContainer, otherTablesContainer);
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: #EEE; ");
        this.getStylesheets().remove(Constants.DARK_THEME_CSS);
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #222; ");
        this.getStylesheets().remove(Constants.LIGHT_THEME_CSS);
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }
}
