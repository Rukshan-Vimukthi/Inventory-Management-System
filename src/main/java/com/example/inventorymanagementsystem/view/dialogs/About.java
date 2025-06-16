package com.example.inventorymanagementsystem.view.dialogs;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class About extends Stage {
    public About(){
        super();
        this.initStyle(StageStyle.TRANSPARENT);

        BorderPane dialogPane = new BorderPane();
        dialogPane.setMinWidth(500.0D);
        dialogPane.setMaxWidth(500.0D);
        dialogPane.setMinHeight(500.0D);
        dialogPane.setMaxHeight(500.0D);
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label headerText = new Label("About Us");
        headerText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #667788;");
        header.getChildren().add(headerText);

        VBox content = new VBox();

        HBox logoContainer = new HBox();

        Label logo = new Label("NEXTDEV");
        logo.setStyle("-fx-text-weight: bold; -fx-font-size: 18px; -fx-text-fill: #667788;");
        ImageView logoImage = new ImageView(new Image(String.valueOf(InventoryManagementApplication.class.getResource("images/NXTDEV-logo-new.png"))));
        logoImage.setFitWidth(150.0D);
        logoImage.setFitHeight(150.0D);
        logo.setGraphic(logoImage);
        logo.setContentDisplay(ContentDisplay.TOP);
        logo.setAlignment(Pos.CENTER);

        logoContainer.getChildren().add(logo);
        logoContainer.setAlignment(Pos.CENTER);


        Text text = new Text("We are NXTDEV co. we are a software development company." +
                " we build custom desktop and web applications for businesses and personal use. for inquiries " +
                " more information or If you notice any bug or something not working right, please contact us using information below.");
        text.setStyle("-fx-fill: #667788; -fx-font-size: 16px;");

        Text emails = new Text("rukshanse.info@gmail.com | sandunsathyajith1@gmail.com");
        emails.setStyle("-fx-fill: #667788; -fx-font-size: 16px;");

        Text phone = new Text("+94774547632 | +94760556766");
        phone.setStyle("-fx-fill: #667788; -fx-font-size: 16px;");

        content.getChildren().addAll(logoContainer, headerText, text, emails, phone);
        content.setSpacing(10.0D);
        content.setFillWidth(true);

        text.wrappingWidthProperty().bind(dialogPane.widthProperty().subtract(10.0D));

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: #FFF;");
        closeButton.setOnAction(actionEvent -> {
            this.close();
        });

        footer.getChildren().add(closeButton);

        VBox mainContainer = new VBox();
        VBox.setVgrow(content, Priority.ALWAYS);
        mainContainer.getChildren().addAll(content, footer);
        dialogPane.setCenter(mainContainer);
        dialogPane.setPadding(new Insets(20.0D));
        dialogPane.setStyle("" +
                "-fx-background-color: #111122; " +
                "-fx-background-radius: 10px;" +
                "-fx-border: solid;" +
                "-fx-border-color: #002288;" +
                "-fx-border-radius: 10px;");
        Scene scene = new Scene(dialogPane);
        scene.setFill(Paint.valueOf("#00000000"));

        this.setScene(scene);
        this.initModality(Modality.WINDOW_MODAL);
    }
}
