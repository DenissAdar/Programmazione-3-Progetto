package com.example.progettoprogrammazione;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

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
    private Button outMailBtn;
    @FXML
    private Button inviaBtn;
    @FXML
    private TextArea mailBodyTxt;
    @FXML
    private ListView<String> mailList;
    @FXML
    private TextField mittenteTxt;
    @FXML
    private Button newMailBtn;
    @FXML
    private TextField oggettoTxt;
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
    private final String jsonPath = "src/main/java/com/example/progettoprogrammazione/accounts/account.json";
    private Email selectedEmail;
    private Client client;



    public void init() {

        setInvisible();
        //inMailBtn.setOnMouseClicked(this::onClickInMail);
       // outMailBtn.setOnMouseClicked(this::onClickOutMail);
    }





/* setInvisible setta i diversi pulsanti invisibili in modo da
non vederli quando non servono(aka quando scrivo una mail)*/
    public void setInvisible() {
        replyBtn.setVisible(false);
        replyAllBtn.setVisible(false);
        forwardBtn.setVisible(false);
        deleteBtn.setVisible(false);
        connectionNotification.setVisible(false);
        dataTxt.setVisible(false);
        dataLable.setVisible(false);


    }

/*Pulisce i campi su cui devo scrivere quando creo una nuova mail*/
    @FXML
    public void newMailCreation() {
        //Da bindare il mittente con la property giusta
        destinatarioTxt.clear();
        oggettoTxt.clear();
        mailBodyTxt.clear();
        setInvisible();

    }
/*Situazione di partenza*/


    // TODO da gestire quali sono le mail in entrata e quali in uscita
    // Risolto in teoria vedi Client.java

    // TODO DEN  bisogna bindare account a quello che ci sta scritto nella label accountDisplay

    /*Azione eseguita quando clicco sul pulsante delle mail in ingresso*/
   /* private void onClickInMail(MouseEvent mouseEvent) {
        // Pulisco la ListView
        mailList.getItems().clear();
        client = new Client("denis@example.com", jsonPath);
        for (Email email : client.getInMailProperty()) {
            mailList.getItems().add(email.getSender() + " - " + email.getObject());
        }
    }*/
    /*Azione eseguita quando clicco sul pulsante delle mail in uscita*/
    /*private void onClickOutMail(MouseEvent mouseEvent) {
        // Pulisco la ListView
        mailList.getItems().clear();
        client = new Client("denis@example.com", jsonPath);
        // Aggiunta delle email alla ListView - Visualizzazione di tutte le mail
        for (Email email : client.getOutMailProperty()) {
            mailList.getItems().add(email.getSender() + " - " + email.getObject());
        }

    }*/
}
