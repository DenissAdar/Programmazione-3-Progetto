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


    @FXML
    public void forwardBtn() throws IOException {
       /* Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();*/
        //client.getForwardAccounts();
    }
    @FXML
    public void replyMail(){
        String mittente = mittenteTxt.textProperty().getValue();
        String destinatario = destinatarioTxt.textProperty().getValue();
        newMailCreation();
        mittenteTxt.textProperty().set(destinatario);
        destinatarioTxt.textProperty().set(mittente);
        oggettoTxt.clear();



    }
    public void setVisibility(Boolean flag){

        inviaBtn.setVisible(flag);
       // replyBtn.setVisible(flag);
        replyAllBtn.setVisible(flag);
        forwardBtn.setVisible(flag);
        deleteBtn.setVisible(flag);

        dataTxt.setVisible(flag);
        dataLable.setVisible(flag);
    }



    @FXML
    public void newMailCreation(){

        mittenteTxt.textProperty().set(client.getAccount());
        client.setSenderProperty(mittenteTxt.textProperty().get());

        mittenteTxt.setEditable(false);
        destinatarioTxt.setEditable(true);
        oggettoTxt.setEditable(true);
        mailBodyTxt.setEditable(true);
        dataTxt.setEditable(true);
        clearFields();
        replyBtn.setVisible(false);

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
        replyBtn.setVisible(true);

    }

    @FXML
    public void showOutMail(){client.socketOutMail();
        replyBtn.setVisible(false);
    }

    @FXML
    public void displayMail(){
        // TODO Manca bindare la data che sono gay e non ho molto sbatti di f
        Email selectedMail  = mailList.getSelectionModel().getSelectedItem();
        if(selectedMail == null){
            System.out.println("Selezionato mail inesistente");
        }else {

            dataLable.setVisible(true);
            dataTxt.setVisible(true);

            mittenteTxt.textProperty().setValue(selectedMail.getSender());

            destinatarioTxt.textProperty().set(selectedMail.getReceiver());

            oggettoTxt.textProperty().set(selectedMail.getObject());

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
        replyBtn.setVisible(false);


        accountDisplay.textProperty().bind(client.getAccountProperty()) ;
        mailList.itemsProperty().bind(client.getMailProperty());

    }


}
