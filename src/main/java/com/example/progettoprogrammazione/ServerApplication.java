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
    private ServerController controller;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        controller = fxmlLoader.getController();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        controller.init();
    }

    @Override
    public void stop() throws Exception {
        //controller.handleWindowClose();
        controller.close();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
        System.exit(0);
    }
}


