package com.example.progettoprogrammazione;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ServerController {

    @FXML
    private TextArea ServerLog;
    public void init()
    {
        ServerLog.textProperty().set("Funziona");
    }
}
