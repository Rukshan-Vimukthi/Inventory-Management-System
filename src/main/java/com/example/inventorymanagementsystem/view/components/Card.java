package com.example.inventorymanagementsystem.view.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Card extends VBox {

    private Label titleLabel;
    private Label bodyLabel;
    private Label footerLabel;

    private HBox headerContainer;
    private BorderPane bodyContainer;
    private HBox footerContainer;

    private Node header;
    private Node body;
    private Node footer;

    public void initContainers(){
        this.headerContainer = new HBox();
        this.bodyContainer = new BorderPane();
        this.footerContainer = new HBox();

        System.out.println(this.getStyle());
    }

    public Card(Node header, Node body, Node footer){
        initContainers();

        this.header = header;
        this.body = body;
        this.footer = footer;

        headerContainer.getChildren().add(header);
        bodyContainer.setCenter(body);
        footerContainer.getChildren().add(footer);

        this.getChildren().addAll(this.headerContainer, this.bodyContainer, this.footerContainer);
    }

    public Card(String title, String body, String footer){
        super();
        initContainers();

        titleLabel = new Label(title);
        bodyLabel = new Label(body);
        footerLabel = new Label(footer);

        headerContainer.getChildren().add(this.titleLabel);
        bodyContainer.setCenter(this.bodyLabel);
        footerContainer.getChildren().add(this.footerLabel);

        this.getChildren().addAll(this.headerContainer, this.bodyContainer, this.footerContainer);
    }

    public void setHeader(String headerText){
        if(titleLabel != null){
            titleLabel.setText(headerText);
        }
//        this.headerContainer.getChildren().remove(this.titleLabel);
    }

    public void setHeader(Node header){
        if(this.header != null) {
            this.headerContainer.getChildren().remove(this.header);
        }

        this.header = header;
        this.headerContainer.getChildren().add(this.header);
    }

    public void setBody(String bodyText){
        if(this.bodyLabel != null){
            this.bodyLabel.setText(bodyText);
        }
    }

    public void setBody(Node newBody){
        if (this.body != null) {
            bodyContainer.getChildren().remove(this.body);
        }
        this.body = newBody;
        bodyContainer.setCenter(this.body);
    }

    public void setFooter(String footerText){
        if(this.footerLabel != null){
            this.footerLabel.setText(footerText);
        }
    }

    public void setFooter(Node footerNode){
        if(this.footer != null) {
            this.footerContainer.getChildren().remove(this.footer);
        }
        this.footer = footerNode;
        this.footerContainer.getChildren().add(this.footer);
    }

    private void addStyle(String style){
        this.setStyle(this.getStyle() + style);
    }

    public void setBackgroundColor(String colorCode){
        this.addStyle("-fx-background-color: %s; ".formatted(colorCode));
        System.out.println(this.getStyle());
    }

    public void setRoundedCorner(Double cornerRadius){
        this.addStyle("-fx-border-radius: %f; ".formatted(cornerRadius));
        this.addStyle("-fx-background-radius: %f; ".formatted(cornerRadius));
    }

    public void setStyles(String style){
        this.setStyle(this.getStyle() + " " + style);
    }

}
