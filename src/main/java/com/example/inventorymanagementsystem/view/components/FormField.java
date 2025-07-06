package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.Customer;
import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import com.example.inventorymanagementsystem.state.Constants;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormField<C extends Control, M> extends VBox implements ThemeObserver {
    private Control node;
    private String columnName = null;
    private Label label;

    public FormField(String labelText, Class<C> nodeClass){
        try {
            label = new Label(labelText);
            label.getStyleClass().add("custom-form-field-label");
            if (nodeClass == TextField.class){
                buildTextField();
            }else if(nodeClass == ComboBox.class){
                buildComboBox();
            }else if(nodeClass == ColorPicker.class){
                buildColorPicker();
            }else if(nodeClass == DatePicker.class){
                buildDatePicker(null);
            }else if(nodeClass == PasswordField.class){
                buildPasswordField();
            }
            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }

        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public FormField(String labelText, Class<DatePicker> nodeClass, String value){
        try {
            label = new Label(labelText);
            label.getStyleClass().add("custom-form-field-label");
            if(nodeClass == DatePicker.class){
                try {
                    buildDatePicker(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }

        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public FormField(String labelText, Class<C> nodeClass, ObservableList<M> items){
        try {
            label = new Label(labelText);
            label.getStyleClass().add("custom-form-field-label");
            if (nodeClass == TextField.class){
                buildTextField();
            }else if(nodeClass == ComboBox.class){
                buildComboBox(items);
            }else if(nodeClass == ColorPicker.class){
                buildColorPicker();
            }

            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);

    }

    public FormField(String labelText, Class<C> nodeClass, ObservableList<M> items, M selectedItem){
        try {
            label = new Label(labelText);
            label.getStyleClass().add("custom-form-field-label");
            if (nodeClass == TextField.class){
                buildTextField();
            }else if(nodeClass == ComboBox.class){
                buildComboBox(items, selectedItem);
            }else if(nodeClass == ColorPicker.class){
                buildColorPicker();
            }

            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        com.example.inventorymanagementsystem.state.ThemeObserver.init().addObserver(this);
    }

    public Object getValue(){
        if (node != null) {
            if (node instanceof TextField textField){
                return textField.getText();
            }else if(node instanceof ComboBox<?> comboBox){
                return comboBox.getSelectionModel().getSelectedItem();
            }else if(node instanceof ColorPicker colorPicker){
                return colorPicker.getValue().toString();
            }else if(node instanceof DatePicker datePicker){
                return datePicker.getValue();
            }else if(node instanceof PasswordField passwordField){
                return passwordField.getText();
            }
        }
        return null;
    }

    public void setItems(ObservableList<?> data){
        if (node instanceof ComboBox<?> comboBox){
            ((ComboBox)node).setItems(data);
        }
    }

    private void buildTextField(){
        node = new TextField();
        node.getStyleClass().add("default-text-areas");
    }
    private void buildPasswordField(){
        node = new PasswordField();
    }

    private void buildColorPicker(){
        node = new ColorPicker();
    }
    private void buildComboBox(){
        ComboBox<M> comboBox = new ComboBox<M>();
        comboBox.getStyleClass().add("default-dropdowns");
        comboBox.setCellFactory(new Callback<ListView<M>, ListCell<M>>() {
            @Override
            public ListCell<M> call(ListView<M> param) {
                return new ListCell<M>(){
                    @Override
                    protected void updateItem(M item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            setText(((DataModel)item).getValue());
                        }
                    }
                };
            }
        });
        comboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(M item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(((DataModel)item).getValue());
                }
                setTextFill(Paint.valueOf("#0088FF"));
            }
        });
        node = comboBox;
    }

    private void buildComboBoxWithFilterEnables(ListView<M> param){

    }

    private void buildComboBox(ObservableList<M> data){
        ComboBox<M> comboBox = new ComboBox<M>(data);
        comboBox.getStyleClass().add("default-dropdowns");
        comboBox.setCellFactory(new Callback<ListView<M>, ListCell<M>>() {
            @Override
            public ListCell<M> call(ListView<M> param) {
                return new ListCell<M>(){
                    @Override
                    protected void updateItem(M item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            setText(((DataModel)item).getValue());
                        }
                    }
                };
            }
        });
        comboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(M item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(((DataModel)item).getValue());
                }
                setTextFill(Paint.valueOf("#0088FF"));
            }
        });
        node = comboBox;
    }

    private void buildComboBox(ObservableList<M> data, M selectedItem){
        ComboBox<M> comboBox = new ComboBox<M>(data);
        comboBox.getStyleClass().add("default-dropdowns");
        comboBox.setCellFactory(new Callback<ListView<M>, ListCell<M>>() {
            @Override
            public ListCell<M> call(ListView<M> param) {
                return new ListCell<M>(){
                    @Override
                    protected void updateItem(M item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            setText(((DataModel)item).getValue());
                        }
                    }
                };
            }
        });

        comboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(M item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(((DataModel)item).getValue());
                }
                setTextFill(Paint.valueOf("#0088FF"));
            }
        });

        comboBox.getSelectionModel().select(selectedItem);
        node = comboBox;
    }

    public void buildDatePicker(String date){
        DatePicker datePicker = new DatePicker();
        if (date != null) {
            datePicker.setValue(LocalDate.parse(date));
        }
        node = datePicker;
    }

    public void setColumnName(String columnName){
        this.columnName = columnName;
    }

    public String getColumnName(){
        return this.columnName;
    }

    public void setValue(String value){
        if (value != null) {
            if (node instanceof TextField textField) {
                textField.setText(value);
            } else if (node instanceof ColorPicker colorPicker) {
                colorPicker.setValue(Color.valueOf(value));
            }else if (node instanceof DatePicker datePicker){
                try{
                    LocalDate localDate = LocalDate.parse(value);
                    setValue(localDate);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void setValue(LocalDate localDate){
        if (node instanceof DatePicker datePicker){
            datePicker.setValue(LocalDate.of(Integer.parseInt(localDate.format(DateTimeFormatter.ofPattern("yyyy"))), localDate.getMonthValue(), localDate.getDayOfMonth()));
        }
    }

    public Node getControl(){
        return node;
    }

    public ComboBox<M> getComboBox(){
        return (ComboBox<M>) node;
    }

    public C getField() {
        return (C) node;
    }

    public void setEnabled(boolean isEnabled){
        node.setDisable(!isEnabled);
    }

    @Override
    public void lightTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.LIGHT_THEME_CSS);
    }

    @Override
    public void darkTheme() {
        this.getStylesheets().clear();
        this.getStylesheets().add(Constants.DARK_THEME_CSS);
    }
}
