package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.services.interfaces.ItemPreviewObserver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Class that extends VBox to show the information of the selected item.
 */
public class ItemPreview extends HBox implements ItemPreviewObserver {
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
        this.setStyle("-fx-background-color: #AAA; -fx-border-radius: 10px; -fx-background-radius: 10px;");
        this.setPadding(new Insets(10.0D));
        imageView = new ImageView();
        imageView.setFitWidth(300.0D);
        imageView.setFitHeight(300.0D);
        imageView.setStyle("-fx-background-color: #888888;");
        this.getChildren().add(imageView);
        clearContent();
        this.setMinHeight(300.0D);
        this.setMaxHeight(300.0D);
        this.setPadding(new Insets(10.0D));
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
                orderedPrice = new Label();
                itemPriceLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                orderedPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemSellingPriceLabel = new Label("Selling Price");
                sellingPrice = new Label();
                itemSellingPriceLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                sellingPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemSizeLabel = new Label("Size");
                size = new Label();
                itemSizeLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                size.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemStockDateLabel = new Label("Stock Date");
                itemStock = new Label();
                itemStockDateLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                itemStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemStockNameLabel = new Label("Stock Name");
                itemStockNameLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));

                itemColorLabel = new Label("Color");
                itemColorLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));


                remainingStockCountLabel = new Label("Remaining Items");
                remainingStock = new Label();
                remainingStockCountLabel.setFont(Font.font("Sans Serif", FontWeight.SEMI_BOLD, 16.0D));
                remainingStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                totalItemsInStockLabel = new Label("Total Items Received");
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
                itemName.setFont(Font.font("Sans Serif", FontWeight.BOLD, 20.0D));

                stockStatus = new Label();
                stockStatus.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));
                stockStatus.setStyle("" +
                        "-fx-background-color: #00FF0033; " +
                        "-fx-border-color: #00FF00; " +
                        "-fx-border-radius: 5px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;");


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
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(label);
    }
}
