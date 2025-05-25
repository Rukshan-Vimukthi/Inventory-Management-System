package com.example.inventorymanagementsystem.view.components;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Custom card with Header, Body and Footer
 * Methods available:
 * @code
 * setHeader(String headerText);
 * @code
 * <p>Available methods:</p>
 * <ul>
 *  @link #setHeader(String headerText)
 *  <li>#setHeader(Node header)</li>
 *  <li>#setBody(String bodyText)</li>
 *  <li>#setBody(Node newBody)</li>
 *  <li>#setFooter(String footerText)</li>
 *  <li>#setFooter(Node footerNode)</li>
 *  <li>#setBackgroundColor(String colorCode)</li>
 *  <li>#setRoundedCorner(Double cornerRadius)</li>
 *  <li>#setStyles(String style)</li>
 * </ul>
 */
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

    /**
     * This constructor is used to place the nodes for the header, body and footer at the time the card get created
     * @param header Node - node for the header
     * @param body Node - node for the body
     * @param footer Node - node for the footer
     */
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

    /**
     * Set the header text to the provided text. The text added to the Header will be a Label Node.
     * @param headerText - Text to be placed in the header.
     */
    public void setHeader(String headerText){
        if(titleLabel != null){
            titleLabel.setText(headerText);
        }
//        this.headerContainer.getChildren().remove(this.titleLabel);
    }

    /**
     * Set the Header to a specified node. Use this method if you need to customize the header with your own component
     * @param header - Node to be placed as the header
     */
    public void setHeader(Node header){
        if(this.header != null) {
            this.headerContainer.getChildren().remove(this.header);
        }

        this.header = header;
        this.headerContainer.getChildren().add(this.header);
    }

    /**
     * Set the text of the body to the provided text
     * @param bodyText - Text to be placed in the body
     */
    public void setBody(String bodyText){
        if(this.bodyLabel != null){
            this.bodyLabel.setText(bodyText);
        }
    }

    /**
     * Set the body to a specified Node. Use this method to customize the body content of the card.
     * @param newBody - Node to be placed as the body of the card
     */
    public void setBody(Node newBody){
        if (this.body != null) {
            bodyContainer.getChildren().remove(this.body);
        }
        this.body = newBody;
        bodyContainer.setCenter(this.body);
    }

    /**
     * Set the Footer text to the provided text.
     * @param footerText - Text to be placed in the footer
     */
    public void setFooter(String footerText){
        if(this.footerLabel != null){
            this.footerLabel.setText(footerText);
        }
    }

    /**
     * Set the footer content to the specified Node. Use this method to customize the footer content as you need
     * @param footerNode - Node to be placed in the footer.
     */
    public void setFooter(Node footerNode){
        if(this.footer != null) {
            this.footerContainer.getChildren().remove(this.footer);
        }
        this.footer = footerNode;
        this.footerContainer.getChildren().add(this.footer);
    }

    /**
     * Get the style already in the Card and add the new style to those styles to prevent previous styles
     * getting removed from the component
     * @param style - new style to be added
     */
    private void addStyle(String style){
        this.setStyle(this.getStyle() + style);
    }

    /**
     * Sets the background color of the card
     * @param colorCode - Background Color code to be set (ex. #0000FF)
     */
    public void setBackgroundColor(String colorCode){
        this.addStyle("-fx-background-color: %s; ".formatted(colorCode));
        System.out.println(this.getStyle());
    }

    /**
     * Set the rounded corners according to the provided Corner Radius
     * @param cornerRadius Double - Corner Radius
     */
    public void setRoundedCorner(Double cornerRadius){
        this.addStyle("-fx-border-radius: %f; ".formatted(cornerRadius));
        this.addStyle("-fx-background-radius: %f; ".formatted(cornerRadius));
    }

    /**
     * Add a new style to the card component. Make sure to end each style with ; and a space character.
     * (ex. "-fx-background-color: #888; ")
     * @param style - JavaFX style to be added. (ex. -fx-background-color: #888; )
     */
    public void setStyles(String style){
        this.addStyle(style);
    }
}
