package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.services.interfaces.ItemPreviewObserver;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Class that extends VBox to show the information of the selected item.
 */
public class ItemPreview extends HBox implements ItemPreviewObserver, ThemeObserver {
    VBox informationContainer;
    GridPane gridPane;

    Label itemName;
    Label itemPrice;
    Label itemStock;
    Label sellingPrice;
    Label orderedPrice;
    Label size;
    Label color;
    Label remainingStock;
    Label stockStatus;

    Label label;

    ImageView imageView;

    Label itemPriceLabel;
    Label itemSellingPriceLabel;
    Label itemSizeLabel;
    Label itemStockDateLabel;
    Label itemStockNameLabel;
    Label itemColorLabel;
    Label remainingStockCountLabel;
    Label totalItemsInStockLabel;

    /**
     *
     */
    public ItemPreview(){
        this.setStyle("-fx-border-radius: 10px; -fx-background-radius: 10px; ");
        this.getStyleClass().add("nav-bar");
        this.setPadding(new Insets(10.0D));
        imageView = new ImageView();
        imageView.setImage(new Image(Constants.IMAGE_UNAVAILABLE));
        imageView.setFitWidth(300.0D);
        imageView.setFitHeight(300.0D);
        imageView.setStyle("-fx-background-color: #888888;");
        this.getChildren().add(imageView);
        this.setAlignment(Pos.CENTER_LEFT);
        clearContent();
        this.setMinHeight(300.0D);
        this.setMaxHeight(300.0D);
        this.setPadding(new Insets(10.0D));
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    @Override
    public void update(ItemDetail itemDetail) {
        ItemHasSize itemHasSize = null;
        if(itemDetail != null){
            itemHasSize = Connection.getInstance().getItemHasSize(itemDetail);
            if (informationContainer == null){
                this.getChildren().remove(label);
                informationContainer = new VBox();
                informationContainer.setSpacing(10.0D);

                itemPriceLabel = new Label("Ordered Price");
                itemPriceLabel.getStyleClass().add("item-information-label-text");
                orderedPrice = new Label();
                orderedPrice.getStyleClass().add("item-information-label-text");
                itemPriceLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                orderedPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemSellingPriceLabel = new Label("Selling Price");
                itemSellingPriceLabel.getStyleClass().add("item-information-label-text");
                sellingPrice = new Label();
                sellingPrice.getStyleClass().add("item-information-label-text");
                itemSellingPriceLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                sellingPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemSizeLabel = new Label("Size");
                itemSizeLabel.getStyleClass().add("item-information-label-text");
                size = new Label();
                size.getStyleClass().add("item-information-label-text");
                itemSizeLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                size.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemStockDateLabel = new Label("Stock Date");
                itemStockDateLabel.getStyleClass().add("item-information-label-text");
                itemStock = new Label();
                itemStock.getStyleClass().add("item-information-label-text");
                itemStockDateLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                itemStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemStockNameLabel = new Label("Stock Name");
                itemStockNameLabel.getStyleClass().add("item-information-label-text");
                itemStockNameLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));

                itemColorLabel = new Label("Color");
                itemColorLabel.getStyleClass().add("item-information-label-text");
                itemColorLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));


