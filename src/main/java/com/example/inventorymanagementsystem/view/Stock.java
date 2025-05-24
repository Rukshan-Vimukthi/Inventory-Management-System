package com.example.inventorymanagementsystem.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;

public class Stock {
    private VBox mainLayout;

    public Stock() {
        mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 20, 0, 10));

        VBox navbar = new VBox();
        navbar.setMaxWidth(Double.MAX_VALUE);
        navbar.setPadding(new Insets(20));
        navbar.setSpacing(5.5);
        navbar.setStyle("-fx-background-color: lightGray;");
        navbar.setAlignment(Pos.TOP_CENTER);

        Text heading = new Text("Stock Management");
        heading.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        heading.setTextAlignment(TextAlignment.CENTER);

        HBox headerContainer = new HBox();
        headerContainer.setPadding(new Insets(20, 0, 0, 0));
        headerContainer.setSpacing(10);
        headerContainer.setAlignment(Pos.CENTER);

        HBox searchContainer = new HBox();
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search Item by Name");
        Button searchBtn = new Button("Search");
        searchContainer.getChildren().addAll(searchBar, searchBtn);

        Text filterTxt = new Text("Filters:");
        filterTxt.setStyle("-fx-font-size: 17px;");

        ComboBox<String> category = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                            "T-Shirts", "Shirts", "Jeans", "Trousers", "Jackets", "Coats",
                            "Dresses", "Skirts", "Shorts", "Sweaters", "Hoodies", "Activewear",
                            "Underwear", "Sleepwear", "Swimwear", "Accessories", "Shoes")
                )
        );
        category.setPromptText("Select Category");

        ComboBox<String> brand = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                            "Nike", "Adidas", "Zara", "H&M", "Uniqlo", "Levi's", "Gucci", "Louis Vuitton",
                            "Prada", "Balenciaga", "Puma", "Reebok", "Tommy Hilfiger", "Calvin Klein", "Under Armour"
                        )
                )
        );
        brand.setPromptText("Select Brand");

        ComboBox<String> availability = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                            "In Stock", "Out of Stock", "Pre-Order", "Limited Edition", "Coming Soon"
                        )
                )
        );

        availability.setPromptText("Select Availability");

        Button addStock = new Button("+ Add New Stock");

    //  The lightbox for adding items
        HBox lightBoxContainer = new HBox();
        lightBoxContainer.maxHeight(Double.MAX_VALUE);
        lightBoxContainer.maxWidth(Double.MAX_VALUE);
        lightBoxContainer.setAlignment(Pos.CENTER);

        VBox stockLightBox = new VBox(15);
        stockLightBox.setMaxWidth(800);
        stockLightBox.maxHeight(500);
        stockLightBox.minHeight(400);
        stockLightBox.setSpacing(40);
        Button closeBtn = new Button("Close");

        stockLightBox.setStyle("-fx-background-color: lightGray; -fx-border-width :2px; -fx-border-color: black; -fx-padding: 20");
        stockLightBox.setAlignment(Pos.CENTER);
        stockLightBox.getChildren().addAll(closeBtn);
        stockLightBox.setVisible(false);
        lightBoxContainer.getChildren().addAll(stockLightBox);

        addStock.setOnAction(e -> {
            stockLightBox.setVisible(true);
        });
        closeBtn.setOnAction(e -> {
            stockLightBox.setVisible(false);
        });

        Rectangle overlay = new Rectangle(400, 300, Color.rgb(0, 0, 0, 0.5));
        overlay.setArcWidth(20);
        overlay.setArcHeight(20);

        headerContainer.getChildren().addAll(searchContainer, filterTxt, category, brand, availability);
        navbar.getChildren().addAll(heading, headerContainer, addStock);
        
        mainLayout.getChildren().addAll(navbar, lightBoxContainer);
    }

    public VBox getLayout() {
        return mainLayout;
    }
}
