module com.example.inventorymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires java.prefs;
    requires com.google.gson;
//    requires org.apache.commons.configuration2;

    opens com.example.inventorymanagementsystem to javafx.fxml;
    opens com.example.inventorymanagementsystem.models to javafx.base;
    exports com.example.inventorymanagementsystem;
    exports com.example.inventorymanagementsystem.view.components;
    exports com.example.inventorymanagementsystem.services.interfaces;

    opens com.example.inventorymanagementsystem.view.components to javafx.fxml;
}