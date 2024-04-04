package com.example.progettoprogrammazione;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class ServerController {

    @FXML
    private TextArea ServerLog;
    private Server server ;
    @FXML
    ListView<String> serverList;

    public void init()
    {
        server = new Server();
        serverList.itemsProperty().bind(server.getLogList());
        //server.listen(6000);
        //ServerLog.textProperty().bind(server.getAccountLogProperty());
    }
}
