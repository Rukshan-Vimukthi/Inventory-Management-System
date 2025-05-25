package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Item;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.ItemHasSize;
import com.example.inventorymanagementsystem.services.interfaces.ItemPreviewObserver;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Class that extends VBox to show the information of the selected item.
 */
public class ItemPreview extends VBox implements ItemPreviewObserver {
    VBox informationContainer;

    Label itemName;
    Label itemPrice;
    Label itemStock;
    Label sellingPrice;
    Label remainingStock;
    Label stockStatus;

    /**
     *
     */
    public ItemPreview(){
        clearContent();
        this.setMinHeight(300.0D);
        this.setMaxHeight(300.0D);
        this.setPadding(new Insets(10.0D));
    }

    @Override
    public void update(ItemDetail itemDetail) {
        ItemHasSize itemHasSize = null;
        if(itemDetail != null){
            itemHasSize = Connection.getInstance().getItemHasSize(itemDetail.getId());
            if (informationContainer == null){
                this.getChildren().removeAll(this.getChildren());
                informationContainer = new VBox();

                itemName = new Label();
                itemName.setFont(Font.font("Sans Serif", FontWeight.BOLD, 20.0D));

                itemPrice =  new Label();
                itemPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                itemStock = new Label();
                itemStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                sellingPrice = new Label();
                sellingPrice.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

                remainingStock = new Label();
                remainingStock.setFont(Font.font("Sans Serif", FontWeight.NORMAL, 16.0D));

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

                informationContainer.getChildren().addAll(header, itemStock, itemPrice, sellingPrice, remainingStock);
                this.getChildren().add(informationContainer);
                this.setAlignment(Pos.TOP_LEFT);
            }

            System.out.println(itemDetail.getName());
            System.out.println(itemDetail.getPrice());

            itemName.setText(itemDetail.getName());
            itemPrice.setText(String.valueOf(itemDetail.getPrice()));
            itemStock.setText(itemDetail.getStockDate());
            sellingPrice.setText(String.valueOf(itemDetail.getSellingPrice()));
            remainingStock.setText(itemDetail.getName());
            stockStatus.setText("In Stock");
        }
    }

    public void clearContent(){
        this.getChildren().removeAll(this.getChildren());

        Label label = new Label("No item data to preview");
        label.setFont(Font.font(18.0D));
        label.setTextAlignment(TextAlignment.CENTER);
        this.setAlignment(Pos.CENTER);
        this.getChildren().add(label);
    }
}