                remainingStockCountLabel = new Label("Remaining Items");
                remainingStockCountLabel.getStyleClass().add("item-information-label-text");
                remainingStock = new Label();
                remainingStock.getStyleClass().add("item-information-label-text");
                remainingStockCountLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                remainingStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                totalItemsInStockLabel = new Label("Total Items Received");
                totalItemsInStockLabel.getStyleClass().add("item-information-label-text");
                totalItemsInStockLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));


                gridPane = new GridPane();
                gridPane.add(itemPriceLabel, 0, 0);
                gridPane.add(orderedPrice, 1, 0);

                gridPane.add(itemSellingPriceLabel, 0,1);
                gridPane.add(sellingPrice, 1,1);

                gridPane.add(itemSizeLabel, 0,2);
                gridPane.add(size, 1,2);

                gridPane.add(itemStockDateLabel, 0,3);
                gridPane.add(itemStock, 1,3);

                gridPane.add(itemStockNameLabel, 0,4);

                gridPane.add(itemColorLabel, 0,5);

                gridPane.add(remainingStockCountLabel, 0,6);
                gridPane.add(remainingStock, 1,6);

                gridPane.add(totalItemsInStockLabel, 0,7);


                itemName = new Label();
                itemName.getStyleClass().add("item-information-label-text");
                itemName.setFont(Font.font("Sans Serif", FontWeight.BOLD, 20.0D));

                stockStatus = new Label();
                stockStatus.getStyleClass().add("item-information-label-text");
                stockStatus.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));
                stockStatus.setStyle("" +
                        "-fx-background-color: #00FF0033; " +
                        "-fx-border-color: #00FF00; " +
                        "-fx-border-radius: 5px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px; " +
                        "-fx-padding: 5px;");


                if (itemHasSize != null){
                    itemHasSize.getOrderQuantity();
                }

                HBox header = new HBox();
                header.setSpacing(20.0D);
                header.getChildren().addAll(itemName, stockStatus);

                informationContainer.getChildren().addAll(header, gridPane);
                this.getChildren().add(informationContainer);
                this.setAlignment(Pos.TOP_LEFT);
            }

            System.out.println(itemDetail.getName());
            System.out.println(itemDetail.getPrice());

            itemName.setText(itemDetail.getName());
            orderedPrice.setText(String.valueOf(itemDetail.getPrice()));
            sellingPrice.setText(String.valueOf(itemDetail.getSellingPrice()));
            size.setText(itemDetail.getSize());
            itemStock.setText(itemDetail.getStockDate());
            remainingStock.setText(itemDetail.getName());
            stockStatus.setText("In Stock");

            if (itemHasSize != null){
                orderedPrice.setText(String.valueOf(itemHasSize.getCost()));
            }
        }
    }

    public void clearContent(){
        label = new Label("No item data to preview");
        label.setFont(Font.font(18.0D));
        label.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.TOP_LEFT);
        this.getChildren().add(label);
        this.setSpacing(10.0D);
    }

    @Override
    public void lightTheme() {
        this.setStyle(this.getStyle() + "-fx-background-color: #AAA; ");
        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
//        itemName.setTextFill(Paint.valueOf("#000"));
//        itemPrice.setTextFill(Paint.valueOf("#000"));
//        itemStock.setTextFill(Paint.valueOf("#000"));
//        sellingPrice.setTextFill(Paint.valueOf("#000"));
//        orderedPrice.setTextFill(Paint.valueOf("#000"));
//        size.setTextFill(Paint.valueOf("#000"));
//        color.setTextFill(Paint.valueOf("#000"));
//        remainingStock.setTextFill(Paint.valueOf("#000"));
//        stockStatus.setTextFill(Paint.valueOf("#000"));
//        label.setTextFill(Paint.valueOf("#000"));
//        itemPriceLabel.setTextFill(Paint.valueOf("#000"));
//        itemSellingPriceLabel.setTextFill(Paint.valueOf("#000"));
//        itemSizeLabel.setTextFill(Paint.valueOf("#000"));
//        itemStockDateLabel.setTextFill(Paint.valueOf("#000"));
//        itemStockNameLabel.setTextFill(Paint.valueOf("#000"));
//        itemColorLabel.setTextFill(Paint.valueOf("#000"));
//        remainingStockCountLabel.setTextFill(Paint.valueOf("#000"));
//        totalItemsInStockLabel.setTextFill(Paint.valueOf("#000"));
    }

    @Override
    public void darkTheme() {
        this.setStyle(this.getStyle() + "-fx-background-color: #202033; ");
        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.DARK_THEME_CSS);

//        itemName.setTextFill(Paint.valueOf("#EEE"));
//        itemPrice.setTextFill(Paint.valueOf("#EEE"));
//        itemStock.setTextFill(Paint.valueOf("#EEE"));
//        sellingPrice.setTextFill(Paint.valueOf("#EEE"));
//        orderedPrice.setTextFill(Paint.valueOf("#EEE"));
//        size.setTextFill(Paint.valueOf("#EEE"));
//        color.setTextFill(Paint.valueOf("#EEE"));
//        remainingStock.setTextFill(Paint.valueOf("#EEE"));
//        stockStatus.setTextFill(Paint.valueOf("#EEE"));
//        label.setTextFill(Paint.valueOf("#EEE"));
//        itemPriceLabel.setTextFill(Paint.valueOf("#EEE"));
//        itemSellingPriceLabel.setTextFill(Paint.valueOf("#EEE"));
//        itemSizeLabel.setTextFill(Paint.valueOf("#EEE"));
//        itemStockDateLabel.setTextFill(Paint.valueOf("#EEE"));
//        itemStockNameLabel.setTextFill(Paint.valueOf("#EEE"));
//        itemColorLabel.setTextFill(Paint.valueOf("#EEE"));
//        remainingStockCountLabel.setTextFill(Paint.valueOf("#EEE"));
//        totalItemsInStockLabel.setTextFill(Paint.valueOf("#EEE"));
    }
}
