package com.example.inventorymanagementsystem.components;

import com.example.inventorymanagementsystem.constants.Fonts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TabBuilder {
    public static Tab buildTab(String text){
        HBox hbox = new HBox();
        hbox.setFillHeight(true);

        Label tabTitle = new Label(text);
        tabTitle.setRotate(90.0);
        tabTitle.setPrefWidth(200.0D);
        tabTitle.setTextFill(Paint.valueOf("#000"));
        tabTitle.setFont(Fonts.medium);
//        tabTitle.setStyle("-fx-background-color: #888;");
        hbox.getChildren().add(tabTitle);

        hbox.setMinWidth(200.0D);
        hbox.setMinHeight(50.0D);
        hbox.setRotate(90.0D);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(20.0D, 0.0D, 0.0D, 0.0D));
//        hbox.setStyle("-fx-background-color: #555");
        HBox.setHgrow(tabTitle, Priority.ALWAYS);

        Tab tab = new Tab("");
//        tab.setContent(hbox);
        tab.setGraphic(hbox);
        tab.setClosable(false);
        return tab;
    }
}
