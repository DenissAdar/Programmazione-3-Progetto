package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// Come funzionano i bottoni degli OnAction
public class ClientController {
    @FXML
    private Label accountDisplay;

    @FXML
    private Label connectionNotification;

    @FXML
    private TextField dataTxt;

    @FXML
    private Button deleteBtn;

    @FXML
    private TextField destinatarioTxt;

    @FXML
    private Button forwardBtn;

    @FXML
    private Button inMailBtn;

    @FXML
    private Button inviaBtn;

    @FXML
    private TextArea mailBodyTxt;

    @FXML
    private ListView<?> mailList;

    @FXML
    private TextField mittenteTxt;

    @FXML
    private Button newMailBtn;

    @FXML
    private TextField oggettoTxt;

    @FXML
    private Button outMailBtn;
    @FXML
    private Label test;

    @FXML
    private Button replyAllBtn;

    @FXML
    private Button replyBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Label dataLable;
    private Client client;

    public void setUnvisible(){

        replyBtn.setVisible(false);
        replyAllBtn.setVisible(false);
        forwardBtn.setVisible(false);
        deleteBtn.setVisible(false);
        connectionNotification.setVisible(false);
        dataTxt.setVisible(false);
        dataLable.setVisible(false);


    }
    @FXML
    public void newMailCreation(){
        //Da bindare il mittente con la property giusta
        destinatarioTxt.clear();
        oggettoTxt.clear();
        mailBodyTxt.clear();
        setUnvisible();
    }
    public void init(){
        client = new Client();
        setUnvisible();

        accountDisplay.textProperty().bind(client.giveAccountProperty())  ;

    }
}
