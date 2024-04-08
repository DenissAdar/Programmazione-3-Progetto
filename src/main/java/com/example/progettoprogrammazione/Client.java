package com.example.progettoprogrammazione;

import com.example.progettoprogrammazione.Email;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import java.net.InetAddress;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

// TODO Gaia - Dobbiamo iniziare a fare i thread per far funzionare il tutto
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

    //todo allora ho tolto account dal costruttore e lo vado a togliere anche dal controller quando lo definisco
    public Client(){

        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                socketUsername();
                //Crea metododi comunicazione per il pulsante inMail
                //TODO bindare al pulsante
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
  /*   */
    public SimpleStringProperty getSenderProperty(){return senderProperty;}
    public SimpleStringProperty getReceiverProperty(){return receiverProperty;}
    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}
    public ListProperty<Email> getMailProperty() {
        return MailProperty;
    }

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


    // Crea metodo di comunicazione in mail
    public void socketInMail(){

        try{
            emailList.clear();
            System.out.println("----------------");
           socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject("");
            outputStream.writeObject("emailIn");
            inputStream = new ObjectInputStream(socket.getInputStream());
            emailList = (ArrayList<Email>) inputStream.readObject();
            Platform.runLater(()-> setMailProperty());



        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }


    public void setMailProperty(){
        //TODO ATTENZIONE!!!!
        // le due righe successive riescono a ripulire la listview, il problema e' che quando si ricarica si ricaricano anche le mail vecchie
        // (il che e' diverso dal fatto che le mail si aggiungono alla listview che non si ripulisce )
        MailProperty.clear();
        MailContent.clear();
        MailProperty.set(MailContent);
        System.out.println("EmailList quando sono entrano in setMailProperty " + emailList);

        if(MailContent.isEmpty()) {
            for (int i = 0; i < emailList.size(); i++) {
                System.out.println(":: " + emailList.get(i));
                MailContent.add(emailList.get(i));
            }
            MailProperty.set(MailContent);
            System.out.println("Prova: " + MailProperty);
        }
        System.out.println("EmailList quando sto per uscire da setMailProeprty" + emailList);
    }



    public void socketOutMail(){
        try{
            emailList.clear();
            System.out.println("EmailList all'inizio di socket Out"+emailList);
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            outputStream.writeObject("");
            outputStream.writeObject("emailOut");
            inputStream = new ObjectInputStream(socket.getInputStream());
            emailList = (ArrayList<Email>) inputStream.readObject();
            System.out.println("EmaiList dopo che ho caricato le cose dal server " + emailList);
            Platform.runLater(()-> setMailProperty());

        }catch(IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }



    public String getAccount() {
        return account;
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
            System.out.println(account + "è connesso anche al client");
            Platform.runLater( ()-> setAccountProperty());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
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
}


