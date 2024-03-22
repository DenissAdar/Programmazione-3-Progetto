module com.example.progettoprogrammazione {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires com.fasterxml.jackson.databind;


    opens com.example.progettoprogrammazione to javafx.fxml;
    exports com.example.progettoprogrammazione;



}