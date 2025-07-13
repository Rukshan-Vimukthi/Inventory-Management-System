package com.example.inventorymanagementsystem;

import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.AuthenticateStateListener;
import com.example.inventorymanagementsystem.services.utils.Logger;
import com.example.inventorymanagementsystem.state.Constants;

import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.state.Session;
import com.example.inventorymanagementsystem.state.ThemeObserver;
import com.example.inventorymanagementsystem.view.*;
import com.example.inventorymanagementsystem.view.components.TabBuilder;
import com.example.inventorymanagementsystem.view.dialogs.SignIn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class InventoryManagementApplication extends Application implements com.example.inventorymanagementsystem.services.interfaces.ThemeObserver, AuthenticateStateListener {

    Stage stage;
    Scene scene;
    VBox rootContainer;
    Tab checkoutTab;
    Tab analyticsTab;
    Tab inventory;
    Tab users;
    Tab liabilitiesTab;

    Tab salesTab;
    Tab settings;
    TitleBar titleBar;
    TabPane tabPane;

    Checkout checkoutLayout;
    Inventory inventoryView;
    Analytics analyticsView;
    Users userTabView;
    Liabilities liabilities;

    Sales sales;
    Settings settingsView;

    BorderPane borderPane;
    VBox messageContainer;
    Label message;
    Label icon;
    HBox buttonContainer;
    ProgressIndicator progressIndicator;
    ImageView spinner;

    Button retry;

    SignIn signIn;
    @Override
    public void start(Stage stage) {
        com.example.inventorymanagementsystem.services.utils.Settings settings = com.example.inventorymanagementsystem.services.utils.Settings.getInstance();

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Logger.logError("Unhandled exception in thread: " + thread.getName(), throwable);
        });

        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            Logger.logError("FX Application Thread exception: " + thread.getName(), throwable);
        });

        Thread fxThread = Thread.currentThread();
        Platform.runLater(() -> {
            fxThread.setUncaughtExceptionHandler((t, e) -> {
                Logger.logError("Exception in FX Platform.runLater()", e);
            });
        });


        System.out.println("Initializing application...");
        Logger.logMessage("Starting application...");
        this.stage = stage;
        rootContainer = new VBox();
        Session.addListener(this);

        borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #112;");

        messageContainer = new VBox();
        messageContainer.setMaxWidth(400.0D);
        messageContainer.setMinWidth(400.0D);
        messageContainer.setMaxHeight(300.0D);
        messageContainer.setMinHeight(300.0D);
        messageContainer.setStyle("-fx-background-color: #335; -fx-background-radius: 10px;");
        message = new Label("Connecting to the database failed!");
        message.setStyle("-fx-text-fill: #FFAA00; -fx-font-size: 18px;");

        FontIcon fontIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        fontIcon.setFill(Paint.valueOf("#FFAA00"));
        icon = new Label("", fontIcon);
        icon.setStyle("-fx-font-size: 82px;");

        buttonContainer = new HBox();
        retry = new Button("Retry");
        retry.getStyleClass().add("primary-button");
        retry.setStyle("-fx-font-size: 24px;");
        retry.setPadding(new Insets(15.0D, 10.0D, 15.0D, 10.0D));

        Button closeApplication = new Button("Close");
        closeApplication.getStyleClass().add("button-danger");
        closeApplication.setStyle("-fx-font-size: 24px;");
        closeApplication.setPadding(new Insets(15.0D, 10.0D, 15.0D, 10.0D));
        closeApplication.setOnAction(actionEvent -> stage.close());

        buttonContainer.getChildren().addAll(retry, closeApplication);
        buttonContainer.setSpacing(10.0D);
        buttonContainer.setAlignment(Pos.CENTER);

        retry.setOnAction(actionEvent -> {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            spinner.setFitWidth(40.0D);
                            spinner.setFitHeight(40.0D);
                            retry.setGraphic(spinner);
                        }
                    });
                    reconnectAndInitComponent();
                }
            });
            thread.start();
        });

        progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(50.0D);

        Image image = null;
        try {
            image = new Image(String.valueOf(InventoryManagementApplication.class.getResource("images/spinner.gif").toURI()));
            spinner = new ImageView(image);
            spinner.setFitWidth(180.0D);
            spinner.setFitHeight(180.0D);
        }catch(URISyntaxException e){
            Logger.logError(e.getMessage(), e);
            e.printStackTrace();
        }

