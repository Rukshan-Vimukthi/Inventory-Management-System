package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import javafx.geometry.Pos;
import javafx.stage.Popup;
import javafx.scene.control.Label;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;

public class HoverTooltip {
    private final Popup popup = new Popup();
    private final Label label = new Label();
    private final StackPane container = new StackPane(label);

    public HoverTooltip(String tooltipText) {
        label.setText(tooltipText);
        label.setAlignment(Pos.TOP_CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setFont(new Font(13));
        popup.getContent().add(new StackPane(label));
        popup.setAutoHide(true);
        label.getStyleClass().add("hovering-tooltip");
        label.setStyle(
                "-fx-background-color: #2c2c2c;" +
                        "-fx-text-fill: #f0f0f0;" +
                        "-fx-padding: 8px 12px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-border-color: #3a8edb;" +
                        "-fx-border-width: 1px;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-family: 'Segoe UI', sans-serif;" +
                        "-fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.4), 6, 0.3, 0, 2);" +
                        "-fx-max-width: 300px;" +
                        "-fx-wrap-text: true;" +
                        "-fx-font-weight: bold;"
        );
    }

    public void attachTo(Node targetNode) {
        targetNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            Point2D point = targetNode.localToScreen(0, targetNode.getBoundsInLocal().getHeight());
            popup.show(targetNode, point.getX(), point.getY());
        });

        targetNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            popup.hide();
        });
    }
    public void setText(String text) {
        label.setText(text);
    }

}
