package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.User;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

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

    private void init(){
        this.setFillHeight(true);
        this.setPadding(new Insets(10.0D));

        firstName = new Label("", userNameIcon);
        lastName = new Label("");
        userName = new Label("");
        email = new Label("", emailIcon);
        phone = new Label("", phoneIcon);
        registeredDate = new Label("", calenderIcon);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(300.0D);
        imageView.setFitHeight(300.0D);

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
        this.getChildren().addAll(imageView, informationContainer, emptyDataContainer);
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
