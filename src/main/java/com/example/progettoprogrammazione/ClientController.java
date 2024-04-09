package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Button inviaBtn;

    @FXML
    private TextArea mailBodyTxt;

    @FXML
    private ListView<Email> mailList;

    @FXML
    private TextField mittenteTxt;

    @FXML
    private Button newMailBtn;

    @FXML
    private TextField oggettoTxt;

    @FXML
    private Button outMailBtn;
    @FXML
    private Button inMailBtn;
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

    public void setVisibility(Boolean flag){
        inviaBtn.setVisible(flag);
        replyBtn.setVisible(flag);
        replyAllBtn.setVisible(flag);
        forwardBtn.setVisible(flag);
        deleteBtn.setVisible(flag);

        dataTxt.setVisible(flag);
        dataLable.setVisible(flag);
    }



    @FXML
    public void newMailCreation(){
        //Da bindare il mittente con la property giusta
        mittenteTxt.textProperty().setValue(accountDisplay.textProperty().getValue());
        mittenteTxt.setEditable(false);
        destinatarioTxt.clear();
        oggettoTxt.clear();
        mailBodyTxt.clear();
        setVisibility(false);
        inviaBtn.setVisible(true);
        destinatarioTxt.setEditable(true);
        oggettoTxt.setEditable(true);
        mailBodyTxt.setEditable(true);
        dataTxt.setEditable(true);
    }
    @FXML
    public void showInMail(){
        client.socketInMail();
    }

    @FXML
    public void showOutMail(){client.socketOutMail();}

    @FXML
    public void displayMail(){

        Email selectedMail = mailList.getSelectionModel().getSelectedItem();
        dataLable.setVisible(true);
        dataTxt.setVisible(true);

        mittenteTxt.textProperty().setValue(selectedMail.getSender()) ;
        destinatarioTxt.textProperty().setValue(selectedMail.getReceiver())  ;

        oggettoTxt.textProperty().setValue(selectedMail.getObject()) ;
        mailBodyTxt.textProperty().set(selectedMail.getMessage());
        dataTxt.textProperty().setValue(selectedMail.getDate());

        mittenteTxt.setEditable(false);
        destinatarioTxt.setEditable(false);
        oggettoTxt.setEditable(false);
        mailBodyTxt.setEditable(false);
        dataTxt.setEditable(false);

        setVisibility(true);
        inviaBtn.setVisible(false);
    }
    @FXML
    public void handleWindowClose(){
        client.exit();
        Platform.exit();
    }
    @FXML
    public void sendMail(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(currentDate);
        dataTxt.textProperty().setValue(currentDateTime);
        Email e = new Email(mittenteTxt.textProperty().getValue(), destinatarioTxt.textProperty().getValue() , oggettoTxt.textProperty().getValue() , mailBodyTxt.textProperty().getValue(), dataTxt.textProperty().getValue());
        System.out.println("LA NUOVA MAIL CREATA : " + e.visualizzaMail());

        client.socketSendMail(e);
    }
    public void init(){
        connectionNotification.setVisible(false);
        client = new Client();
        setVisibility(false);

        accountDisplay.textProperty().bind(client.getAccountProperty()) ;
        mailList.itemsProperty().bind(client.getMailProperty());

    }


}
