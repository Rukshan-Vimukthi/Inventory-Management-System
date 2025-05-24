package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.db.Connection;
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
    public AddNewItem(ItemDetail itemDetail){
        DialogPane dialogPane = new DialogPane();
        dialogPane.setMaxWidth(330.0D);
        dialogPane.setMinWidth(330.0D);
        dialogPane.setMaxHeight(280.0D);
        dialogPane.setMinHeight(280.0D);

        VBox mainContainer = new VBox();

        FlowPane flowPane = new FlowPane();
        flowPane.setVgap(10.0D);
        flowPane.setHgap(10.0D);

        FormField<TextField, String> itemName = new FormField<>("Item Name", TextField.class);
        FormField<TextField, String> itemQty = new FormField<>("Item Qty", TextField.class);
        FormField<TextField, String> itemOrderedPrice = new FormField<>("Bought Price", TextField.class);
        FormField<TextField, String> itemSellingPrice = new FormField<>("Selling Price", TextField.class);
        FormField<ColorPicker, String> itemColor = new FormField<>("Color", ColorPicker.class);
        FormField<ComboBox, Size> itemSize = new FormField<>("Size", ComboBox.class, Data.getInstance().getSize());
        FormField<ComboBox, Stock> itemStock = new FormField<>("Stock", ComboBox.class, Data.getInstance().getStocks());

        flowPane.getChildren().addAll(
                itemName,
                itemQty,
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

//                boolean isInserted = Connection.getInstance().addNewItem(
//                        (String) itemName.getValue(),
//                        (Integer) itemQty.getValue(),
//                        (Double) itemOrderedPrice.getValue(),
//                        (Double) itemSellingPrice.getValue(),
//                        (Integer) itemStock.getValue(),
//                        (Integer) itemSize.getValue(),
//                        (Integer) itemColor.getValue()
//                );

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
