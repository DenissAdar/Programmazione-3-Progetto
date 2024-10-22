package com.example.progettoprogrammazione;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @Authors: Deniss,Marius,Gaia
 */

public class ClientApplication extends Application {
@Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("client.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ClientController controller = fxmlLoader.getController();
        controller.init();

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();


        stage.setOnCloseRequest(windowEvent -> {
            controller.handleWindowClose();
        });
}
    public static void main(String[] args) {
        launch();

    }
}
