package com.example.progettoprogrammazione;

import com.example.progettoprogrammazione.Email;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
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
import java.util.Observable;
import java.util.List;
import java.util.Objects;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

// TODO Gaia - Dobbiamo iniziare a fare i thread per far funzionare il tutto
public class Client {

    Socket socket = null;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;
    private int id;
    private final int MAXATTEMPTS = 5;

    private String account;
    private ObjectProperty<String> accountProperty = new SimpleObjectProperty<>();
  /*  private SimpleListProperty<Email> inMailProperty;*/
    ArrayList<Email> inMailList = new ArrayList<>();


    private ListProperty<Email> inMail;
    private ListProperty<Email> outMail;
    private ObservableList<Email> inMailContent;
    private ObservableList<Email> outMailContent;

    private SimpleStringProperty senderProperty;
    private SimpleStringProperty receiverProperty;
    private SimpleStringProperty objectProperty;
    private SimpleStringProperty messageProperty;

    private ObjectInputStream in;

    Thread t1;

    //todo allora ho tolto account dal costruttore e lo vado a togliere anche dal controller quando lo definisco
    public Client(){

        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                socketUsername();
                //Crea metododi comunicazione per il pulsante inMail
                //TODO bindare al pulsante
                socketInMail();

            }
        });

        t1.start();

        //this.account = account;
        senderProperty = new SimpleStringProperty();
        receiverProperty = new SimpleStringProperty();
        objectProperty = new SimpleStringProperty();
        messageProperty = new SimpleStringProperty();

        inMail = new SimpleListProperty<>(inMailContent);
        inMailContent = FXCollections.observableList(new LinkedList<>());


        outMail = new SimpleListProperty<>(outMailContent);
        outMailContent = FXCollections.observableList(new LinkedList<>());

        inMailContent.addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> change) {

            }
        });

        outMailContent.addListener(new ListChangeListener<Email>() {
            @Override
            public void onChanged(Change<? extends Email> change) {

            }
        });


    }



        //CONNESSIONE SERVER---------------------------------------------------------------------------------------------------------------
        //todo vedi se si deve eliminare sta roba che qui non arriva mai
        private void connectToServer(String host, int port) throws IOException {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("[Client "+ getAccount() + "] Connesso");
            System.out.println("--------------------merda---------");
        }

        public void communicate(String host, int port){
            int attempts = 0;
            boolean success = false;
            while(attempts < MAXATTEMPTS && !success) {
                attempts += 1;
                System.out.println("[Client " + getAccount()+ "] Tentativo nr. " + attempts);
                success = tryCommunication(host, port);
                if(success)
                    continue;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    // stringproperty

        private boolean tryCommunication(String host, int port) {
            try {
                connectToServer(host, port);
                outputStream.writeObject(getAccount());
                outputStream.flush();
                Thread.sleep(5000);
                return true;
            } catch (ConnectException ce) {
                // nothing to be done
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                closeConnections();
            }
        }

    private void closeConnections() {
        if (socket != null) {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //CONNESSIONE SERVER---------------------------------------------------------------------------------------------------------------

  /*   */
    public SimpleStringProperty getSenderProperty(){return senderProperty;}
    public SimpleStringProperty getReceiverProperty(){return receiverProperty;}
    public SimpleStringProperty getObjectProperty(){return objectProperty;}
    public SimpleStringProperty getMessageProperty(){return messageProperty;}
    public ListProperty<Email> getInMailProperty() {
        return inMail;
    }
    //Aggiunto da DEN quando ho creato il metodo di controllo per l'inserimento in Lista
    public ListProperty<Email> getOutMailProperty() {
        return outMail;
    }

    // Metodo aggiorna le email in entrata o in uscita (fa il reload solo delle nuove) aggiorna in base a ciò che estrae dal server
    public void setInOutEmail(ArrayList<Email> email, String inOut){
        if(inOut == "in")
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    inMail.clear();
                    inMail.addAll(email);

                }
            });
        }
        else
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run() {
                    outMail.clear();
                    outMail.addAll(email);

                }
            });

        }



    }

    ArrayList<Email> emailList = new ArrayList<>();
    // Crea metodo di comunicazione in mail
    public void socketInMail(){
        try{
            socket = new Socket(InetAddress.getLocalHost(), 6000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject("");
            out.writeObject("emailIn");
            in = new ObjectInputStream(socket.getInputStream());
            emailList = (ArrayList<Email>) in.readObject();
            System.out.println("-----------Risultato di socketinMail-----"+emailList);

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
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject("");
            out.writeObject("account");
            in = new ObjectInputStream(socket.getInputStream());
            account = (String) in.readObject();
            System.out.println(account + "è connesso anche al client");
            Platform.runLater( ()-> setAccountProperty());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Non è stato possibile connettersi al server");
        }
    }


    public void setAccountProperty() {
        try {
            accountProperty.setValue((String)account);
        }catch (NullPointerException e){
            System.out.println("Trovato NullPointer Exception ");
        }
    }
    public ObjectProperty<String> giveAccountProperty(){
        return accountProperty;
    }

}
