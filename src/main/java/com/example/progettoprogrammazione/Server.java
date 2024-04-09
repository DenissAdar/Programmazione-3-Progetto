package com.example.progettoprogrammazione;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.io.File;


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
    private String[] accounts;
    //private static AtomicInteger uniqueId = new AtomicInteger(0);
    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";

    ServerSocket serverSocket;
    ThreadPoolExecutor executor;

    private ListProperty<String> logList; /*binding in sever controller*/
    private ObservableList<String> logListContent;

    //ArrayList<Email> inMail = new ArrayList<>();
    ArrayList<Email> outMail = new ArrayList<>();
    private String randomUser;


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

            serverSocket = new ServerSocket(6000);
            new Thread(new RunServer()).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ListProperty<String> getLogList(){
        return logList;
    }


    public void setAccountList(String[] accountList){
        this.accounts = accountList;
    }
    public String[] getAccountList(){return accounts;}
    //Decide randomicamente quale username dare ad un client appena creato
    class ThreadAccount implements Runnable{
        ObjectOutputStream out;
        Socket socket;

        // Metodo che seleziona un account in maniera randomica
        public String accountPicker()  {
            String[] accountList = new String[jsonCount()];
            int i=0;

            // Carico un array di account
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
            int randomNum = ThreadLocalRandom.current().nextInt(0, accountList.length);
            setAccountList(accountList);
            return accountList[randomNum];
        }

        public ThreadAccount(ObjectOutputStream out, Socket socket) {
            this.out = out;
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                randomUser = accountPicker();
                out.writeObject(randomUser);

                Platform.runLater(() -> logList.add(randomUser +" ha fatto l'accesso.")); /*loglist è l'elemento LOG dell'applicazione di sever lato grafico */
                socket.close(); /*todo da rivedere chiusura*/

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Metodo che gestisce le email in entrata
    class ThreadInMail implements Runnable{
        ObjectOutputStream out;
        String account;
        ArrayList<Email> emailList = new ArrayList<>();

        public ThreadInMail(ObjectOutputStream out, String account){
            this.out = out;
            this.account = account;
        }

        @Override
        public void run(){
            try{
                emailList = jSonReader("Entrata",account);
                System.out.println("Attenzione qua ingresso "+emailList);
                out.writeObject(emailList);
                out.flush();
                Platform.runLater(() -> logList.add("L'utente: " + randomUser + " ha richiesto le mail in ingresso"));
            }catch(IOException e){throw new RuntimeException(e);}
        }
    }

    // Metodo che gestisce le email in uscita
    class ThreadOutMail implements Runnable{
        ObjectOutputStream out;
        String account;
        ArrayList<Email> emailList = new ArrayList<>();

        public ThreadOutMail(ObjectOutputStream out,String account){
            this.out = out;
            this.account = account;
        }

        @Override
        public void run(){
            try{
                System.out.println("maillist prima di leggere : " + emailList);
                emailList = jSonReader("Uscita",account);
                System.out.println("maillist dopo la lettura : " + emailList);
                out.writeObject(emailList);
                out.flush();
                Platform.runLater(() -> logList.add("L'utente: " + randomUser + " ha richiesto le mail in uscita"));
            }catch(IOException e){throw new RuntimeException(e);}
        }
    }


    // Metodo che gestisce l'invio di una mail
    class ThreadSend implements Runnable{
        ObjectOutputStream out;
        ObjectInputStream in;
        String account;
        Email email;
        String[] accounts;
        boolean flag=false;

        public ThreadSend(ObjectInputStream in,ObjectOutputStream out,String account)
        {
            this.in = in;
            this.out = out;
            this.account = account;
        }

        @Override
        public void run(){
            try{
                email = (Email) in.readObject(); //Ho una Mail
                accounts = getAccountList(); //Ho un array di Account

                for(int i=0;i<accounts.length;i++){
                    if(email.getRecevier().equals(accounts[i])) flag=true;
                }
                if(flag){
                    Platform.runLater(() -> logList.add("L'utente: " + account + " ha mandato una mail a " + email.getRecevier()));
                    //Metodo che mi carica la mail nel json
                }
                else {
                    Platform.runLater(() -> logList.add("L'utente: " + account + " cerca di mandare un email all'utente " + email.getRecevier() + " che non esiste!"));

                }

            }catch(IOException | ClassNotFoundException e){throw new RuntimeException(e);}

        }
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

    // Metodo che gestisce la chiusura
    class ThreadExit implements Runnable{
        public ThreadExit(){}
        @Override
        public void run(){
            System.out.println("Sono nel ThreadExit");
            Platform.runLater(() -> logList.add(randomUser + " ha fatto il LOGOUT."));
        }
    }


    class RunServer implements Runnable{
        private Socket socket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        String account = "Si è connesso ";
        private final SimpleStringProperty accountLog = new SimpleStringProperty();

        private Socket incoming;
        private String action;

        public RunServer(){}

        @Override
        public void run() {
            try {
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5); /*serve a richimare i thread funzioni più avanti ---> executor.execute(new ThreadUser(out));*/

                while (true) {
                    // Si blocca finchè non riceve qualcosa, va avanti SOLO SE LO RICEVE
                    socket = serverSocket.accept();
                    // Crea un nuovo oggetto che legge oggetti dal flusso di input derivato dalla connessione socket (client)
                    openStreams();

                    // Chi sta comunicando con il server, chi sta richiedendo quell'azione
                    account = (String) inputStream.readObject();
                    // Le azioni che vengono svolte
                    action = (String) inputStream.readObject();


                    // In base all'azione si crea un metodo thread runnable utilizzando execute
                    switch (action) {
                        case "account":
                            executor.execute(new ThreadAccount(outputStream, socket));
                            break;
                        case "emailIn":
                            executor.execute(new ThreadInMail(outputStream,account));
                            break;
                        case "emailOut":
                            executor.execute(new ThreadOutMail(outputStream,account));
                            break;
                        case "send":
                            System.out.println("sono nel case send ");
                            executor.execute(new ThreadSend(inputStream, outputStream, account));
                            break;
                        case "sendAll":
                            //executor.execute(new ThreadSendAll(outputStream));
                            break;
                        case "delete":
                            //executor.execute(new ThreadDelete(outputStream));
                            break;
                        case "deleteAll":
                            //executor.execute(new ThreadDeleteAll(outputStream));
                            break;
                        case "exit":
                            System.out.println("sono nel case");
                            executor.execute(new ThreadExit());
                            break;

                        default:
                            System.out.println("default");
                            break;
                    }
                }
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


    /* -------------------------- GESTIONE JSON --------------------- ----- */


    // Metodo che conta quanti account ci sono nel file json
    public int jsonCount()
    {
        int count = 0;
        try {
            ObjectMapper countObjectMapper = new ObjectMapper();
            JsonNode rootNode = countObjectMapper.readTree(new File(jsonFilePath));

            // Itera sui nodi del file JSON
            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                count++;
                System.out.println(count);
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Metodo JsonReader che restituisce le mail in entrata o in uscita in base ad un valore che gli viene passato come parametro (ingresso,uscita)
    public synchronized ArrayList<Email>  jSonReader (String mailListType, String account){
        ArrayList<Email> inMail = new ArrayList<>();
        try {
            inMail.clear();
            ObjectMapper readObjectMapper = new ObjectMapper();
            JsonNode rootNode = readObjectMapper.readTree(new File(jsonFilePath));

            // Itera sui nodi del file JSON
            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                JsonNode contenutoNode = emailNode.get("content");

                if(Objects.equals(account,email)){
                    // Itera sui contenuti delle del nodo Email
                    for (JsonNode contentNode : contenutoNode) {
                        String sender = contentNode.get("from").asText();
                        String receiver = contentNode.get("to").asText();
                        String object = contentNode.get("object").asText();
                        String message = contentNode.get("text").asText();
                        String dateTime = contentNode.get("dateTime").asText();
                        if(mailListType.equals("Entrata")){
                            Email emailObj = new Email(sender, object,receiver, message, dateTime);
                            if(Objects.equals(account, receiver)) inMail.add(emailObj);
                        }
                        else if(mailListType.equals("Uscita")){
                            Email emailObj = new Email(sender, object,receiver, message, dateTime);
                            if(Objects.equals(account, sender)) inMail.add(emailObj);
                        }

                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return inMail;
    }
/*   public synchronized void jSonWriter(Email email){
        try{
            ObjectMapper writeObjectMapper = new ObjectMapper();
            JsonNode rootNode = writeObjectMapper.readTree(new File(jsonFilePath));

            for (JsonNode emailNode : rootNode) {
                String email = emailNode.get("email").asText();
                JsonNode contenutoNode = emailNode.get("content");

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }  */
}


