package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Authors: Deniss,Marius,Gaia
 */

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

    public void setNewMailProperties(String s){
        client.setReceiverProperty(s);
        client.setObjectProperty(s);
        client.setMessageProperty(s);
        System.out.println(client.getSenderProperty());
        System.out.println(client.getReceiverProperty());
        System.out.println(client.getObjectProperty());
        System.out.println(client.getMessageProperty());
    }
    @FXML
    public void forwardBtn() throws IOException {
       /* Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();*/
        //client.getForwardAccounts();
    }
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
        mittenteTxt.textProperty().bind(client.getAccountProperty());
        client.setSenderProperty(mittenteTxt.textProperty().get());
        mittenteTxt.setEditable(false);
        destinatarioTxt.setEditable(true);
        oggettoTxt.setEditable(true);
        mailBodyTxt.setEditable(true);
        dataTxt.setEditable(true);

        setNewMailProperties("");
        setVisibility(false);
        inviaBtn.setVisible(true);

    }
    public void clearFields(){
        destinatarioTxt.clear();
        oggettoTxt.clear();
        mailBodyTxt.clear();
        setVisibility(false);
    }
    @FXML
    public void showInMail(){
        client.socketInMail();
    }

    @FXML
    public void showOutMail(){client.socketOutMail();}

    @FXML
    public void displayMail(){
        // TODO Manca bindare la data che sono gay e non ho molto sbatti di f
        Email selectedMail  = mailList.getSelectionModel().getSelectedItem();
        if(selectedMail == null){
            System.out.println("Selezionato mail inesistente");
        }else {
            dataLable.setVisible(true);
            dataTxt.setVisible(true);

            client.setSenderProperty(selectedMail.getSender());
            mittenteTxt.textProperty().bind(client.getSenderProperty());


            client.setReceiverProperty(selectedMail.getReceiver());
            destinatarioTxt.textProperty().bind(client.getReceiverProperty());

            client.setObjectProperty(selectedMail.getObject());
            oggettoTxt.textProperty().bind(client.getObjectProperty());


            client.setMessageProperty(selectedMail.getMessage());
            mailBodyTxt.textProperty().bind(client.getMessageProperty());

            dataTxt.textProperty().setValue(selectedMail.getDate());

            mittenteTxt.setEditable(false);
            destinatarioTxt.setEditable(false);
            oggettoTxt.setEditable(false);
            mailBodyTxt.setEditable(false);
            dataTxt.setEditable(false);

            setVisibility(true);
            inviaBtn.setVisible(false);
        }


    }
    @FXML
    public void handleWindowClose(){
        client.exit();
        Platform.exit();
    }
    @FXML
    public String getDate(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(currentDate);
        return currentDateTime;
    }
    @FXML
    public void sendMail(){

        dataTxt.textProperty().setValue(getDate());
        Email e = new Email(mittenteTxt.textProperty().getValue(), destinatarioTxt.textProperty().getValue() , oggettoTxt.textProperty().getValue() , mailBodyTxt.textProperty().getValue(), dataTxt.textProperty().getValue());
        System.out.println("LA NUOVA MAIL CREATA : " + e.visualizzaMail());
        clearFields();
        client.socketSendMail(e);
        //todo MARIUS
        //connectionNotification.textProperty().set(client.getErroreInMittente());
    }
    @FXML
    public void deleteMail(){
        Email em = new Email(mittenteTxt.textProperty().getValue(), destinatarioTxt.textProperty().getValue(),
                oggettoTxt.textProperty().getValue(), mailBodyTxt.textProperty().getValue(), dataTxt.textProperty().getValue()
        );
        clearFields();
        client.socketDeleteMail(em);
    }
    public void init(){
        connectionNotification.setVisible(false);
        client = new Client();
        setVisibility(false);

        accountDisplay.textProperty().bind(client.getAccountProperty()) ;
        mailList.itemsProperty().bind(client.getMailProperty());

    }


}
