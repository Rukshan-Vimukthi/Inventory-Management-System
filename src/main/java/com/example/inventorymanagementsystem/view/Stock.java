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
        headerContainer.setSpacing(30);
        headerContainer.setAlignment(Pos.CENTER);

        HBox filterContainer = new HBox();
        filterContainer.setSpacing(20);
        HBox searchContainer = new HBox();
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search Item by Name");
        Button searchBtn = new Button("🔎 Search");
        searchContainer.getChildren().addAll(searchBar, searchBtn);

        Text filterTxt = new Text("FILTER:");
        filterTxt.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");

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

        HBox sortContainer = new HBox();
        sortContainer.setSpacing(20);

        Text sortTxt = new Text("SORT");
        sortTxt.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        sortTxt.setFill(Color.web("#FFA85B"));

        ComboBox<String> quantitySort = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Ascending", "Descending")
                )
        );
        quantitySort.setPromptText("Quantity");

        ComboBox<String> nameSort = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "A → Z", "Z → A"
                        )
                )
        );
        nameSort.setPromptText("Name");

        ComboBox<String> dateSort = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Newest First", "Oldest First"
                        )
                )
        );
        dateSort.setPromptText("Date");

        ComboBox<String> priceSort = new ComboBox<>(
                FXCollections.observableArrayList(
                        Arrays.asList(
                                "Low → High", "High → Low"
                        )
                )
        );
        priceSort.setPromptText("Price");

        HBox sortFilterSEc = new HBox();
        sortFilterSEc.setAlignment(Pos.TOP_CENTER);
        sortFilterSEc.setMaxWidth(Double.MAX_VALUE);
        sortFilterSEc.getChildren().addAll(filterContainer, sortContainer);
        sortFilterSEc.setSpacing(20);
        sortFilterSEc.setPadding(new Insets(20, 0, 0, 0));

        filterContainer.getChildren().addAll(filterTxt, category, brand, availability);
        sortContainer.getChildren().addAll(sortTxt, quantitySort, nameSort, dateSort, priceSort);

        headerContainer.getChildren().addAll(searchContainer);
        navbar.getChildren().addAll(heading, sortFilterSEc, headerContainer);
        
        mainLayout.getChildren().addAll(navbar);
    }
    public VBox getLayout() {
        return mainLayout;
    }
}
