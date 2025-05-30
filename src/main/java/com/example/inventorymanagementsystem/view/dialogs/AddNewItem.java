package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AddNewItem extends Dialog<Boolean> {
    private FormField<TextField, String> itemName;
    private FormField<TextField, String> itemQty;
    private FormField<TextField, String> remainingQty;
    private FormField<TextField, String> itemOrderedPrice;
    private FormField<TextField, String> itemSellingPrice;
    private FormField<ColorPicker, String> itemColor;
    private FormField<ComboBox, Size> itemSize;
    private FormField<ComboBox, Stock> itemStock;
    public AddNewItem(ItemDetail itemDetail){
        DialogPane dialogPane = new DialogPane();
        dialogPane.setMaxWidth(330.0D);
        dialogPane.setMinWidth(330.0D);
        dialogPane.setMaxHeight(280.0D);
        dialogPane.setMinHeight(280.0D);

        if(itemDetail == null){
            this.setTitle("Add a new item");
        }else{
            this.setTitle("Update item");
        }

        VBox mainContainer = new VBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setVgap(10.0D);
        flowPane.setHgap(10.0D);

        itemName = new FormField<>("Item Name", TextField.class);
        itemQty = new FormField<>("Item Qty", TextField.class);
        remainingQty = new FormField<>("Remaining Qty", TextField.class);
        remainingQty.setDisable(itemDetail == null);
        itemOrderedPrice = new FormField<>("Bought Price", TextField.class);
        itemSellingPrice = new FormField<>("Selling Price", TextField.class);
        itemColor = new FormField<>("Color", ColorPicker.class);
        if (itemDetail == null) {
            itemSize = new FormField<>("Size", ComboBox.class, Data.getInstance().getSize());
            itemStock = new FormField<>("Stock", ComboBox.class, Data.getInstance().getStocks());
        }else{
            itemSize = new FormField<>(
                    "Size",
                    ComboBox.class,
                    Data.getInstance().getSize(),
                    Connection.getInstance().getSize(itemDetail.getSizeID()));

            itemStock = new FormField<>(
                    "Stock",
                    ComboBox.class,
                    Data.getInstance().getStocks(),
                    Connection.getInstance().getStock(itemDetail.getSizeID()));

            itemName.setValue(itemDetail.getName());
            itemQty.setValue(String.valueOf(itemDetail.getOrderedQty()));
            remainingQty.setValue(String.valueOf(itemDetail.getRemainingQty()));
            itemOrderedPrice.setValue(String.valueOf(itemDetail.getPrice()));
            itemSellingPrice.setValue(String.valueOf(itemDetail.getSellingPrice()));
            itemColor.setValue(itemDetail.getItemColor());
        }

        flowPane.getChildren().addAll(
                itemName,
                itemQty,
                remainingQty,
                itemOrderedPrice,
                itemSellingPrice,
                itemColor,
                itemSize,
                itemStock
        );

        HBox footer = new HBox();
        footer.setSpacing(10.0D);
        Button save = new Button("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println((String) itemName.getValue());
                System.out.println(itemQty.getValue());
                System.out.println(itemOrderedPrice.getValue());
                System.out.println(itemSellingPrice.getValue());
                System.out.println(itemStock.getValue());
                System.out.println(itemSize.getValue());
                System.out.println("#" + ((String)itemColor.getValue()).split("0x")[1]);

                String name = (String) itemName.getValue();
                Integer qty = Integer.parseInt((String)itemQty.getValue());
                Integer remainingQtyValue = Integer.parseInt((String)remainingQty.getValue());
                Double price = Double.parseDouble((String)itemOrderedPrice.getValue());
                Double sellingPrice = Double.parseDouble((String)itemSellingPrice.getValue());
                Stock stock = (Stock) itemStock.getValue();
                Size size = (Size) itemSize.getValue();
                String colorCode = "#" + ((String)itemColor.getValue()).split("0x")[1];

                Color color = Connection.getInstance().getColorByCode(colorCode);
                int newColorID = -1;
                if (color == null){
                    newColorID = Connection.getInstance().addNewColor(colorCode);
                }else{
                    newColorID = color.getId();
                }

                String sql = "INSERT INTO `item` " +
                        "(`name`, `stock_id`) " +
                        "VALUES('%s', %d)".formatted(itemName.getValue(), ((Stock)itemStock.getValue()).getId());

                
                if (itemDetail != null){
                    int itemID = itemDetail.getId();
                    int itemHasSizeID = itemDetail.getItemHasSizeID();
                    int colorHasItemHasSizeID = itemDetail.getColorHasItemHasSize();

                    boolean isUpdated = Connection.getInstance().updateItem(
                            itemID,
                            itemHasSizeID,
                            colorHasItemHasSizeID,
                            name,
                            qty,
                            remainingQtyValue,
                            price, sellingPrice,
                            stock.getId(),
                            size.getId(),
                            newColorID
                    );
                }else{
                    boolean isInserted = Connection.getInstance().addNewItem(
                        name,
                        qty,
                        price,
                        sellingPrice,
                        stock.getId(),
                        size.getId(),
                        Connection.getInstance().getColorByCode(colorCode).getId()
                    );
                    System.out.println("New item is added!");
                }
                Data.getInstance().refreshItemDetails();

//                AddNewItem.this.setResult(isInserted);
            }
        });

        Button saveAndAddAnother = new Button("Save & Add Another");
        Button close = new Button("Close");
        close.setOnAction(actionEvent -> {
            this.setResult(false);
        });
        footer.getChildren().addAll(save, saveAndAddAnother, close);
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));
        mainContainer.getChildren().addAll(flowPane, footer);

        dialogPane.setContent(mainContainer);

        this.setDialogPane(dialogPane);
    }
}
