package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Color;
import com.example.inventorymanagementsystem.models.ItemDetail;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.NewItemVariant;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AddNewItem extends Stage {
    private FormField<TextField, String> itemName;
    private FormField<TextField, String> itemQty;
    private FormField<TextField, String> remainingQty;
    private FormField<TextField, String> itemOrderedPrice;
    private FormField<TextField, String> itemSellingPrice;
    private FormField<ColorPicker, String> itemColor;
    private FormField<ComboBox, Size> itemSize;
    private FormField<ComboBox, Stock> itemStock;

    ListView<NewItemVariant> variantContainer;
    Label messageLabel;
    public AddNewItem(ItemDetail itemDetail){
        this.initStyle(StageStyle.TRANSPARENT);
        this.setMaxWidth(750.0D);
        this.setMinWidth(750.0D);
        this.setMaxHeight(500.0D);
        this.setMinHeight(500.0D);

        Label title = new Label("");
        title.setStyle("-fx-font-size: 15px; -fx-text-fill: white;");

        messageLabel = new Label("");
        messageLabel.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-border: solid; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-color: #0055FF; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 20px;");
        messageLabel.setTextFill(Paint.valueOf("#00CCFF"));
        messageLabel.setVisible(false);

        if(itemDetail == null){
            title.setText("Add a new item");
        }else{
            title.setText("Update item");
        }

        VBox mainContainer = new VBox();
        mainContainer.setPadding(new Insets(10.0D));

        Scene scene = new Scene(mainContainer);
        scene.setFill(Paint.valueOf("#00000000"));
        scene.getStylesheets().clear();
        scene.getStylesheets().add(String.valueOf(InventoryManagementApplication.class.getResource("css/style.css")));
        scene.getStylesheets().add(String.valueOf(InventoryManagementApplication.class.getResource("css/darkTheme.css")));

        mainContainer.getStyleClass().add("dialog");
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
        save.getStyleClass().add("primary-button");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println((String) itemName.getValue());
                try {
                    int id = 0;
                    if (itemDetail == null) {
                        id = Connection.getInstance().addSingleItem((String) itemName.getValue());
                    }else{
                        id = itemDetail.getId();
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
                        }else if(itemDetail != null){
                            selectedFilePath = itemDetail.getImagePath();
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
                            System.out.println("Adding new item.");
                            boolean itemAdded = Connection.getInstance().addNewVariant(
                                    id,
                                    stockID,
                                    sizeID,
                                    colorID,
                                    orderedQty,
                                    price,
                                    sellingPrice,
                                    selectedFilePath
                            );

                            if (itemAdded) {
                                messageLabel.setTextFill(Paint.valueOf("#00AA00"));
                                showToast("Item Added successfully!");
                            }else{
                                messageLabel.setTextFill(Paint.valueOf("#FF0000"));
                                showToast("Failed to add item!");
                            }
                        }
                    }

                    Data.getInstance().refreshItemDetails();
                }catch(SQLException e){
                    e.printStackTrace();
                }
//                AddNewItem.this.setResult(isInserted);
            }
        });

//        Button saveAndAddAnother = new Button("Save & Add Another");
        Button close = new Button("Close");
        close.getStyleClass().add("button-danger");
        close.setOnAction(actionEvent -> {
            this.close();
        });

        footer.getChildren().addAll(messageLabel, save, close);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10.0D, 0.0D, 0.0D, 0.0D));

        System.out.println("Item detail: " + itemDetail);

        HBox variantCommandButtonsContainer = new HBox();
        Button addVariant = new Button("Add a new variant");
        addVariant.getStyleClass().add("success-button");
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
        mainContainer.getChildren().addAll(title, flowPane, variantCommandButtonsContainer, variantContainer, footer);


        if(itemDetail != null) {
            try {
                NewItemVariant newItemVariant = new NewItemVariant(itemDetail);
                variantContainer.getItems().add(newItemVariant);
                System.out.println("Added item variant");
            }catch(SQLException e){
                e.printStackTrace();
            }
        }

        this.setScene(scene);
    }

    public void showToast(String message){
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), messageLabel);
                        fadeOut.setFromValue(1.0D);
                        fadeOut.setToValue(0.0D);
                        fadeOut.play();
                    }
                });
            }
        });

        thread.start();
    }
}
