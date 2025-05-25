package com.example.inventorymanagementsystem.view.components;

import com.example.inventorymanagementsystem.services.interfaces.DataModel;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class FormField<C extends Control, M> extends VBox {
    private Control node;
    public FormField(String labelText, Class<C> nodeClass){
        try {
            Label label = new Label(labelText);

            if (nodeClass == TextField.class){
                buildTextField();
            }else if(nodeClass == ComboBox.class){
                buildComboBox();
            }else if(nodeClass == ColorPicker.class){
                buildColorPicker();
            }

            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public FormField(String labelText, Class<C> nodeClass, ObservableList<M> items){
        try {
            Label label = new Label(labelText);

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
    }

    public FormField(String labelText, Class<C> nodeClass, ObservableList<M> items, M selectedItem){
        try {
            Label label = new Label(labelText);

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
    }

    public Object getValue(){
        if (node != null) {
            if (node instanceof TextField textField){
                return ((TextField) node).getText();
            }else if(node instanceof ComboBox<?> comboBox){
                return ((ComboBox<?>)node).getSelectionModel().getSelectedItem();
            }else if(node instanceof ColorPicker colorPicker){
                return colorPicker.getValue().toString();
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
        comboBox.getSelectionModel().select(selectedItem);
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
}
