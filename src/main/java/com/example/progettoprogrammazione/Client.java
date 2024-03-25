package com.example.progettoprogrammazione;

import com.example.progettoprogrammazione.Email;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;


public class Client {

    Socket socket = null;
    private String jsonFilePath;
    private ListProperty<Email> inMail;
    private ListProperty<Email> outMail;
    private ObservableList<Email> inMailContent;
    private ObservableList<Email> outMailContent;

    private String account;
    private SimpleStringProperty senderProperty;
    private SimpleStringProperty receiverProperty;
    private SimpleStringProperty objectProperty;
    private SimpleStringProperty messageProperty;

    public Client(String account, String JFilePath){
        this.account = account;
        this.jsonFilePath = JFilePath;

        senderProperty = new SimpleStringProperty();
        receiverProperty = new SimpleStringProperty();
        objectProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();

        inMail = new SimpleListProperty<>(inMailContent);
        inMailContent = FXCollections.observableList(new LinkedList<>());


        outMail = new SimpleListProperty<>(outMailContent);
        outMailContent = FXCollections.observableList(new LinkedList<>());

        inMailContent.addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> change) {

            }
        });

        outMailContent.addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> change) {

            }
        });


        // Legge il file JSON e aggiunge le email al client
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            // Itera sui nodi del file JSON
            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                JsonNode contenutoNode = emailNode.get("content");

                // Itera sui contenuti delle email
                for (JsonNode contentNode : contenutoNode) {
                    String sender = contentNode.get("from").asText();
                    String receiver = contentNode.get("to").asText();
                    String object = contentNode.get("object").asText();
                    String message = contentNode.get("text").asText();
                    String dateTime = contentNode.get("dateTime").asText();

                    // Crea un oggetto Email e lo aggiunge alla lista corretta
                    Email emailObj = new Email(sender, receiver, object, message, dateTime);

                    /* TODO - Ci dovrebbero essere una serie di if per controllare se il destinatario è l'account
                       TODO - che abbiamo aperto e che quindi sono le mail in entrata altrimenti saranno outMail */


                    //Aggiunto da DEN quando ho creato il metodo di controllo per l'inserimento in Lista
                    if(Objects.equals(this.account, sender)) outMail.add(emailObj);
                    else if(Objects.equals(this.account, receiver)) inMail.add(emailObj);

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
}
    public SimpleStringProperty getSenderProperty(){return senderProperty;}
    public SimpleStringProperty getReceiverProperty(){return receiverProperty;}
    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}
    public ListProperty<Email> getInMail() {
        return inMail;
    }
    //Aggiunto da DEN quando ho creato il metodo di controllo per l'inserimento in Lista
    public ListProperty<Email> getOutMail() {
        return outMail;
    }


    /*public void addEmail(Email EmailDaAggiungere){
        inMail.add(EmailDaAggiungere);
    }
    public void removeEmail(Email EmailDaEliminare){
        inMail.remove(EmailDaEliminare);
    }*/


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }



}
