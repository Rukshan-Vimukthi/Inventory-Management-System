module com.example.inventorymanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.inventorymanagementsystem to javafx.fxml;
    exports com.example.inventorymanagementsystem;
    exports com.example.inventorymanagementsystem.components;
    opens com.example.inventorymanagementsystem.components to javafx.fxml;
}