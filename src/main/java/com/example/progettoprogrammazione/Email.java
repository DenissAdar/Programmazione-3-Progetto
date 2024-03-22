package com.example.progettoprogrammazione;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

// Creo i dati che sono dentro a "contenuto" del file json
public class Email implements Serializable {


    private String message;
    private String object;
    private String sender;
    private String receiver;
    private String date;

    public Email(String sender, String receiver, String object, String message, String date) {
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

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Email{" +
                "message='" + message + '\'' +
                ", object='" + object + '\'' +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", date='" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(getMessage(), email.getMessage()) && Objects.equals(getObject(), email.getObject()) && Objects.equals(getSender(), email.getSender()) && Objects.equals(getReceiver(), email.getReceiver());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getObject(), getSender(), getReceiver());
    }
}
