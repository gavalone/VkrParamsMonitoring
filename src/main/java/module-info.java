module com.example.standtrain {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.sun.jna;

    opens com.example.standtrain to javafx.fxml;
    exports com.example.standtrain;
    exports com.example.standtrain.controllers;
    opens com.example.standtrain.controllers to javafx.fxml;
    exports com.example.standtrain.services;
    opens com.example.standtrain.services to javafx.fxml;
}