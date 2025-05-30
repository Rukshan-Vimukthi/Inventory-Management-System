package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.models.Size;
import com.example.inventorymanagementsystem.models.Stock;
import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import com.example.inventorymanagementsystem.services.interfaces.ThemeObserver;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;

public class FormField<C extends Control, M> extends VBox implements ThemeObserver {
    private Control node;
    private String columnName = null;
    private Label label;
    public FormField(String labelText, Class<C> nodeClass){
        try {
            label = new Label(labelText);

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
                return ((TextField) node).getText();
            }else if(node instanceof ComboBox<?> comboBox){
                return ((ComboBox<?>)node).getSelectionModel().getSelectedItem();
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
    }

    private void buildPasswordField(){
        node = new PasswordField();
    }

    private void buildColorPicker(){
        node = new ColorPicker();
    }
    private void buildComboBox(){
        ComboBox<M> comboBox = new ComboBox<M>();
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
        node = comboBox;
    }

    private void buildComboBox(ObservableList<M> data){
        ComboBox<M> comboBox = new ComboBox<M>(data);
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
        node = comboBox;
    }

    private void buildComboBox(ObservableList<M> data, M selectedItem){
        ComboBox<M> comboBox = new ComboBox<M>(data);
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
            }
        }
    }

    public void setValue(LocalDate localDate){
        if (node instanceof DatePicker datePicker){
            datePicker.setValue(localDate);
        }
    }

    @Override
    public void lightTheme() {
        try {
            label.getStyleClass().remove("custom-form-field-label-dark");
        }catch(Exception e){
            e.printStackTrace();
        }
        label.getStyleClass().add("custom-form-field-label-light");

        if (node != null) {
            node.getStyleClass().add("default-text-areas");
        }
    }

    @Override
    public void darkTheme() {
        try {
            label.getStyleClass().remove("custom-form-field-label-light");
        }catch(Exception e){
            e.printStackTrace();
        }
        label.getStyleClass().add("custom-form-field-label-dark");
        if(node != null) {
            node.getStyleClass().add("default-text-areas");
        }
    }
}
