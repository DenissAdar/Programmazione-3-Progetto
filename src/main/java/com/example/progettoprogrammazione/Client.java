package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.beans.property.*;

import java.net.InetAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @Authors: Deniss,Marius,Gaia
 */


public class Client{

    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;

    private String account = "";

    private ObjectProperty<String> label = new SimpleObjectProperty<>();

    private ObjectProperty<String> accountProperty = new SimpleObjectProperty<>();

    private SimpleStringProperty senderProperty;
    private SimpleStringProperty receiverProperty;
    private SimpleStringProperty objectProperty;
    private SimpleStringProperty messageProperty;
    private ArrayList<Email> inMailList = new ArrayList<>();
    private ArrayList<Email> outMailList = new ArrayList<>();
    private ArrayList<String> accounts = new ArrayList<>();
    private ArrayList<Email> inCurrentEmailList = new ArrayList<>();
    private ArrayList<Email> outCurrentEmailList = new ArrayList<>();
    Thread t1;

    private boolean firstConnection = true;
    private ListProperty<Email> inbox;
    private ObservableList<Email> inboxContent;
    private ListProperty<Email> outbox;
    private ObservableList<Email> outboxContent;

    public Client(){

        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>(inboxContent);

        this.outboxContent = FXCollections.observableList(new LinkedList<>());
        this.outbox = new SimpleListProperty<>(outboxContent);

        senderProperty = new SimpleStringProperty();
        receiverProperty = new SimpleStringProperty();
        objectProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();


        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try{
                        while(account.isEmpty()){
                            socketUsername();
                        }
                        socketInMail();
                        socketOutMail();
                       Thread.sleep(10000);

                    }catch (InterruptedException e) {
                        System.out.println("Errore thread costruttore client");
                    }

                }

            }
        });
        t1.setDaemon(true);
        t1.start();
    }



    public SimpleStringProperty getSenderProperty(){
        System.out.println("Valore di SenderProeprty" + senderProperty);
        return senderProperty;
    }

    public void setSenderProperty(String s){senderProperty.set(s);}
    public SimpleStringProperty getReceiverProperty(){return receiverProperty;}
    public void setReceiverProperty(String s){receiverProperty.set(s);}

    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public void setObjectProperty(String s){objectProperty.set(s);}

    public SimpleStringProperty getMessageProperty(){return messageProperty;}
    public void setMessageProperty(String s){messageProperty.set(s);}

    //TODO CI STO LAVORANDO E' LA LABEL di errore
    public ObjectProperty<String> getErrorLabelProperty(){
        return label;
    }
    //LIST
    public ListProperty<Email> getInMailProperty() {
        return inbox;
    }
    public ListProperty<Email> getOutMailProperty() {
        return outbox;
    }

    public void socketSendMail(Email email){
        try{
            Email e = email;
            System.out.println("Inizio "+email.visualizzaMail());
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            if(Objects.equals(e.getReceiver(), "")){
                setError("Mail non mandata perchè non c'è il Destinatario");
            }else   setError("");
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("send");
            outputStream.writeObject(email);
            socket.close();

        }catch(IOException  e) {
            System.out.println("Non è stato possibile connettersi al server");
            setError("Errore nella richiesta di Mandare una Mail, Server non connesso");
            //TODO comunicarlo alla label
        }
    }
    public void socketDeleteMail(Email email,String side){
        try{
            System.out.println("Inizio "+email.visualizzaMail());
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("delete");
            outputStream.writeObject(email);
            setError("");
            socket.close();

            if(side.equals("in")){
                inCurrentEmailList.remove(email);
                inbox.clear();
                inbox.addAll(inCurrentEmailList);
            }else if (side.equals("out")){
                outCurrentEmailList.remove(email);
                outbox.clear();
                outbox.addAll(outCurrentEmailList);
            }

        }catch(IOException  e) {
            System.out.println("Non è stato possibile connettersi al server");
            setError("Errore nella richiesta di Eliminazione della Mail, Server non connesso");
        }
    }

    public void socketInMail(){
        try{
            inMailList.clear();
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("emailIn");
            outputStream.writeObject(inCurrentEmailList);
            inputStream = new ObjectInputStream(socket.getInputStream());

            inMailList = (ArrayList<Email>) inputStream.readObject();
            if(inMailList.size() > 0)
                Platform.runLater(()-> setMailProperty("in",inMailList));
            socket.close();
        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
            setError("Errore nella richiesta delle Mail in Entrata, Server non connesso");
        }
    }
    public void socketOutMail(){
        try{
            outMailList.clear();
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("emailOut");
            outputStream.writeObject(outCurrentEmailList);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outMailList = (ArrayList<Email>) inputStream.readObject();
            if(outMailList.size() > 0)
                Platform.runLater(()-> setMailProperty("out",outMailList));

            socket.close();

        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
            setError("Errore nella richiesta delle Mail in Uscita, Server non connesso");
        }
    }
    public void setMailProperty(String side, ArrayList<Email> ml){
        if(side.equals("out")){
            outCurrentEmailList.addAll(ml);
            outbox.clear();
            outbox.addAll(outCurrentEmailList);
            Collections.reverse(outbox);
        }else {
            inCurrentEmailList.addAll(ml);
            inbox.clear();
            inbox.addAll(inCurrentEmailList);
            Collections.reverse(inbox);
            if (firstConnection)
                firstConnection = false;
            else {
                setError("Hai delle nuove mail da leggere");
                System.out.println("Ho settato l'errore");
            }
        }


    }
    public String getAccount() {
        return account;
    }


    public void setAccountProperty() {
        try {
            accountProperty.setValue(account);
        }catch (NullPointerException e){
            System.out.println("Trovato NullPointer Exception ");
        }
    }
    public ObjectProperty<String> getAccountProperty(){
        return accountProperty;
    }
    public boolean socketUsername(){
        try {
            System.out.println("Sono in socketUser");
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream= new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject("");
            outputStream.writeObject("account");
            inputStream = new ObjectInputStream(socket.getInputStream());
            account = (String) inputStream.readObject();
            if(Objects.equals(account, "")){
                System.out.println("Ciao");
                Platform.exit();
                socket.close();
                return false;
            }
            else{
                Platform.runLater( ()-> setAccountProperty());
                setError("");
                socket.close();
                return true;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server in socket username");
            setError("Errore di associazione di user, Server non connesso");
            return false;
        }

}
    public void openStreams() throws IOException {
        System.out.println("Server Connesso");
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
    }
    public void closeStreams() {
        try {
            if(inputStream != null) {
                inputStream.close();
            }

            if(outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exit(){
        try{
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("exit");
            inputStream = new ObjectInputStream(socket.getInputStream());
            boolean response = (boolean) inputStream.readObject();
             if (response)
                 closeStreams();
            socket.close();
        }catch(IOException e) {
            System.out.println("Non è stato possibile connettersi al server");
        } catch (ClassNotFoundException e) {
            System.out.println("Non è stato possibile chiudere");

        }
    }
    public void setError(String error){
        Platform.runLater(() -> label.set(error));

    }
}


