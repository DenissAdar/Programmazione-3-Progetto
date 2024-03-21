module com.example.progettoprogrammazione {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;


    opens com.example.progettoprogrammazione to javafx.fxml;
    exports com.example.progettoprogrammazione.Model;
    opens com.example.progettoprogrammazione.Model to javafx.fxml;
    exports com.example.progettoprogrammazione.Controller;
    opens com.example.progettoprogrammazione.Controller to javafx.fxml;
    exports com.example.progettoprogrammazione;


}