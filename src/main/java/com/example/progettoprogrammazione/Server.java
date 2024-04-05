package com.example.progettoprogrammazione;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.concurrent.ThreadLocalRandom;


public class Server {

    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";

    ServerSocket serverSocket;
    ThreadPoolExecutor executor;

    private ListProperty<String> logList; /*binding in sever controller*/
    private ObservableList<String> logListContent;

    ArrayList<Email> inMail = new ArrayList<>();
    ArrayList<Email> outMail = new ArrayList<>();


    public Server(){
        try {
            this.logListContent = FXCollections.observableList(new LinkedList<>());
            this.logList = new SimpleListProperty<>();
            this.logList.set(logListContent);
            logListContent.addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> change) {

                }
            });


            /*TODO: CREARE E GESTIRE HASHMAP PER LA MUTUA ESCLUSIONE DEL FILE JSON DELLE MAIL*/
            serverSocket = new ServerSocket(6000);
            new Thread(new RunServer()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ListProperty<String> getLogList(){
        return logList;
    }

    //Conessione---------------------------------------------------------------------------------
    /*public void listen(int port) {

        try {
            int id = 1;
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("ascolto");
                Socket incoming = serverSocket.accept();
                //Classe dei runnable thread---
                Runnable r = new RunServer(incoming,id);
                new Thread(r).start();
                id++;
                //serveClient(serverSocket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    //Conessione---------------------------------------------------------------------------------

   /* public void jSonReader (){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            // Itera sui nodi del file JSON
            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                JsonNode contenutoNode = emailNode.get("content");

                // Itera sui contenuti delle email
                for (JsonNode contentNode : contenutoNode) {
                    String sender = contentNode.get("from").asText();
                    String receiver = contentNode.get("to").asText();
                    String object = contentNode.get("object").asText();
                    String message = contentNode.get("text").asText();
                    String dateTime = contentNode.get("dateTime").asText();

                    // Crea un oggetto Email e lo aggiunge alla lista corretta
                    Email emailObj = new Email(sender,  object,receiver, message);

                    //Aggiunto da DEN quando ho creato il metodo di controllo per l'inserimento in Lista
                    //if(Objects.equals(this.account, sender))       outMail.add(emailObj);
                    //else if(Objects.equals(this.account, receiver)) inMail.add(emailObj);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Decide randomicamente quale username dare ad un client appena creato*/
    class ThreadAccount implements Runnable{
        ObjectOutputStream out;
        public String accountPicker()  {
            //todo: cambiare 3 con il numero degli elementi nell'array
            String[] accountList = new String[2];
            int i=0;
            //carico un array di account
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
                for(JsonNode emailNode: rootNode){
                    accountList[i] = emailNode.get("email").asText();
                    i++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int randomNum = ThreadLocalRandom.current().nextInt(0, accountList.length );
            return accountList[randomNum];
        }
        public ThreadAccount(ObjectOutputStream out) {
            this.out = out;
        }
        @Override
        public void run() {
            try {
                /*todo creare metodo per ottenere randomicamente un account e restituirlo come se fosse prova_user qua sotto*/
                //todo den: fatto
                String prova_user = accountPicker();
                out.writeObject(prova_user);

                Platform.runLater(() -> logList.add(prova_user+" ha fatto l'accesso.")); /*loglist è l'elemento LOG dell'applicazione di sever lato grafico */
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //TODO Fai le 6 classi Thread qui sotto
    class ThreadInMail implements Runnable{
        ObjectOutputStream out;
        public ThreadInMail(ObjectOutputStream out){this.out=out;}
        @Override
        public void run(){
            Platform.runLater(() -> logList.add("Mail In Entrata ricevuta da"));
        }
    }
    class ThreadOutMail implements Runnable{
        public ThreadOutMail(){}
        @Override
        public void run(){}
    }
    class ThreadSend implements Runnable{
        public ThreadSend(){}
        @Override
        public void run(){}
    }
    class ThreadSendAll implements Runnable{
        public ThreadSendAll(){}
        @Override
        public void run(){}
    }
    class ThreadDelete implements Runnable{
        public ThreadDelete(){}
        @Override
        public void run(){}
    }
    class ThreadDeleteAll implements Runnable{
        public ThreadDeleteAll(){}
        @Override
        public void run(){}
    }

    class RunServer implements Runnable{
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        String account = "Si è connesso ";
        private final SimpleStringProperty accountLog = new SimpleStringProperty();

        private Socket incoming;
        private int id;
        private String action;

        public RunServer(){}

        @Override
        public void run() {

            try {
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5); /*serve a richimare i thread funzioni più avanti ---> executor.execute(new ThreadUser(out));*/

                /*
                    socket = s.accept();
                    System.out.println("Accettato socket");
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    in = new ObjectInputStream(socket.getInputStream());
                    username = (String) in.readObject();
                    String command = (String) in.readObject();
                    switch (command)...
                */


                while (true) {
                    // Si blocca finchè non riceve qualcosa, va avanti SOLO SE LO RICEVE
                    socket = serverSocket.accept();
                    System.out.println(socket);
                    System.out.println(socket.getPort());

                    // Crea un nuovo oggetto che legge oggetti dal flusso di input derivato dalla connessione socket (client)
                    openStreams();

                    // Chi sta comunicando con il server, chi sta richiedendo quell'azione
                    account = (String) inputStream.readObject();

                    // Le azioni che vengono svolte
                    action = (String) inputStream.readObject();

                    // In base all'azione si crea un metodo thread runnable utilizzando execute
                    switch (action) {
                        case "account":
                            executor.execute(new ThreadAccount(outputStream));
                            break;
                        case "emailIn":
                            break;
                        case "emailOut":
                            break;
                        case "send":
                            break;
                        case "sendAll":
                            break;
                        case "delete":
                            break;
                        case "deleteAll":
                            break;

                        default:
                            System.out.println("default");
                            break;
                    }

                    // Chiudo connessione e socket
                    //socket.close();
                }


                /*account += (String) inputStream.readObject();
                System.out.println(account);
                accountLog.set(account);*/

            /*jSonReader();
            contenuto.add(inMail);
            contenuto.add(outMail);
            System.out.println(contenuto);
            //mando sullo stream i contenuti letti dal json
            outputStream.writeObject( contenuto);
            System.out.println(account);*/

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                closeStreams();
            }
        }

        public void openStreams() throws IOException {

            System.out.println("Server Connesso");

            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Ripulisce lo stream
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
} //Fine classe Server-----------------------------------


