module com.example.inventorymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires mysql.connector.j;

    opens com.example.inventorymanagementsystem to javafx.fxml;
    opens com.example.inventorymanagementsystem.models to javafx.base;
    exports com.example.inventorymanagementsystem;
    exports com.example.inventorymanagementsystem.view.components;
    exports com.example.inventorymanagementsystem.services.interfaces;

    opens com.example.inventorymanagementsystem.view.components to javafx.fxml;
}