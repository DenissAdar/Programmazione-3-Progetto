package com.example.progettoprogrammazione.Model;

import com.example.progettoprogrammazione.Email;

import java.util.Objects;

public class Client {
    private String account; //esempio@gmail.com

    public Client(String email){
        this.account = email;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

/*
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
