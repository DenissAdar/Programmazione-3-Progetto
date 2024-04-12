package com.example.progettoprogrammazione;

import javafx.application.Platform;
import javafx.beans.property.*;
import java.net.InetAddress;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;


public class Client {

    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;

    private String account;
    private ObjectProperty<String> accountProperty = new SimpleObjectProperty<>();



    private ListProperty<Email> MailProperty;

    private ObservableList<Email> MailContent;
    private SimpleStringProperty senderProperty;
    private SimpleStringProperty receiverProperty;
    private SimpleStringProperty objectProperty;
    private SimpleStringProperty messageProperty;
    private ArrayList<Email> emailList = new ArrayList<>();
    Thread t1;

    public Client(){

        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                socketUsername();
            }
        });

        t1.start();

        senderProperty = new SimpleStringProperty();
        receiverProperty = new SimpleStringProperty();
        objectProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();

        MailProperty = new SimpleListProperty<Email>(MailContent);
        MailContent = FXCollections.observableList(new LinkedList<>());
        MailContent.addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> change) {

            }
        });




    }

    public SimpleStringProperty getSenderProperty(){
        System.out.println("Valore di SenderProeprty" + senderProperty);
        return senderProperty;}
    public SimpleStringProperty getReceiverProperty(){return receiverProperty;}
    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}

    //LIST
    public ListProperty<Email> getMailProperty() {
        return MailProperty;
    }



    //todo MARIUS

    String errore="";
    public void setErroreInMittente(String s){
         this.errore = s;
    }
    public String getErroreInMittente(){
        return this.errore;
    }
    //todo MARIUS

    public void socketSendMail(Email email){
        try{
            String s;
            System.out.println("Inizio"+email.visualizzaMail());
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("send");
            outputStream.writeObject(email);
            //todo MARIUS
            //s = String.valueOf(inputStream.read());
            //setErroreInMittente("ciao");

        }catch(IOException  e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }
    public void socketDeleteMail(Email email){
        try{
            System.out.println("Inizio"+email.visualizzaMail());
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("delete");
            outputStream.writeObject(email);


        }catch(IOException  e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }

    public void socketInMail(){
        try{
            emailList.clear();
            System.out.println("----------------");
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("emailIn");
            inputStream = new ObjectInputStream(socket.getInputStream());
            emailList = (ArrayList<Email>) inputStream.readObject();
            Platform.runLater(()-> setMailProperty());



        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }
    public void setMailProperty(){
        MailProperty.clear();
        MailContent.clear();
        MailProperty.set(MailContent);

        if(MailContent.isEmpty()) {
            for (int i = 0; i < emailList.size(); i++) {
                System.out.println(":: " + emailList.get(i));
                MailContent.add(emailList.get(i));
            }
            MailProperty.set(MailContent);
            System.out.println("Prova: " + MailProperty);
        }
    }
    public void socketOutMail(){
        try{
            emailList.clear();
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject(this.getAccount());
            outputStream.writeObject("emailOut");
            inputStream = new ObjectInputStream(socket.getInputStream());
            emailList = (ArrayList<Email>) inputStream.readObject();
            Platform.runLater(()-> setMailProperty());

        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }
    public String getAccount() {
        return account;
    }

    //-----------
    public void setAccountProperty() {
        try {
            accountProperty.setValue((String)account);
        }catch (NullPointerException e){
            System.out.println("Trovato NullPointer Exception ");
        }
    }
    public ObjectProperty<String> getAccountProperty(){
        return accountProperty;
    }
    public void socketUsername() {
        try {
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream= new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject("");
            outputStream.writeObject("account");
            inputStream = new ObjectInputStream(socket.getInputStream());
            account = (String) inputStream.readObject();
            Platform.runLater( ()-> setAccountProperty());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }
    //-----------

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
            closeStreams();
        }catch(IOException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }
}


