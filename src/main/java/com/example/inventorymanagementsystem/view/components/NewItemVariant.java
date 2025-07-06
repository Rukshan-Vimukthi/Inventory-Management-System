package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;


import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;


public class NewItemVariant extends HBox {
    FormField<ComboBox, Stock> stock;
    FormField<ComboBox, Size> sizeOfItem;
    FormField<ComboBox, Color> colorOfItem;
    FormField<TextField, Double> priceOfItem;
    FormField<TextField, Double> sellingPriceOfItem;
    FormField<TextField, Integer> orderedQty;

    boolean isUpdate = false;
    int itemHasSizeID = 0;
    int colorHasItemHasSizeID = 0;

    int itemHasSizeHasStockID = 0;

    int id = 0;

    File selectedImage;

    Button selectImageButton;
    CheckBox editableCheckBox;
    public NewItemVariant(ItemDetail itemDetail) throws SQLException {
        super();

        if (itemDetail != null){
            editableCheckBox = new CheckBox("Edit");
            editableCheckBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    orderedQty.setEnabled(editableCheckBox.isSelected());
                }
            });

            System.out.println("Item Stock ID: " + itemDetail.getStockID());
            stock = new FormField<>("Stock", ComboBox.class, Data.getInstance().getStocks(), Connection.getInstance().getStock(itemDetail.getStockID()));
            stock.setEnabled(false);
            sizeOfItem = new FormField<>("Size", ComboBox.class, Data.getInstance().getSize(), Connection.getInstance().getSize((itemDetail.getSizeID())));
            sizeOfItem.setEnabled(false);
            colorOfItem = new FormField<>("Color", ComboBox.class, Data.getInstance().getColors(), Connection.getInstance().getColorByCode(itemDetail.getItemColor()));
            colorOfItem.setEnabled(false);

            priceOfItem = new FormField<>("Cost", TextField.class);
            priceOfItem.setValue(String.valueOf(itemDetail.getPrice()));
            priceOfItem.setEnabled(false);

            sellingPriceOfItem = new FormField<>("Selling Price", TextField.class);
            sellingPriceOfItem.setValue(String.valueOf(itemDetail.getSellingPrice()));
            sellingPriceOfItem.setEnabled(false);

            orderedQty = new FormField<>("Ordered Qty", TextField.class);
            orderedQty.setValue(String.valueOf(itemDetail.getOrderedQty()));
            orderedQty.setEnabled(false);

            selectImageButton = new Button("Select an Image");
            selectImageButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    selectedImage = fileChooser.showOpenDialog(null);
                }
            });

            isUpdate = true;

            itemHasSizeID = itemDetail.getItemHasSizeID();
            itemHasSizeHasStockID = itemDetail.getItemHasSizeHasStockID();
            colorHasItemHasSizeID = itemDetail.getColorHasItemHasSizeID();
            id = itemDetail.getId();
            this.getChildren().add(editableCheckBox);
            editableCheckBox.setAlignment(Pos.CENTER_LEFT);
        }else{
            stock = new FormField<>("Stock", ComboBox.class, Data.getInstance().getStocks());
            sizeOfItem = new FormField<>("Size", ComboBox.class, Data.getInstance().getSize());
            colorOfItem = new FormField<>("Color", ComboBox.class, Data.getInstance().getColors());
            priceOfItem = new FormField<>("Cost", TextField.class);
            sellingPriceOfItem = new FormField<>("Selling Price", TextField.class);
            orderedQty = new FormField<>("Ordered Qty", TextField.class);
            selectImageButton = new Button("Select an Image");
            selectImageButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    selectedImage = fileChooser.showOpenDialog(null);
                }
            });
        }

        ((ComboBox<Color>)colorOfItem.getComboBox()).setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
            @Override
            public ListCell<Color> call(ListView<Color> param) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        HBox pane = new HBox();
                        pane.setMaxWidth(20.0D);
                        pane.setMinWidth(20.0D);
                        pane.setMaxHeight(20.0D);
                        pane.setMinHeight(20.0D);
                        System.out.println(item);
                        if (item != null){
                            pane.setStyle("-fx-background-color: " + String.valueOf(item.getColor()) + "; ");
                            setText(item.getColor());
                        }
                        setGraphic(pane);
                        setGraphicTextGap(10.0D);
                    }
                };
            }
        });
        this.setSpacing(5.0D);
        this.getChildren().addAll(stock, sizeOfItem, colorOfItem, priceOfItem, sellingPriceOfItem, orderedQty, selectImageButton);
    }

    public int getStockID(){
        Stock stockData = ((Stock)stock.getValue());
        if (stockData != null) {
            return stockData.getId();
        }
        return 0;
    }

    public int getSizeID(){
        Size size = ((Size)sizeOfItem.getValue());
        if (size != null) {
            return size.getId();
        }
        return 0;
    }

    public int getColorID(){
        Color color = ((Color)colorOfItem.getValue());
        if(color != null) {
            return color.getId();
        }
        return 0;
    }

    public double getPrice(){
        return Double.parseDouble((String)priceOfItem.getValue());
    }

    public double getSellingPrice() {
        return Double.parseDouble((String)sellingPriceOfItem.getValue());
    }

    public int getOrderedQty(){
        return Integer.parseInt((String)orderedQty.getValue());
    }

    public boolean isUpdate(){
        return isUpdate;
    }

    public int getItemId(){
        return id;
    }

    public int getItemHasSizeID() {
        return itemHasSizeID;
    }

    public int getColorHasItemHasSizeID(){
        return colorHasItemHasSizeID;
    }

    public int getItemHasSizeHasStockID() {
        return itemHasSizeHasStockID;
    }

    public File getSelectedImage(){
        return selectedImage;
    }
}
