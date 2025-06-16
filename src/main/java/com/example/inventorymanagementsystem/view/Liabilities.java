package com.example.inventorymanagementsystem.view;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.LiableCustomers;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import com.example.inventorymanagementsystem.state.Data;
import com.example.inventorymanagementsystem.view.components.Card;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class Liabilities extends HBox implements ThemeObserver {

    private double totalAmountOwedToShop = 0.0D;
    public Liabilities() throws SQLException {
        super();
        this.setPadding(new Insets(10.0D));
        this.setSpacing(10.0D);

        VBox cardsAndTableContainer = new VBox();
        HBox cardContainer = new HBox();

        Label totalLiabilitiesHeader = new Label("Total Liabilities");
        totalLiabilitiesHeader.getStyleClass().add("card-heading");
        totalLiabilitiesHeader.setStyle("-fx-text-fill: #FF0000;");
        Label totalLiabilitiesAmount = new Label("0");
        totalLiabilitiesAmount.getStyleClass().add("card-content");
        totalLiabilitiesAmount.setStyle("-fx-text-fill: #FF0000;");

        Card totalLiabilities = new Card(totalLiabilitiesHeader, totalLiabilitiesAmount, null);
        totalLiabilities.getStyleClass().add("card");
        totalLiabilities.setStyles("-fx-background-color: #000000; ");

        Label totalLiableCustomersHeader = new Label("Customers Liable");
        totalLiableCustomersHeader.getStyleClass().add("card-heading");
        Label totalLiableCustomersValue = new Label("0");
        totalLiableCustomersValue.getStyleClass().add("card-content");
        Card totalLiableCustomers = new Card(totalLiableCustomersHeader, totalLiableCustomersValue, null);
        totalLiableCustomers.getStyleClass().add("card");

        FontIcon pointsIcon = new FontIcon(FontAwesomeSolid.COINS);
        pointsIcon.setFill(Paint.valueOf("#FFDD00"));
//        pointsIcon.setIconSize(50);

        Label totalPointsHeader = new Label("Total Points");
        totalPointsHeader.setGraphic(pointsIcon);
        totalPointsHeader.setGraphicTextGap(10.0D);
        totalPointsHeader.getStyleClass().add("card-heading");
        Label totalPointsValue = new Label("0");
        totalPointsValue.getStyleClass().add("card-content");
//        totalPointsValue.setGraphic(pointsIcon);
//        totalPointsValue.setContentDisplay(ContentDisplay.TOP);
        Card totalPoints = new Card(totalPointsHeader, totalPointsValue, null);
        totalPoints.getStyleClass().add("card");

        cardContainer.getChildren().addAll(totalLiabilities, totalLiableCustomers, totalPoints);
        cardContainer.setSpacing(10.0D);

        TableView<LiableCustomers> liableCustomersTableView = new TableView<>();

        TableColumn<LiableCustomers, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(param.getValue().customerObject.get().getFirstName());
            }
        });

        TableColumn<LiableCustomers, String> liableAmountColumn = new TableColumn<>("Liable Amount");
        liableAmountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LiableCustomers, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getLiableAmount()));
            }
        });

        TableColumn<LiableCustomers, Customer> pointsAvailableColumn = new TableColumn<>("Points Available");
//        pointsAvailableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LiableCustomers, Customer>, ObservableValue<Customer>>() {
//            @Override
//            public ObservableValue<Customer> call(TableColumn.CellDataFeatures<LiableCustomers, Customer> param) {
//                return new SimpleObjectProperty<>();
//            }
//        });

        pointsAvailableColumn.setCellFactory(new Callback<TableColumn<LiableCustomers, Customer>, TableCell<LiableCustomers, Customer>>() {
            @Override
            public TableCell<LiableCustomers, Customer> call(TableColumn<LiableCustomers, Customer> param) {
                return new TableCell<>(){
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            FontIcon coinsIcon = new FontIcon(FontAwesomeSolid.COINS);
                            coinsIcon.setFill(Paint.valueOf("#FFDD00"));
                            this.setText(String.valueOf(item.getPoints()));
                            this.setGraphic(coinsIcon);
                            this.setGraphicTextGap(5.0D);
                        }
                    }
                };
            }
        });

        totalLiabilitiesAmount.setText(Constants.CURRENCY_UNIT + Data.getInstance().getTotalAccountsReceivable());
        totalLiableCustomersValue.setText(String.valueOf(Data.getInstance().getLiableCustomers().size()));
        liableCustomersTableView.getColumns().addAll(firstNameColumn, liableAmountColumn, pointsAvailableColumn);

        liableCustomersTableView.setItems(Data.getInstance().getLiableCustomers());
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");
        LineChart<Number, Number> accountsReceivableProgress = new LineChart<>(xAxis, yAxis);
        accountsReceivableProgress.setTitle("Accounts Receivables and Points");
        accountsReceivableProgress.getStyleClass().add("account-receivable-and-points");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");

        series.getData().add(new XYChart.Data<>(1, 10));
        series.getData().add(new XYChart.Data<>(2, 15));
        series.getData().add(new XYChart.Data<>(3, 5));
        series.getData().add(new XYChart.Data<>(4, 40));
        series.getData().add(new XYChart.Data<>(5, 35));
        series.getData().add(new XYChart.Data<>(6, 20));
        series.getData().add(new XYChart.Data<>(7, 1));

        accountsReceivableProgress.getData().add(series);

        cardsAndTableContainer.getChildren().addAll(cardContainer, liableCustomersTableView, accountsReceivableProgress);
        cardsAndTableContainer.setSpacing(10.0D);

        VBox leftSideBar = new VBox();
        leftSideBar.setMinWidth(300.0D);
        leftSideBar.setMaxWidth(300.0D);
        this.getChildren().addAll(cardsAndTableContainer, leftSideBar);
        HBox.setHgrow(cardsAndTableContainer, Priority.ALWAYS);
    }

    @Override
    public void lightTheme() {
        this.setStyle("-fx-background-color: #EEE; ");
        this.getStylesheets().remove(Constants.DARK_THEME_CSS);
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
    }

    @Override
    public void darkTheme() {
        this.setStyle("-fx-background-color: #222; ");
        this.getStylesheets().remove(Constants.LIGHT_THEME_CSS);
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }
}
