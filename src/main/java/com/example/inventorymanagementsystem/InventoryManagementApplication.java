package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.AuthenticateStateListener;
import com.example.inventorymanagementsystem.state.Constants;

import com.example.inventorymanagementsystem.state.Session;
import com.example.inventorymanagementsystem.state.ThemeObserver;
import com.example.inventorymanagementsystem.view.*;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import com.example.inventorymanagementsystem.view.dialogs.SignIn;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class InventoryManagementApplication extends Application implements com.example.inventorymanagementsystem.services.interfaces.ThemeObserver, AuthenticateStateListener {

    Scene scene;
    Tab checkoutTab;
    Tab analyticsTab;
    Tab inventory;
    Tab users;

    TabPane tabPane;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(InventoryManagementApplication.class.getResource("ApplicationUI.fxml"));
        VBox rootContainer = new VBox();

        TitleBar titleBar = new TitleBar(stage);

        tabPane = new TabPane();

        tabPane.setStyle("-fx-background-color: #222;");

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(Side.LEFT);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setRotateGraphic(true);
        tabPane.setTabMinWidth(30.0D);
        tabPane.setTabMaxWidth(30.0D);
        tabPane.setTabMinHeight(200.0D);
        tabPane.setTabMaxHeight(200.0D);
        tabPane.setFocusTraversable(false);

        FontIcon checkoutIcon = new FontIcon(FontAwesomeSolid.SHOPPING_CART);
        checkoutIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon analyticsIcon = new FontIcon(FontAwesomeSolid.CHART_LINE);
        analyticsIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon inventoryIcon = new FontIcon(FontAwesomeSolid.BOXES);
        inventoryIcon.setFill(Paint.valueOf("#FFF"));
        FontIcon userIcon = new FontIcon(FontAwesomeSolid.USERS);
        userIcon.setFill(Paint.valueOf("#FFF"));

        // tabs in the main UI
        checkoutTab = TabBuilder.buildTab("Checkout", checkoutIcon);
        analyticsTab = TabBuilder.buildTab("Analytics", analyticsIcon);
        inventory = TabBuilder.buildTab("Inventory", inventoryIcon);
        users = TabBuilder.buildTab("Users", userIcon);

        // create the inventory view (custom javaFX layout container ex. HBox, VBox)
        Inventory inventoryView = new Inventory();
        // set the custom view as the content of the tab created for inventory (inventory)
        inventory.setContent(inventoryView);

        // The checkout Section
        Checkout checkoutLayout = new Checkout();
        BorderPane checkoutContainer = checkoutLayout.getLayout();
        checkoutTab.setContent(checkoutContainer);

        InventoryManagementApplicationController.NavigationHandler handler = new InventoryManagementApplicationController.NavigationHandler() {
            @Override
            public void goToInventory() {
                navigate("Inventory");
            }

            public void navigate(String destination) {
                if (destination.equals("Inventory")) {
                    tabPane.getSelectionModel().select(inventory);
                }
            }
        };

        // The Stock Section
        Analytics analyticsView = new Analytics(handler);
        VBox analyticsViewContainer = analyticsView.getLayout();

        ScrollPane scrollableAnalytics = new ScrollPane(analyticsViewContainer);
        scrollableAnalytics.setFitToWidth(true);
        scrollableAnalytics.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        analyticsTab.setContent(scrollableAnalytics);

        Users userTabView = new Users();
        users.setContent(userTabView);

        ThemeObserver.init().addObserver(inventoryView);
        ThemeObserver.init().addObserver(analyticsView);
        ThemeObserver.init().addObserver(checkoutLayout);

        ThemeObserver.init().applyDarkThemeChange();

        // Add tabs to the tabPane
        tabPane.getTabs().addAll(
                checkoutTab,
                analyticsTab,
                inventory,
                users
        );

        rootContainer.getChildren().addAll(titleBar, tabPane);

        scene = new Scene(rootContainer);

        scene.getStylesheets().add(
            String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        // remove the frame of the window
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        if (!Session.isLoggedIn()){
            SignIn signIn = new SignIn(stage);
            signIn.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void lightTheme() {
        scene.getStylesheets().remove(Constants.DARK_THEME_CSS);
        scene.getStylesheets().add(Constants.LIGHT_THEME_CSS);
        tabPane.setStyle("-fx-background-color: blue;");
    }

    @Override
    public void darkTheme() {
        scene.getStylesheets().remove(Constants.LIGHT_THEME_CSS);
        scene.getStylesheets().add(Constants.DARK_THEME_CSS);
        tabPane.setStyle("-fx-background-color: black;");
    }

    @Override
    public void onLoggedIn() {
        User user = Session.getInstance().getSessionUser();
        if (user.getRole().equals("Admin")){
            checkoutTab.setDisable(false);
            analyticsTab.setDisable(false);
            inventory.setDisable(false);
            users.setDisable(false);
        }else{
            checkoutTab.setDisable(false);
            analyticsTab.setDisable(true);
            inventory.setDisable(true);
            users.setDisable(true);
        }
    }

    @Override
    public void onLoggedOut() {
        checkoutTab.setDisable(false);
        analyticsTab.setDisable(false);
        inventory.setDisable(false);
        users.setDisable(false);
    }

}