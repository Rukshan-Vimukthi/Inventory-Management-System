package com.example.inventorymanagementsystem.view.components;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FormField<C extends Control> extends VBox {
    private C node;
    public FormField(String labelText, Class<C> nodeClass){
        try {
            Label label = new Label(labelText);

            node = nodeClass.getDeclaredConstructor().newInstance();
            this.getChildren().addAll(label, node);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public String getValue(){
        if (node != null) {
            return ((C)node);
        }
        return null;
    }
}
