package com.example.inventorymanagementsystem.view;


import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.db.Connection;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.models.UserAnalytics;
import com.example.inventorymanagementsystem.services.interfaces.TableContainerInterface;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.Card;
import com.example.inventorymanagementsystem.view.components.FormField;
import com.example.inventorymanagementsystem.view.components.TableContainer;
import com.example.inventorymanagementsystem.view.components.UserPreview;
import com.example.inventorymanagementsystem.view.dialogs.AddUpdateCustomerDialog;
import com.example.inventorymanagementsystem.view.dialogs.AddUpdateUserDialog;
import com.example.inventorymanagementsystem.view.forms.AddUpdateCustomer;
import com.example.inventorymanagementsystem.view.forms.AddUpdateUser;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class Users extends HBox implements ThemeObserver {
    private int totalNumberOfUsers;
    private int totalAdmins;
    private int totalCashiers;

    private int totalCustomers;
    private int filteredNumberOfCustomers;

    private List<Customer> topTenCustomers;

    private UserPreview userPreview;

    private Label customersRegisteredOnATimeFrameValue;

    public Users(){
        totalNumberOfUsers = 0;
        totalAdmins = 0;
        totalCashiers = 0;
        totalCustomers = 0;
        filteredNumberOfCustomers = 0;

        VBox tableAndFormContainer = new VBox();

        HBox tableContainer = new HBox();

        TableContainer<User> userTableContainer = new TableContainer<>(false, null, null);
        userTableContainer.addColumn("id", Integer.class);
        userTableContainer.addColumn("firstName", Integer.class);
        userTableContainer.addColumn("lastName", Integer.class);
        userTableContainer.addColumn("email", Integer.class);
        userTableContainer.addColumn("role", Integer.class);
        userTableContainer.addItems(Data.getInstance().getUsers());
        userTableContainer.setOnActionPerformed(new TableContainerInterface<User>() {
            @Override
            public void addItem() {
                AddUpdateUserDialog addUpdateUserDialog = new AddUpdateUserDialog(null);
                addUpdateUserDialog.show();
            }

            @Override
            public void refresh() {
                Data.getInstance().refreshUsers();
            }

            @Override
            public void update(User user) {
                AddUpdateUserDialog addUpdateUserDialog = new AddUpdateUserDialog(user);
                addUpdateUserDialog.show();
            }

            @Override
            public void delete(User user) {
                Connection.getInstance().deleteUser(user.getId());
            }

            @Override
            public void onSelectItem(User user) {
                userPreview.setUserData(user);
            }

            @Override
            public void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText) {

            }
        });

        TableContainer<Customer> customerTableContainer = new TableContainer<>(false, null, null);
        customerTableContainer.addColumn("id", Integer.class);
        customerTableContainer.addColumn("firstName", Integer.class);
        customerTableContainer.addColumn("lastName", Integer.class);
        customerTableContainer.addColumn("phone", Integer.class);
        customerTableContainer.addColumn("email", Integer.class);
        customerTableContainer.addColumn("registeredDate", Integer.class);
        customerTableContainer.addItems(Data.getInstance().getCustomers());
        customerTableContainer.setOnActionPerformed(new TableContainerInterface<Customer>() {
            @Override
            public void addItem() {
                AddUpdateCustomerDialog addUpdateCustomerDialog = new AddUpdateCustomerDialog(null);
                addUpdateCustomerDialog.show();
            }

            @Override
            public void refresh() {
                Data.getInstance().refreshCustomers();
            }

            @Override
            public void update(Customer customer) {
                AddUpdateCustomerDialog addUpdateCustomerDialog = new AddUpdateCustomerDialog(customer);
                addUpdateCustomerDialog.show();
            }

            @Override
            public void delete(Customer customer) {
                Connection.getInstance().deleteCustomer(customer.getId());
            }

            @Override
            public void onSelectItem(Customer customer) {
                userPreview.setCustomerData(customer);
            }

            @Override
            public void onSearch(List<FormField<? extends Control, ?>> formFields, String searchBoxText) {

            }
        });
        tableContainer.getChildren().addAll(userTableContainer, customerTableContainer);

        userPreview = new UserPreview();
        VBox.setVgrow(userPreview, Priority.ALWAYS);

        tableAndFormContainer.getChildren().addAll(tableContainer, userPreview);

        UserAnalytics userAnalytics = Data.getInstance().getUserAnalytics();

        FlowPane analyticsContainer = new FlowPane();
        analyticsContainer.setStyle("-fx-background-color: transparent;");
        analyticsContainer.setPadding(new Insets(10.0D));
        analyticsContainer.setMinWidth(300.0D);
        HBox.setHgrow(analyticsContainer, Priority.SOMETIMES);

        // Analytics data
        int[] userCount = Connection.getInstance().getUserCounts();

        FontIcon totalUsersIcon = new FontIcon(FontAwesomeSolid.USERS);
        FontIcon adminUsersIcon = new FontIcon(FontAwesomeSolid.USER_SHIELD);
        FontIcon userIcon = new FontIcon(FontAwesomeSolid.USER);
        FontIcon customerIcon = new FontIcon(FontAwesomeSolid.USER_TAG);

        // Total number of users
        Label totalUsersCardHeader = new Label("Total Users", totalUsersIcon);
        totalUsersCardHeader.setGraphicTextGap(10.0D);
        totalUsersCardHeader.getStyleClass().add("card-heading");
        Label totalUserCountLabel = new Label(String.valueOf(userCount[0] + userCount[1] + userCount[2]));
        totalUserCountLabel.getStyleClass().add("card-content");
        Card totalUsersCard = new Card(totalUsersCardHeader, totalUserCountLabel, null);
        totalUsersCard.setPadding(new Insets(5.0D));
        totalUsersCard.setCardWidth(150.0D);
        totalUsersCard.setRoundedCorner(10.0D);
        totalUsersCard.getStyleClass().add("user-analytics-card");

        // Admin users card
        Label adminUsersCardHeader = new Label("Admins", adminUsersIcon);
        adminUsersCardHeader.setGraphicTextGap(10.0D);
        adminUsersCardHeader.getStyleClass().add("card-heading");
        Label adminUserCountLabel = new Label(String.valueOf(userCount[0]));
        adminUserCountLabel.getStyleClass().add("card-content");
        Card adminUsersCard = new Card(adminUsersCardHeader, adminUserCountLabel, null);
        adminUsersCard.setPadding(new Insets(5.0D));
        adminUsersCard.setRoundedCorner(10.0D);

        // Users card
        Label userCardHeader = new Label("Users", userIcon);
        userCardHeader.setGraphicTextGap(10.0D);
        userCardHeader.getStyleClass().add("card-heading");
        Label userCountLabel = new Label(String.valueOf(userCount[1]));
        userCountLabel.getStyleClass().add("card-content");
        Card userCard = new Card(userCardHeader, userCountLabel, null);
        userCard.setCardWidth(100.0D);
        userCard.setPadding(new Insets(5.0D));
        userCard.setRoundedCorner(10.0D);

        // customer card
        Label customerCardHeader = new Label("Customers", customerIcon);
        customerCardHeader.setGraphicTextGap(10.0D);
        customerCardHeader.getStyleClass().add("card-heading");
        Label customerCountLabel = new Label(String.valueOf(userCount[2]));
        customerCountLabel.getStyleClass().add("card-content");
        Card customerCard = new Card(customerCardHeader, customerCountLabel, null);
        customerCard.setPadding(new Insets(5.0D));
        customerCard.setRoundedCorner(10.0D);

        // Customers registered at certain time frame
        FontIcon userTimeIcon = new FontIcon(FontAwesomeSolid.USER_CLOCK);
        HBox customerRegisteredOnTimeHeader = new HBox();
        customerRegisteredOnTimeHeader.setSpacing(10.0D);
        Label customersRegisteredOnATimeFrameLabel = new Label("Customers Registered", userTimeIcon);
        customersRegisteredOnATimeFrameLabel.getStyleClass().add("card-heading");
        ComboBox<String> timeFrame = new ComboBox<>();
        timeFrame.getItems().addAll("Today", "This Week", "Last Week", "This month", "Last Month", "This Year", "Last Year");
        timeFrame.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int resultCount = Connection.getInstance().getCustomersRegisteredOn(newValue).size();
                customersRegisteredOnATimeFrameValue.setText(String.valueOf(resultCount));
//                customersRegisteredOnATimeFrameValue.setText(Connection.getInstance().getCustomersRegisteredOn(newValue));
            }
        });

        customerRegisteredOnTimeHeader.getChildren().addAll(customersRegisteredOnATimeFrameLabel, timeFrame);
        customersRegisteredOnATimeFrameValue = new Label("");
        customersRegisteredOnATimeFrameValue.getStyleClass().add("card-content");
        Card customerRegisteredOnATimeCard = new Card(customerRegisteredOnTimeHeader, customersRegisteredOnATimeFrameValue, null);
        customerRegisteredOnATimeCard.setBackgroundColor("#DDD");
        customerRegisteredOnATimeCard.setPadding(new Insets(5.0D));
        customerRegisteredOnATimeCard.setRoundedCorner(10.0D);

        // Top 10 customers
        HBox topTenCustomerCardHeaderContainer = new HBox();
        Label topTenCustomerCardHeader = new Label("Top 10 customers");
        topTenCustomerCardHeader.setGraphicTextGap(10.0D);
        topTenCustomerCardHeader.getStyleClass().add("card-heading");
        FontIcon topTenCustomersFontIcon = new FontIcon(FontAwesomeSolid.USERS);
        topTenCustomersFontIcon.setFill(Paint.valueOf("#000"));
        topTenCustomerCardHeader.setGraphic(topTenCustomersFontIcon);
        topTenCustomerCardHeaderContainer.getChildren().addAll(topTenCustomerCardHeader);

        ListView<Customer> listView = new ListView<>();
        listView.setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
            @Override
            public ListCell<Customer> call(ListView<Customer> param) {
                return new ListCell<>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getFirstName() + ' ' + item.getLastName());
                        }
                    }
                };
            }
        });

        listView.getItems().addAll(Connection.getInstance().getTopTenCustomers());
        listView.setStyle("-fx-background-color: transparent; ");

        Card topTenCustomersCard = new Card(topTenCustomerCardHeaderContainer, listView, null);
        topTenCustomersCard.setPadding(new Insets(5.0D));
        topTenCustomersCard.setRoundedCorner(10.0D);

        analyticsContainer.setHgap(10.0D);
        analyticsContainer.setVgap(10.0D);
        analyticsContainer.getChildren().addAll(totalUsersCard, adminUsersCard, userCard, customerRegisteredOnATimeCard, topTenCustomersCard);


        HBox.setHgrow(tableAndFormContainer, Priority.ALWAYS);
        HBox.setHgrow(analyticsContainer, Priority.ALWAYS);
        this.getChildren().addAll(tableAndFormContainer, analyticsContainer);
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public void getTotalNumberOfUsers(){

    }

    @Override
    public void lightTheme() {
        this.getStylesheets().remove(Constants.DARK_THEME_CSS);
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
        this.setStyle("-fx-background-color: #EEE;");
    }

    @Override
    public void darkTheme() {
        this.getStylesheets().remove(Constants.LIGHT_THEME_CSS);
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
        this.setStyle("-fx-background-color: #111; ");
    }
}
