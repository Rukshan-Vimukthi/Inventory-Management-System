package com.example.inventorymanagementsystem.view.components;


import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
public class Card extends VBox implements ThemeObserver {

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
        this.setMinWidth(200.0D);
        this.setMinHeight(100.0D);
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
        if (header != null) {
            headerContainer.getChildren().add(header);
            HBox.setHgrow(this.header, Priority.ALWAYS);
        }

        if (body != null) {
            bodyContainer.setCenter(body);
        }

        if(footer != null) {
            footerContainer.getChildren().add(footer);
//            HBox.setHgrow(this.footer, Priority.ALWAYS);
        }
        VBox.setVgrow(this.headerContainer, Priority.ALWAYS);
        VBox.setVgrow(this.bodyContainer, Priority.ALWAYS);
        VBox.setVgrow(this.footerContainer, Priority.ALWAYS);
        this.setFillWidth(true);
        this.getChildren().addAll(this.headerContainer, this.bodyContainer, this.footerContainer);
    }

    public Card(String title, String body, String footer){
        super();

        titleLabel = new Label(title);
        bodyLabel = new Label(body);
        footerLabel = new Label(footer);

        headerContainer.getChildren().add(this.titleLabel);
        bodyContainer.setCenter(this.bodyLabel);
        footerContainer.getChildren().add(this.footerLabel);

        this.getChildren().addAll(this.headerContainer, this.bodyContainer, this.footerContainer);
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public void setTitle(String newTitle){
        titleLabel.setText(newTitle);
    }

    public void setBody(String newBody){
        bodyLabel.setText(newBody);
    }

    public void setFooter(String newFooter){
        footerLabel.setText(newFooter);
    }

    public void setAccountBalance(double newBalance){
        if(newBalance < 0){
            System.out.println("Incorrect balance");
        }
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

    public void setCardWidth(double width){
        this.setMinWidth(width);
        this.setMaxWidth(width);
    }

    public void setCardHeight(double height){
        this.setMinHeight(height);
        this.setMaxHeight(height);
    }

    @Override
    public void lightTheme() {
//        this.setBackgroundColor("#EEE");
    }

    @Override
    public void darkTheme() {
//        this.setBackgroundColor("#002");
    }
}
