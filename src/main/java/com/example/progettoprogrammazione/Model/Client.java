package com.example.progettoprogrammazione.Model;
import com.example.progettoprogrammazione.Email;
import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private List<Email> inMail ;
    private String account;
    private SimpleStringProperty mittenteProperty;

    private SimpleStringProperty destinatarioProperty;

    private SimpleStringProperty oggettoProperty;

    private SimpleStringProperty messageProperty;

    public Client(String a){

        inMail = new ArrayList<Email>();

        this.account = a;
        mittenteProperty = new SimpleStringProperty();
        destinatarioProperty = new SimpleStringProperty();
        oggettoProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();
    }
    public SimpleStringProperty getMittenteProperty(){return mittenteProperty;}
    public SimpleStringProperty getDestinatarioProperty(){return destinatarioProperty;}
    public SimpleStringProperty getOggettoProperty(){return oggettoProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}


    public void aggiungiEmail(Email EmailDaAggiungere){
        inMail.add(EmailDaAggiungere);
    }
    public void rimuoviEmail(Email EmailDaEliminare){
        inMail.remove(EmailDaEliminare);
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
