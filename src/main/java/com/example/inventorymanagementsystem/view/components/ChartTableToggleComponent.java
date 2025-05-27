package com.example.inventorymanagementsystem.view.components;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;

public class ChartTableToggleComponent extends VBox {

    private final Node chart;
    private final TableView<?> table;
    private final Button toggleButton;
    private final BorderPane container;
    private boolean showingChart = true;

    public ChartTableToggleComponent(Node chart, TableView<?> table) {
        this.chart = chart;
        this.table = table;

        ((Region) chart).prefWidthProperty().bind(this.widthProperty());
        ((Region) table).prefWidthProperty().bind(this.widthProperty());

        this.toggleButton = new Button("Show Table");

        this.container = new BorderPane();
        container.setCenter(chart);
        VBox.setVgrow(container, Priority.ALWAYS);

        Button toggleButton = new Button("Show Table");
        toggleButton.setOnAction(e -> {
            showingChart = !showingChart;
            container.setCenter(showingChart ? chart : table);
            toggleButton.setText(showingChart ? "Show Table" : "Show Chart");
        });

        HBox buttonContainer = new HBox(toggleButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-padding: 10;");

        toggleButton.setAlignment(Pos.CENTER);

        this.setSpacing(10);
        this.getChildren().addAll(container, buttonContainer);
    }
}
