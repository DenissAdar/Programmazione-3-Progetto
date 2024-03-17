module com.example.progettoprogrammazione {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.progettoprogrammazione to javafx.fxml;
    exports com.example.progettoprogrammazione;
}