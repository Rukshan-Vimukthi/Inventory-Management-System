package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.InventoryManagementApplication;
import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class UserPreview extends HBox implements ThemeObserver {
    Label firstName;
    Label lastName;

    Label userName;
    Label email;
    Label phone;
    Label registeredDate;

    FontIcon userNameIcon = new FontIcon(FontAwesomeSolid.USER);
    FontIcon emailIcon = new FontIcon(FontAwesomeSolid.ENVELOPE);
    FontIcon calenderIcon = new FontIcon(FontAwesomeSolid.CALENDAR);
    FontIcon phoneIcon = new FontIcon(FontAwesomeSolid.PHONE);

    BorderPane emptyDataContainer;
    VBox informationContainer;

    ImageView imageView;

    Image defaultImage;

    private void init(){
        this.setFillHeight(true);
        this.setPadding(new Insets(10.0D));

        firstName = new Label("", userNameIcon);
        lastName = new Label("");
        userName = new Label("");
        email = new Label("", emailIcon);
        phone = new Label("", phoneIcon);
        registeredDate = new Label("", calenderIcon);

        imageView = new ImageView();
        imageView.setFitWidth(300.0D);
        imageView.setFitHeight(300.0D);
        try {
            defaultImage = new Image(String.valueOf(InventoryManagementApplication.class.getResource("images/ChatGPT Image Jun 2, 2025, 07_50_06 AM.png").toURI()));
            imageView.setImage(defaultImage);
        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        firstName.setStyle("-fx-font-size: 18px;");
        lastName.setStyle("-fx-font-size: 18px;");
        userName.setStyle("-fx-font-size: 18px;");
        email.setStyle("-fx-font-size: 18px;");
        phone.setStyle("-fx-font-size: 18px;");
        registeredDate.setStyle("-fx-font-size: 18px;");

        informationContainer = new VBox();
        informationContainer.setVisible(false);
        emptyDataContainer = new BorderPane();
        Label emptyDataMessage = new Label("No User or Customer Selected");
        emptyDataMessage.setTextFill(Paint.valueOf("#555"));
        emptyDataContainer.setCenter(emptyDataMessage);
        HBox.setHgrow(emptyDataContainer, Priority.ALWAYS);

        VBox nameContainer = new VBox();
        HBox firstAndLastnameContainer = new HBox();
        firstAndLastnameContainer.setSpacing(5.0D);
        firstAndLastnameContainer.getChildren().addAll(firstName, lastName);
        nameContainer.getChildren().addAll(firstAndLastnameContainer, userName);

        informationContainer.getChildren().addAll(nameContainer, email, phone, registeredDate);
        informationContainer.setSpacing(5.0D);
        this.getChildren().addAll(imageView, informationContainer, emptyDataContainer);
        this.setSpacing(10.0D);
        HBox.setHgrow(informationContainer, Priority.ALWAYS);
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public UserPreview(User user){
        super();
        init();
        informationContainer.setVisible(true);
        emptyDataContainer.setVisible(false);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        userName.setText(user.getUserName());
        email.setText(user.getEmail());

        String imageURI = null;
        if (user.getImagePath() == null){
            imageView.setImage(defaultImage);
        }else if(user.getImagePath().isBlank() && user.getImagePath().isEmpty()){
            imageView.setImage(defaultImage);
        }else {
            try {
                File imageFile = new File(user.getImagePath());
                imageURI = imageFile.toURI().toURL().toString();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            System.out.println(imageURI);
            imageView.setImage(new Image(imageURI));
        }
    }

    public UserPreview(Customer customer){
        super();
        init();
        informationContainer.setVisible(true);
        emptyDataContainer.setVisible(false);
        firstName.setText(customer.getFirstName());
        lastName.setText(customer.getLastName());
        email.setText(customer.getEmail());
        phone.setText(customer.getPhone());

        String imageURI = null;
        if (customer.getImagePath() == null){
            imageView.setImage(defaultImage);
        }else if(customer.getImagePath().isBlank() && customer.getImagePath().isEmpty()){
            imageView.setImage(defaultImage);
        }else {
            try {
                File imageFile = new File(customer.getImagePath());
                imageURI = imageFile.toURI().toURL().toString();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            System.out.println(imageURI);
            imageView.setImage(new Image(imageURI));
        }

    }

    public UserPreview(){
        super();
        init();
        informationContainer.setVisible(false);
        emptyDataContainer.setVisible(true);
    }

    public void setUserData(User user){
        informationContainer.setVisible(true);
        emptyDataContainer.setVisible(false);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getUserName());
        userName.setText(user.getUserName());
        email.setText(user.getEmail());
        registeredDate.setText(user.getRegisteredDate());

        String imageURI = null;
        if (user.getImagePath() == null){
            imageView.setImage(defaultImage);
        }else if(user.getImagePath().isBlank() && user.getImagePath().isEmpty()){
            imageView.setImage(defaultImage);
        }else {
            try {
                System.out.println("Image Path Received: " + user.getImagePath());
                File imageFile = new File(user.getImagePath());
                System.out.println("Name: " + imageFile.getName());
                System.out.println("URI" + imageFile.toURI());
                imageURI = imageFile.toURI().toURL().toString();
                System.out.println("Absolute path: " + imageFile.getAbsolutePath());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            System.out.println(imageURI);
            imageView.setImage(new Image(imageURI));
        }
    }

    public void setCustomerData(Customer customer){
        informationContainer.setVisible(true);
        emptyDataContainer.setVisible(false);
        firstName.setText(customer.getFirstName());
        lastName.setText(customer.getLastName());
        userName.setText("");
        email.setText(customer.getEmail());
        phone.setText(customer.getPhone());
        registeredDate.setText(customer.getRegisteredDate());

        String imageURI = null;
        System.out.println("Image path: " + customer.getImagePath());
        if (customer.getImagePath() == null){
            imageView.setImage(defaultImage);
        }else if(customer.getImagePath().isBlank() && customer.getImagePath().isEmpty()){
            imageView.setImage(defaultImage);
        }else {
            try {
                File imageFile = new File(customer.getImagePath());
                imageURI = imageFile.toURI().toURL().toString();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            System.out.println(imageURI);
            imageView.setImage(new Image(imageURI));
        }
    }

    @Override
    public void lightTheme() {
        firstName.setTextFill(Paint.valueOf("#000"));
        lastName.setTextFill(Paint.valueOf("#000"));
        userName.setTextFill(Paint.valueOf("#000"));
        email.setTextFill(Paint.valueOf("#000"));
        phone.setTextFill(Paint.valueOf("#000"));
        registeredDate.setTextFill(Paint.valueOf("#000"));

        userNameIcon.setFill(Paint.valueOf("#000"));
        emailIcon.setFill(Paint.valueOf("#000"));
        calenderIcon.setFill(Paint.valueOf("#000"));
        phoneIcon.setFill(Paint.valueOf("#000"));

        try{
            this.getStyleClass().remove("user-preview-dark");
            this.getStyleClass().add("user-preview-light");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void darkTheme() {
        firstName.setTextFill(Paint.valueOf("#FFFFFF"));
        lastName.setTextFill(Paint.valueOf("#FFFFFF"));
        userName.setTextFill(Paint.valueOf("#FFFFFF"));
        email.setTextFill(Paint.valueOf("#FFFFFF"));
        phone.setTextFill(Paint.valueOf("#FFFFFF"));
        registeredDate.setTextFill(Paint.valueOf("#FFFFFF"));

        userNameIcon.setFill(Paint.valueOf("#FFF"));
        emailIcon.setFill(Paint.valueOf("#FFF"));
        calenderIcon.setFill(Paint.valueOf("#FFF"));
        phoneIcon.setFill(Paint.valueOf("#FFF"));

        try{
            this.getStyleClass().remove("user-preview-light");
            this.getStyleClass().add("user-preview-dark");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
