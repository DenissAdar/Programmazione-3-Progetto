package com.example.progettoprogrammazione;
import java.io.Serializable;
import java.util.ArrayList;

// Creo i dati che sono dentro a "contenuto" del file json
public class Email implements Serializable {


    private String message;
    private String object;
    private String sender;
    private String receiver;
    private String date;
private String idMail;

    public Email(String sender, String receiver, String object,  String message, String date) {
        this.message = message;
        this.object = object;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getObject() {
        return object;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String s){
        this.receiver = s;
    }
    public String getDate(){return date;}

    public String visualizzaMail() {
        return "Email{" +
                "Sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", object='" + object + '\'' +
                ", Message='" + message + '\'' +
                ", Data='" + date + '\'' +
                '}';
    }
    @Override
    public String toString(){
        return object;
    }


/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(getMessage(), email.getMessage()) && Objects.equals(getObject(), email.getObject()) && Objects.equals(getSender(), email.getSender()) && Objects.equals(getRecevier(), email.getRecevier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getObject(), getSender(), getRecevier());

    }*/






}