//        messageContainer.getChildren().addAll(icon, message, buttonContainer);
        messageContainer.setSpacing(20.0D);
        borderPane.setCenter(messageContainer);
        messageContainer.setFillWidth(true);
        messageContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(message, Priority.ALWAYS);
        if (spinner != null) {
            messageContainer.getChildren().add(spinner);
        }

        titleBar = new TitleBar(stage);

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

        scene = new Scene(borderPane);

        scene.getStylesheets().add(
                String.valueOf(InventoryManagementApplication.class.getResource("css/style.css"))
        );

        // remove the frame of the window
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.setMaximized(true);
        stage.setWidth(1906);
        stage.setHeight(1100);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

        /**
         /* try to create the instance of the Data object. under the hood, it creates the connection instance to
         /* load data. if initializing database connection fails, this throws a SQLException
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Data.getInstance();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            authenticate();
                        }
                    });
                }catch(SQLException exception){
                    exception.printStackTrace();
                    Logger.logError(exception.getMessage(), exception);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            messageContainer.getChildren().removeAll(messageContainer.getChildren());
                            messageContainer.getChildren().addAll(icon, message, buttonContainer);
                            scene.setRoot(borderPane);
                        }
                    });
                }
            }
        });
        thread.start();

        ThemeObserver.init().addObserver(this);

    }

    public void switchTabs(){
        for (Tab tab : tabPane.getTabs()){
            if (tab.isSelected()) {
                User loggedInUser = Session.getInstance().getSessionUser();
                if (loggedInUser != null) {
                    if (!loggedInUser.getRole().equals("Admin")){
                        if (tab == analyticsTab || tab == inventory || tab == users) {
                            tabPane.getSelectionModel().select(checkoutTab);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
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
        System.out.println("Logged In!");
        System.out.println(user);
        if (user != null) {
            if (!user.getRole().equals("Admin")) {
                tabPane.getSelectionModel().select(checkoutTab);
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageContainer.getChildren().removeAll(messageContainer.getChildren());
                    try {
                        initComponents();
                    }catch(SQLException exception){
                        Logger.logError(exception.getMessage(), exception);
                        exception.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onLoggedOut() {
        tabPane.getSelectionModel().select(checkoutTab);
        authenticate();
    }


    public void reconnectAndInitComponent(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                retry.setGraphic(spinner);
            }
        });
        try{
            /**
             /* try to create the instance of the Data object. under the hood, it creates the connection instance to
             /* load data. if initializing database connection fails, this throws a SQLException
             */

            Data.getInstance();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    retry.setGraphic(null);
                }
            });
            authenticate();
