package com.example.progettoprogrammazione;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


/**
 * @Authors: Deniss,Marius,Gaia
 */

public class ServerController {
    @FXML
    private TextArea ServerLog;
    private Server server ;
    @FXML
    ListView<String> serverList;

    public void init()
    {
        server = new Server();
        System.out.println("Avviato new server");
        serverList.itemsProperty().bind(server.getLogList());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.exit();
            System.out.println("Server shut down.");
        }));
    }

    public void close() {
        server.exit();
    }
}
