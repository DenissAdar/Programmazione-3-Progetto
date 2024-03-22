package com.example.progettoprogrammazione;

import com.example.progettoprogrammazione.Email;
import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Client {

    private List<Email> inMail;
    private List<Email> outMail;
    private String account;
    private SimpleStringProperty senderProperty;

    private SimpleStringProperty recevierProperty;

    private SimpleStringProperty objectProperty;

    private SimpleStringProperty messageProperty;

    public Client(String account, String jsonFilePath){

        this.account = account;
        senderProperty = new SimpleStringProperty();
        recevierProperty = new SimpleStringProperty();
        objectProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();
        inMail = new ArrayList<>();

        // Leggi il file JSON e aggiungi le email al client
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("src/main/java/com/example/progettoprogrammazione/accounts/account.json"));

            // Itera sui nodi del file JSON
            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                JsonNode contenutoNode = emailNode.get("content");

                // Itera sui contenuti delle email
                for (JsonNode contentNode : contenutoNode) {
                    String sender = contentNode.get("from").asText();
                    String recevier = contentNode.get("to").asText();
                    String object = contentNode.get("object").asText();
                    String message = contentNode.get("text").asText();
                    String dateTime = contentNode.get("dateTime").asText();

                    // Crea un oggetto Email e aggiungilo alla lista inMail
                    Email emailObj = new Email(sender, recevier, object, message, dateTime);

                    /* TODO - Ci dovrebbero essere una serie di if per controllare se il destinatario è l'account
                       TODO - che abbiamo aperto e che quindi sono le mail in entrata altrimenti saranno outMail */
                    inMail.add(emailObj); // Se è una mail in entrata
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
}
    public SimpleStringProperty getSenderProperty(){return senderProperty;}
    public SimpleStringProperty getRecevierProperty(){return recevierProperty;}
    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}
    public List<Email> getInMail() {
        return inMail;
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