//            initComponents();
        }catch (SQLException e){
            Logger.logError(e.getMessage(), e);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageContainer.getChildren().removeAll(messageContainer.getChildren());
                    messageContainer.getChildren().addAll(icon, message, buttonContainer);
                    scene.setRoot(borderPane);
                    retry.setGraphic(null);
                }
            });
        }
    }

    private void initComponents() throws SQLException{
        if (rootContainer.getChildren().size() == 0) {
            FontIcon checkoutIcon = new FontIcon(FontAwesomeSolid.SHOPPING_CART);
            checkoutIcon.setFill(Paint.valueOf("#FFF"));
            FontIcon analyticsIcon = new FontIcon(FontAwesomeSolid.CHART_LINE);
            analyticsIcon.setFill(Paint.valueOf("#FFF"));
            FontIcon inventoryIcon = new FontIcon(FontAwesomeSolid.BOXES);
            inventoryIcon.setFill(Paint.valueOf("#FFF"));
            FontIcon userIcon = new FontIcon(FontAwesomeSolid.USERS);
            userIcon.setFill(Paint.valueOf("#FFF"));
            FontIcon liabilitiesIcon = new FontIcon(FontAwesomeSolid.MONEY_BILL_WAVE);
            liabilitiesIcon.setFill(Paint.valueOf("#FFF"));

            FontIcon saleIcon = new FontIcon(FontAwesomeSolid.MONEY_CHECK);
            saleIcon.setFill(Paint.valueOf("#FFF"));

            FontIcon userLiabilitiesIcon = new FontIcon(FontAwesomeSolid.USER_MINUS);
            userLiabilitiesIcon.setFill(Paint.valueOf("#FFF"));

            FontIcon settingsTabIcon = new FontIcon(FontAwesomeSolid.COG);
            settingsTabIcon.setFill(Paint.valueOf("#FFF"));

            // tabs in the main UI
            checkoutTab = TabBuilder.buildTab("Checkout", checkoutIcon);
            analyticsTab = TabBuilder.buildTab("Analytics", analyticsIcon);
            inventory = TabBuilder.buildTab("Inventory", inventoryIcon);
            users = TabBuilder.buildTab("Users", userIcon);
            liabilitiesTab = TabBuilder.buildTab("Liabilities", liabilitiesIcon);
            salesTab = TabBuilder.buildTab("Sales", saleIcon);
            settings = TabBuilder.buildTab("Settings", settingsTabIcon);

            if (inventoryView == null) {
                // create the inventory view (custom javaFX layout container ex. HBox, VBox)
                inventoryView = new Inventory();
                // set the custom view as the content of the tab created for inventory (inventory)
                inventory.setContent(inventoryView);
            }


            if (checkoutLayout == null) {
                // The checkout Section
                checkoutLayout = new Checkout();

                BorderPane checkoutContainer = checkoutLayout.getLayout();
                checkoutTab.setContent(checkoutContainer);
            }

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

            if (analyticsView == null) {
                // The Stock Section
                analyticsView = new Analytics(handler);
                VBox analyticsViewContainer = analyticsView.getLayout();

                ScrollPane scrollableAnalytics = new ScrollPane(analyticsViewContainer);
                scrollableAnalytics.setFitToWidth(true);
                scrollableAnalytics.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                analyticsTab.setContent(scrollableAnalytics);
            }

            if (userTabView == null) {
                userTabView = new Users();
                users.setContent(userTabView);
            }

            if (liabilities == null) {
                liabilities = new Liabilities();
                liabilitiesTab.setContent(liabilities);
            }

            if (sales == null){
                sales = new Sales();
                salesTab.setContent(sales);
            }

            if (settingsView == null){
                settingsView = new Settings();
                settings.setContent(settingsView);
            }

            ThemeObserver.init().addObserver(inventoryView);
            ThemeObserver.init().addObserver(analyticsView);
            ThemeObserver.init().addObserver(checkoutLayout);
            ThemeObserver.init().addObserver(sales);
            ThemeObserver.init().addObserver(settingsView);

            ThemeObserver.init().applyDarkThemeChange();

            // Add tabs to the tabPane
            tabPane.getTabs().addAll(
                    checkoutTab,
                    analyticsTab,
                    inventory,
                    users,
                    liabilitiesTab,
                    salesTab
//                    settings
            );

            rootContainer.getChildren().addAll(titleBar, tabPane);

            for (Tab tab : tabPane.getTabs()) {
                tab.setOnSelectionChanged(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        switchTabs();
                    }
                });
            }
        }

        scene.setRoot(rootContainer);

        System.out.println("Application Initialized!");
    }



    public void authenticate(){
        if (!Session.isLoggedIn()){
            scene.setRoot(borderPane);
            signIn = new SignIn(stage);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    messageContainer.getChildren().removeAll(messageContainer.getChildren());
                    messageContainer.getChildren().add(signIn);
                }
            });
        }
    }


}