package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.state.Fonts;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;

/**
 * This class is used to build the custom tabs. call buildTab() method to build a tab
 *
 * ex. TabBuilder.buildTab()
 */
public class TabBuilder {

    private static HBox hbox;
    private static Label tabTitle;
    private static Tab tab;

    /**
     * Creates a new tab
     * @param text - the text to be included in the tab
     * @return newly created Tab instance.
     */
    public static Tab buildTab(String text){
        Tab tab = createTab(text);
        tabTitle.setFocusTraversable(false);
        return createTab(text);
    }

    public static Tab buildTab(String text, Node icon){
        Tab tab = createTab(text);
        tabTitle.setGraphic(icon);
        tabTitle.setGraphicTextGap(10.0D);
        tabTitle.setFocusTraversable(false);
        hbox.setFocusTraversable(false);
        return tab;
    }

    private static Tab createTab(String text){
        hbox = new HBox();
        hbox.setFillHeight(true);

        tabTitle = new Label(text);
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

        tab = new Tab("");
//        tab.setContent(hbox);
        tab.setGraphic(hbox);
        tab.setClosable(false);
        tab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                if (tab.isDisable() && tab.isSelected()){
                    TabPane tabPane = tab.getTabPane();
                    if (tabPane != null){
                        for (Tab tab : tabPane.getTabs()){
                            if (!tab.isDisable()){
                                tabPane.getSelectionModel().select(tab);
                            }
                        }
                    }
                }
            }
        });
        return tab;
    }
}
