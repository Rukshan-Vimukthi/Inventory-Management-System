module com.example.inventorymanagementsystem {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires com.google.gson;
//    requires org.apache.poi.ooxml;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires java.prefs;
//    requires org.apache.commons.configuration2;

    opens com.example.inventorymanagementsystem to javafx.fxml;
    opens com.example.inventorymanagementsystem.models to javafx.base;
    opens com.example.inventorymanagementsystem.state to com.google.gson;
    exports com.example.inventorymanagementsystem;
    exports com.example.inventorymanagementsystem.view.components;
    exports com.example.inventorymanagementsystem.services.interfaces;

    opens com.example.inventorymanagementsystem.view.components to javafx.fxml;
}