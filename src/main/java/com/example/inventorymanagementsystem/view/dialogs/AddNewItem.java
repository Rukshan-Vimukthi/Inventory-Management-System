package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.NewItemVariant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AddNewItem extends Dialog<Boolean> {
    private FormField<TextField, String> itemName;
    private FormField<TextField, String> itemQty;
    private FormField<TextField, String> remainingQty;
    private FormField<TextField, String> itemOrderedPrice;
    private FormField<TextField, String> itemSellingPrice;
    private FormField<ColorPicker, String> itemColor;
    private FormField<ComboBox, Size> itemSize;
    private FormField<ComboBox, Stock> itemStock;

    ListView<NewItemVariant> variantContainer;
    public AddNewItem(ItemDetail itemDetail){
        DialogPane dialogPane = new DialogPane();
        dialogPane.setMaxWidth(750.0D);
        dialogPane.setMinWidth(750.0D);
        dialogPane.setMaxHeight(500.0D);
        dialogPane.setMinHeight(500.0D);

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
        itemName.setEnabled(itemDetail == null);

        itemName.setMinWidth(400.0D);

        if (itemDetail != null){
            itemName.setValue(itemDetail.getName());
        }

        flowPane.getChildren().addAll(
                itemName
        );

        HBox footer = new HBox();
        footer.setSpacing(10.0D);
        Button save = new Button("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println((String) itemName.getValue());
                try {
                    int id = 0;
                    if (itemDetail == null) {
                        id = Connection.getInstance().addSingleItem((String) itemName.getValue());
                    }

                    for (NewItemVariant itemVariant : variantContainer.getItems()) {
                        int stockID = itemVariant.getStockID();
                        int sizeID = itemVariant.getSizeID();
                        int colorID = itemVariant.getColorID();
                        double price = itemVariant.getPrice();
                        double sellingPrice = itemVariant.getSellingPrice();
                        int orderedQty = itemVariant.getOrderedQty();
                        File selectedFile = itemVariant.getSelectedImage();

                        String selectedFilePath = null;
                        if (selectedFile != null) {
                            Path destinationPath = Paths.get(Constants.itemsMediaDirectory);
                            try {
                                Files.createDirectories(destinationPath);
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }

                            Path destinationFilePath = destinationPath.resolve(selectedFile.getName());
                            Path sourcePath = selectedFile.toPath();

                            try {
                                Files.copy(sourcePath, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
                                selectedFilePath = destinationFilePath.toAbsolutePath().toString().replace('\\', '/');
                                System.out.println(selectedFilePath);
                            } catch (IOException exception) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setContentText("Could not copy the image to the destination location");
                                alert.show();
                            }
                        }

                        if (itemVariant.isUpdate()) {
                            System.out.println("Item Should Be Updated!");

                            Connection.getInstance().updateItem(
                                    itemVariant.getItemId(),
                                    itemVariant.getItemHasSizeID(),
                                    itemVariant.getItemHasSizeHasStockID(),
                                    itemVariant.getColorHasItemHasSizeID(),
                                    (String) itemName.getValue(),
                                    orderedQty,
                                    orderedQty,
                                    price,
                                    sellingPrice, stockID,
                                    sizeID,
                                    colorID,
                                    selectedFilePath
                            );
                        } else {

                            Connection.getInstance().addNewVariant(
                                    id,
                                    stockID,
                                    sizeID,
                                    colorID,
                                    orderedQty,
                                    price,
                                    sellingPrice,
                                    selectedFilePath
                            );
                        }
                    }

//                String name = (String) itemName.getValue();
//                Integer qty = Integer.parseInt((String)itemQty.getValue());
//                Integer remainingQtyValue = Integer.parseInt((String)remainingQty.getValue());
//                Double price = Double.parseDouble((String)itemOrderedPrice.getValue());
//                Double sellImaingPrice = Double.parseDouble((String)itemSellingPrice.getValue());
//                Stock stock = (Stock) itemStock.getValue();
//                Size size = (Size) itemSize.getValue();
//                String colorCode = "#" + ((String)itemColor.getValue()).split("0x")[1];
//
//                Color color = Connection.getInstance().getColorByCode(colorCode);
//                int newColorID = -1;
//                if (color == null){
//                    newColorID = Connection.getInstance().addNewColor(colorCode);
//                }else{
//                    newColorID = color.getId();
//                }
//
//                String sql = "INSERT INTO `item` " +
//                        "(`name`, `stock_id`) " +
//                        "VALUES('%s', %d)".formatted(itemName.getValue(), ((Stock)itemStock.getValue()).getId());
//
//
//                if (itemDetail != null){
//                    int itemID = itemDetail.getId();
//                    int itemHasSizeID = itemDetail.getItemHasSizeID();
//                    int colorHasItemHasSizeID = itemDetail.getColorHasItemHasSize();
//
//                    boolean isUpdated = Connection.getInstance().updateItem(
//                            itemID,
//                            itemHasSizeID,
//                            colorHasItemHasSizeID,
//                            name,
//                            qty,
//                            remainingQtyValue,
//                            price, sellingPrice,
//                            stock.getId(),
//                            size.getId(),
//                            newColorID
//                    );
//                }else{
//                    boolean isInserted = Connection.getInstance().addNewItem(
//                        name,
//                        qty,
//                        price,
//                        sellingPrice,
//                        stock.getId(),
//                        size.getId(),
//                        Connection.getInstance().getColorByCode(colorCode).getId()
//                    );
//                    System.out.println("New item is added!");
//                }
                    Data.getInstance().refreshItemDetails();
                }catch(SQLException e){
                    e.printStackTrace();
                }
//                AddNewItem.this.setResult(isInserted);
            }
        });

//        Button saveAndAddAnother = new Button("Save & Add Another");
        Button close = new Button("Close");
        close.setOnAction(actionEvent -> {
            this.setResult(false);
        });
        footer.getChildren().addAll(save, close);
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));

        System.out.println("Item detail: " + itemDetail);

        HBox variantCommandButtonsContainer = new HBox();
        Button addVariant = new Button("Add a new variant");
        addVariant.setOnAction(event -> {
            NewItemVariant newItemVariant = null;
            try {
                newItemVariant = new NewItemVariant(null);
                variantContainer.getItems().add(newItemVariant);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        variantCommandButtonsContainer.getChildren().add(addVariant);
        variantCommandButtonsContainer.setAlignment(Pos.CENTER_RIGHT);

        variantContainer = new ListView<>();
        variantContainer.setMaxHeight(350.0D);
        variantContainer.setMinHeight(350.0D);

        mainContainer.setSpacing(10.0D);
        mainContainer.getChildren().addAll(flowPane, variantCommandButtonsContainer, variantContainer, footer);

        dialogPane.setContent(mainContainer);

        if(itemDetail != null) {
            try {
                NewItemVariant newItemVariant = new NewItemVariant(itemDetail);
                variantContainer.getItems().add(newItemVariant);
                System.out.println("Added item variant");
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

        this.setDialogPane(dialogPane);
    }
}
