package com.example.progettoprogrammazione.Model;

import java.io.Serializable;
import java.util.Objects;

// Creo i dati che sono dentro a "contenuto" del file json
public class Email implements Serializable {

    private String message;
    private String object;
    private String sender;
    private String recevier;

    public Email(String message, String object, String sender, String recevier) {
        this.message = message;
        this.object = object;
        this.sender = sender;
        this.recevier = recevier;
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
        return recevier;
    }

    @Override
    public String toString() {
        return "Email{" +
                "message='" + message + '\'' +
                ", object='" + object + '\'' +
                ", sender='" + sender + '\'' +
                ", recevier='" + recevier + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(getMessage(), email.getMessage()) && Objects.equals(getObject(), email.getObject()) && Objects.equals(getSender(), email.getSender()) && Objects.equals(getRecevier(), email.getRecevier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessage(), getObject(), getSender(), getRecevier());
    }
}
