package com.example.progettoprogrammazione;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * @Authors: Deniss,Marius,Gaia
 */

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("server.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        ServerController controller = fxmlLoader.getController();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        controller.init();
 /*       stage.setOnCloseRequest(windowEvent -> {
            controller.handleWindowClose();
        });*/
    }

    public static void main(String[] args) {
        launch();

    }
}


