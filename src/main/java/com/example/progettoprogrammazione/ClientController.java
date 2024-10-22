package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private Label dataLable;
    private Client client;
    private String side;

    @FXML
    public void forwardBtn(){
        String oggetto = "FWD:" + oggettoTxt.textProperty().getValue();
        mittenteTxt.textProperty().set(client.getAccount());
        oggettoTxt.textProperty().set(oggetto);
        destinatarioTxt.clear();
        destinatarioTxt.setEditable(true);
        mailBodyTxt.setEditable(false);

        forwardBtn.setVisible(false);
        replyBtn.setVisible(false);
        replyAllBtn.setVisible(false);
        deleteBtn.setVisible(false);
        inviaBtn.setVisible(true);

    }
    @FXML
    public void replyMail(){
        String mittente = mittenteTxt.textProperty().getValue();
        String oggetto = "R:" + oggettoTxt.textProperty().getValue();

        newMailCreation();
        mittenteTxt.textProperty().set(accountDisplay.textProperty().getValue());
        destinatarioTxt.textProperty().set(mittente);
        oggettoTxt.textProperty().set(oggetto);
        oggettoTxt.setEditable(false);
        destinatarioTxt.setEditable(false);
        mittenteTxt.setEditable(false);

    }
    public void replyAllMail() {
        String oldSender = mittenteTxt.textProperty().getValue();
        String destinatari = destinatarioTxt.textProperty().getValue();
        String oggetto = "R-All:" + oggettoTxt.textProperty().getValue();
        destinatari = destinatari.replace(accountDisplay.textProperty().getValue(),oldSender);

        newMailCreation();
        mittenteTxt.textProperty().setValue(accountDisplay.textProperty().getValue());
        destinatarioTxt.textProperty().setValue(destinatari);
        oggettoTxt.textProperty().setValue(oggetto);
        oggettoTxt.setEditable(false);
        destinatarioTxt.setEditable(false);
        mittenteTxt.setEditable(false);
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
        clearFields();
        mittenteTxt.textProperty().set(client.getAccount());

        mittenteTxt.setEditable(false);
        destinatarioTxt.setEditable(true);
        oggettoTxt.setEditable(true);
        mailBodyTxt.setEditable(true);
        dataTxt.setEditable(true);

        replyBtn.setVisible(false);

        setVisibility(false);
        inviaBtn.setVisible(true);

    }
    public void clearFields(){
        mittenteTxt.clear();
        destinatarioTxt.clear();
        oggettoTxt.clear();
        dataTxt.clear();
        mailBodyTxt.clear();
        setVisibility(false);
    }
    @FXML
    public void showInMail(){
        client.socketInMail();
        setVisibility(true);
        inviaBtn.setVisible(false);
        side = "in";
        mailList.itemsProperty().bind(client.getInMailProperty());
        clearFields();


    }
    @FXML
    public void showOutMail(){
        replyBtn.setVisible(false);
        replyAllBtn.setVisible(false);
        client.socketOutMail();
        side = "out";
        mailList.itemsProperty().bind(client.getOutMailProperty());
        clearFields();

    }
    @FXML
    public void displayMail(){
        Email selectedMail  = mailList.getSelectionModel().getSelectedItem();
        if(selectedMail == null){
            client.setError("Selezionata mail inesistente");
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
            if(selectedMail.getSender().equals(accountDisplay.textProperty().getValue())){
                replyBtn.setVisible(false);
                replyAllBtn.setVisible(false);
            }
            if(!destinatarioTxt.textProperty().getValue().contains(","))
                replyAllBtn.setVisible(false);
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
        Email e;


        if(oggettoTxt.textProperty().getValue().isEmpty())
                oggettoTxt.textProperty().set("(No Subject)");
        e = new Email(mittenteTxt.textProperty().getValue(), destinatarioTxt.textProperty().getValue() , oggettoTxt.textProperty().getValue() , mailBodyTxt.textProperty().getValue(), dataTxt.textProperty().getValue());

        clearFields();
        client.socketSendMail(e);
    }
    @FXML
    public void deleteMail(){
        Email em = new Email(mittenteTxt.textProperty().getValue(), destinatarioTxt.textProperty().getValue(),
                oggettoTxt.textProperty().getValue(), mailBodyTxt.textProperty().getValue(), dataTxt.textProperty().getValue());
        clearFields();

        client.socketDeleteMail(em,side);

    }
    public void init(){
        client = new Client();
        setVisibility(false);

        side = "in";

        accountDisplay.textProperty().bind(client.getAccountProperty()) ;
        connectionNotification.textProperty().bind(client.getErrorLabelProperty());
        mailList.itemsProperty().bind(client.getInMailProperty());



    }


}
