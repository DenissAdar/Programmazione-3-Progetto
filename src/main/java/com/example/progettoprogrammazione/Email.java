package com.example.progettoprogrammazione;
import java.io.Serializable;
import java.util.Objects;

// Creo i dati che sono dentro a "contenuto" del file json
public class Email implements Serializable {


    private String message;
    private String object;
    private String sender;
    private String receiver;
    private String idMail;

    public Email(String sender, String object, String receiver, String message) {
        this.message = message;
        this.object = object;
        this.sender = sender;
        this.receiver = receiver;
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

    public String getRecevier() {
        return receiver;
    }

    @Override
    public String toString() {
        return "Email{" +
                "Sender='" + sender + '\'' +
                ", object='" + object + '\'' +
                ", receiver='" + receiver + '\'' +
                ", Message='" + message + '\'' +
                '}';
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

        Server Connesso
Server Connesso
Sono entrato in JsonReader , Valore di account: gaia@example.com
Valore di Receiver in jsondenis@example.com
Valore di Receiver in jsonmarius@example.com
Valore di Receiver in jsongaia@example.com
Attenzione qua ingresso [Email{Sender='marius@example.com', object='MARIUS', receiver='gaia@example.com', Message='TESTO3'}]
Server Connesso
Sono entrato in JsonReader , Valore di account: gaia@example.com
Valore di Receiver in jsondenis@example.com
Valore di Receiver in jsonmarius@example.com
Valore di Receiver in jsongaia@example.com
Attenzione qua uscita[Email{Sender='marius@example.com', object='MARIUS', receiver='gaia@example.com', Message='TESTO3'}, Email{Sender='gaia@example.com', object='GAIA', receiver='denis@example.com', Message='TESTO1'}, Email{Sender='gaia@example.com', object='GAIA2', receiver='marius@example.com', Message='TESTO2'}]






    }*/






}
