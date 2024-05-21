package com.example.progettoprogrammazione;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Authors: Deniss,Marius,Gaia
 */
public class Server {

    ArrayList<String> accounts = new ArrayList<>();
    ArrayList<String> loggedAccounts = new ArrayList<>();

    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";
    ServerSocket serverSocket;
    ThreadPoolExecutor executor;
    private ListProperty<String> logList; /*binding in sever controller*/
    private ObservableList<String> logListContent;

    private Socket socket;
    private ObjectInputStream socketInputStream;
    private ObjectOutputStream socketOutputStream;

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
    public void setAccountList(){
        //lista = new String[jsonCount()];
        int i=0;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            for(JsonNode emailNode: rootNode){
                accounts.add(emailNode.get("email").asText());
                //lista[i] = emailNode.get("email").asText();
                //i++;
            }

          // return lista;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Decide randomicamente quale username dare ad un client appena creato
    class ThreadAccount implements Runnable{
        ObjectOutputStream out;
        Socket socket;

        // Metodo che seleziona un account in maniera randomica
        public String accountPicker()  {
            String pickedAccount;
            do {
                pickedAccount = accounts.get(ThreadLocalRandom.current().nextInt(0, accounts.size()));
                if(loggedAccounts.size() == accounts.size()) return "error";
            }while (loggedAccounts.contains(pickedAccount));
            loggedAccounts.add(pickedAccount);
            return pickedAccount;
        }

        public ThreadAccount(ObjectOutputStream out, Socket socket) {
            this.out = out;
            this.socket = socket;

        }

        @Override
        public void run() {
            try {
                String randomUser = accountPicker();
                out.writeObject(randomUser);
                Platform.runLater(() -> logList.add(randomUser +" ha fatto l'accesso.")); /*loglist è l'elemento LOG dell'applicazione di sever lato grafico */
                //this.socket.close(); /*todo da rivedere chiusura*/
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
        ArrayList<Email> clientCurrentEmailList;

        public ThreadInMail(ObjectOutputStream out, String account, ArrayList<Email> clientCurrentEmailList){
            this.out = out;
            this.account = account;
            this.clientCurrentEmailList = clientCurrentEmailList;
        }

        @Override
        public void run(){
            try{
                emailList = jSonReader("Entrata",account);
                /*for (int i = 0; i < emailList.size(); i++){
                    if (clientCurrentEmailList.contains(emailList.get(i))){
                        emailList.remove(i);
                        i--;
                    }
                }*/
                emailList.removeAll(clientCurrentEmailList);
                out.writeObject(emailList);
                out.flush();
                //Platform.runLater(() -> logList.add("L'utente: " + account + " ha richiesto le mail in ingresso"));
            }catch(IOException e){throw new RuntimeException(e);}
        }
    }
    // Metodo che gestisce le email in uscita
    class ThreadOutMail implements Runnable{
        ObjectOutputStream out;
        String account;
        ArrayList<Email> emailList = new ArrayList<>();
        ArrayList<Email> clientCurrentEmailList;

        public ThreadOutMail(ObjectOutputStream out, String account, ArrayList<Email> clientCurrentEmailList){
            this.out = out;
            this.account = account;
            this.clientCurrentEmailList = clientCurrentEmailList;
        }

        @Override
        public void run(){
            try{
                emailList = jSonReader("Uscita",account);
                /*for (int i = 0; i < emailList.size(); i++){
                    if (clientCurrentEmail.contains(emailList.get(i))){
                        emailList.remove(i);
                        i--;
                    }
                }*/
                emailList.removeAll(clientCurrentEmailList);
                out.writeObject(emailList);
                out.flush();
                //Platform.runLater(() -> logList.add("L'utente: " + account + " ha richiesto le mail in uscita"));
            }catch(IOException e){throw new RuntimeException(e);}
        }
    }
    // Metodo che gestisce l'invio di una mail
    class ThreadSend implements Runnable{
        ObjectOutputStream out;
        ObjectInputStream in;
        String account;
        Email email;
        //boolean response = true;
        int response =0;
        ArrayList<String> receivers = new ArrayList<>();


        public ThreadSend(ObjectInputStream in,ObjectOutputStream out,String account){

            this.in = in;
            this.out = out;
            this.account = account;
        }
        @Override
        public void run(){
            try{
                email = (Email) in.readObject(); //Ho una Mail
                if(email.getReceiver().contains(",")){
                    String[] singleReceiver = email.getReceiver().split(",");
                    for(int i = 0; i < singleReceiver.length ; i++){
                        jSonWriterMultiple(email,account,singleReceiver[i]);
                        String s = singleReceiver[i];
                        Platform.runLater(() -> logList.add("L'utente: " + account + " ha mandato una mail a " + s));
                    }
                }
                else {
                    if(accounts.contains(email.getReceiver())){
                        jSonWriter(email,account);
                        Platform.runLater(() -> logList.add("L'utente: " + account + " ha mandato una mail a " + email.getReceiver()));
                    }
                    else {

                        //Uso l'intero response per comunicare se si tratta di un problema di sintassi o di un utente non esistente
                        if(email.getReceiver().contains("@example.com")){
                            //Contiene @example.com ma la parte prima non e' presente nella lista
                            response = 2;
                        }
                           //Non contiene nulla
                            else if(email.getReceiver().equals("")) response = 3;
                            //Non contiene @example.com quindi non rispetta il formato
                            else response = 1;
                        Platform.runLater(() -> logList.add("L'utente: " + account + " cerca di mandare un email all'utente " + email.getReceiver() + " che non esiste!"));
                    }
                }
                out.writeObject(response);
            }catch(IOException | ClassNotFoundException e){throw new RuntimeException(e);}

        }
    }
    class ThreadDelete implements Runnable{
        ObjectOutputStream out;
        ObjectInputStream in;
        String account;
        Email email;
        String[] accounts;
        boolean flag=false;

        public ThreadDelete(ObjectInputStream in,ObjectOutputStream out,String account) {

            this.in = in;
            this.out = out;
            this.account = account;
            System.out.println("------------"+account);
        }
        @Override
        public void run(){
            try{
                email = (Email) in.readObject(); //Ho una Mail
                jSonDeleter(email , account);
                //todo Cambiare la scritta del log, non mi interessa d
                Platform.runLater(() -> logList.add(account + " ha cancellato una mail ricevuta con oggetto "+email.getObject() + " con successo!"));
            }
            catch(IOException | ClassNotFoundException e){throw new RuntimeException(e);}
        }
    }
    // Metodo che gestisce la chiusura
    class ThreadExit implements Runnable{
        ObjectOutputStream out;
        String exitUser;
        public ThreadExit(ObjectOutputStream out, String account){

            this.exitUser = account;
            this.out = out;
        }
        @Override
        public void run(){
            try {
                loggedAccounts.remove(exitUser);
                Platform.runLater(() -> logList.add(exitUser + " ha fatto il LOGOUT."));

                out.writeObject(true);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    class RunServer implements Runnable{
        String account = "Si è connesso ";
        private final SimpleStringProperty accountLog = new SimpleStringProperty();

        private Socket incoming;
        private String action;

        public RunServer(){

        }

        @Override
        public void run() {
            try {
                setAccountList();
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
                /*serve a richimare i thread funzioni più avanti ---> executor.execute(new ThreadUser(out));*/
                while (true) {
                    // Si blocca finchè non riceve qualcosa, va avanti SOLO SE LO RICEVE
                    socket = serverSocket.accept();
                    // Crea un nuovo oggetto che legge oggetti dal flusso di input derivato dalla connessione socket (client)
                    openStreams();

                    if(socketInputStream == null || socketOutputStream == null)
                        break;

                    // Chi sta comunicando con il server, chi sta richiedendo quell'azione
                    account = (String) socketInputStream.readObject();
                    // Le azioni che vengono svolte
                    action = (String) socketInputStream.readObject();


                    // In base all'azione si crea un metodo thread runnable utilizzando execute
                    switch (action) {
                        case "account":
                            executor.execute(new ThreadAccount(socketOutputStream, socket));
                            break;
                        case "emailIn":

                            executor.execute(new ThreadInMail(socketOutputStream,account,(ArrayList<Email>) socketInputStream.readObject()));
                            break;
                        case "emailOut":
                            executor.execute(new ThreadOutMail(socketOutputStream,account,(ArrayList<Email>) socketInputStream.readObject()));
                            break;
                        case "send":
                            executor.execute(new ThreadSend(socketInputStream, socketOutputStream, account));
                            break;
                        case "delete":
                            executor.execute(new ThreadDelete(socketInputStream, socketOutputStream, account));
                            break;

                        case "exit":
                            executor.execute(new ThreadExit(socketOutputStream, account));
                            break;

                        default:
                            System.out.println("default");
                            break;
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("RunServer catch (IOException e) ");

            } catch (ClassNotFoundException e) {
                //throw new RuntimeException(e);
                System.out.println("RunServer catch (ClassNotFoundException e)");
            }

            closeStreams();
        }


    }
    public void openStreams() {
        try {
            //System.out.println("Server Connesso");
            socketOutputStream = new ObjectOutputStream(socket.getOutputStream());
            socketOutputStream.flush();
            socketInputStream = new ObjectInputStream(socket.getInputStream());
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void closeStreams() {
        try {

            if(socketInputStream != null) {
                socketInputStream.close();
            }

            if(socketOutputStream != null) {
                socketOutputStream.close();
            }

            executor.shutdown();
            exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exit(){
        try{
            serverSocket.close();
        }
        catch(IOException e){
            System.out.println("Non è stato chiudere il serverSocket con successo!");
        }

    }

    /* -------------------------- GESTIONE JSON --------------------- ----- */



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
                            Email emailObj = new Email(sender, receiver, object, message, dateTime);
                            if(!(Objects.equals(account, sender))|| (Objects.equals(account, receiver))) inMail.add(emailObj);
                        }
                        else if(mailListType.equals("Uscita")){
                            Email emailObj = new Email(sender, receiver, object, message, dateTime);
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

    public synchronized void jSonWriter(Email newEmail, String account){

         // Creazione dell'oggetto ObjectMapper
         ObjectMapper writeObjectMapper = new ObjectMapper();
         ObjectNode newEmailNode;
         try {
             // Carica il file JSON esistente
             JsonNode rootNode = (JsonNode) writeObjectMapper.readTree(new File(jsonFilePath));

             // Cerca l'account nel JSON
             if (rootNode.isArray()) {
                 ArrayNode accounts = (ArrayNode) rootNode;
                 for (JsonNode accountNode : accounts) {
                     String registeredAccount = accountNode.path("email").asText();
                       if(!Objects.equals(newEmail.getSender(), newEmail.getReceiver())){
                         if (registeredAccount.equals(account)) {
                         // Trovato l'account, aggiungi la nuova email al nodo "content"
                         newEmailNode = writeObjectMapper.createObjectNode();
                         newEmailNode.put("from", newEmail.getSender());
                         newEmailNode.put("to", newEmail.getReceiver());
                         newEmailNode.put("object", newEmail.getObject());
                         newEmailNode.put("text", newEmail.getMessage());
                         newEmailNode.put("dateTime", newEmail.getDate());

                         ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                         contentNode.add(newEmailNode);

                         // Salva le modifiche nel file JSON
                         writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                     }
                         if (registeredAccount.equals(newEmail.getReceiver())){
                             newEmailNode = writeObjectMapper.createObjectNode();
                             newEmailNode.put("from", newEmail.getSender());

                             newEmailNode.put("to", newEmail.getReceiver());
                             newEmailNode.put("object", newEmail.getObject());
                             newEmailNode.put("text", newEmail.getMessage());
                             newEmailNode.put("dateTime", newEmail.getDate());

                             ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                             contentNode.add(newEmailNode);

                             // Salva le modifiche nel file JSON
                             writeObjectMapper.writeValue(new File(jsonFilePath), accounts);

                         }}
                     else{
                         if (registeredAccount.equals(account)) {
                             newEmailNode = writeObjectMapper.createObjectNode();
                             newEmailNode.put("from", newEmail.getSender());
                             newEmailNode.put("to", newEmail.getReceiver());
                             newEmailNode.put("object", newEmail.getObject());
                             newEmailNode.put("text", newEmail.getMessage());
                             newEmailNode.put("dateTime", newEmail.getDate());
                             ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                             contentNode.add(newEmailNode);
                             writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                         }
                     }

                 }
             }

         } catch (IOException e) {
             System.err.println("Errore durante la lettura/scrittura del file JSON: " + e.getMessage());
         }
     }

    public synchronized void jSonWriterMultiple(Email newEmail, String account, String receiver){
        String destinatario = receiver;
        ObjectMapper writeObjectMapper = new ObjectMapper();
        ObjectNode newEmailNode;
        try {
            JsonNode rootNode = (JsonNode) writeObjectMapper.readTree(new File(jsonFilePath));
            if (rootNode.isArray()) {
                ArrayNode accounts = (ArrayNode) rootNode;
                for (JsonNode accountNode : accounts) {
                    String registeredAccount = accountNode.path("email").asText();
                    if(!Objects.equals(newEmail.getSender(), destinatario)){
                        if (registeredAccount.equals(account)) {
                            // Trovato l'account, aggiungi la nuova email al nodo "content"
                            System.out.println("Sono prima che si scriva sul mittente ");
                            newEmailNode = writeObjectMapper.createObjectNode();
                            newEmailNode.put("from", newEmail.getSender());
                            newEmailNode.put("to", newEmail.getReceiver());
                            newEmailNode.put("object", newEmail.getObject());
                            newEmailNode.put("text", newEmail.getMessage());
                            newEmailNode.put("dateTime", newEmail.getDate());

                            ArrayNode contentNode = (ArrayNode) accountNode.path("content");

                            contentNode.add(newEmailNode);

                            // Salva le modifiche nel file JSON
                            writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                        }
                        if (registeredAccount.equals(destinatario)){
                            System.out.println("Sono prima che si scriva sul destinatario ");
                            newEmailNode = writeObjectMapper.createObjectNode();
                            newEmailNode.put("from", newEmail.getSender());
                            newEmailNode.put("to", newEmail.getReceiver());
                            newEmailNode.put("object", newEmail.getObject());
                            newEmailNode.put("text", newEmail.getMessage());
                            newEmailNode.put("dateTime", newEmail.getDate());

                            ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                            contentNode.add(newEmailNode);

                            // Salva le modifiche nel file JSON
                            writeObjectMapper.writeValue(new File(jsonFilePath), accounts);

                        }}
                    else{
                        if (registeredAccount.equals(account)) {
                            newEmailNode = writeObjectMapper.createObjectNode();
                            newEmailNode.put("from", newEmail.getSender());
                            newEmailNode.put("to", newEmail.getReceiver());
                            newEmailNode.put("object", newEmail.getObject());
                            newEmailNode.put("text", newEmail.getMessage());
                            newEmailNode.put("dateTime", newEmail.getDate());
                            ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                            contentNode.add(newEmailNode);
                            writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                        }
                    }
                }
            }



        }catch(IOException e){
            System.err.println("Errore durante la lettura/scrittura del file JSON: " + e.getMessage());
        }


    }
    public synchronized void jSonDeleter(Email emailToDelete, String account) throws IOException {
        try {
            // Legge il contenuto del file JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            if (rootNode.isArray()) {
                ArrayNode accounts = (ArrayNode) rootNode;

                // Scorre gli account nel JSON
                for (JsonNode accountNode : accounts) {
                    String email = accountNode.path("email").asText();

                    // Verifica se l'email dell'account corrente corrisponde all'account specificato
                    if (email.equals(account)) {
                        ArrayNode contentNode = (ArrayNode) accountNode.path("content");

                        // Scorre il contenuto dell'email
                        for (int i = 0; i < contentNode.size(); i++) {
                            JsonNode emailNode = contentNode.get(i);
                            if (emailNode.path("from").asText().equals(emailToDelete.getSender()) &&
                                    emailNode.path("to").asText().equals(emailToDelete.getReceiver()) &&
                                    emailNode.path("object").asText().equals(emailToDelete.getObject()) &&
                                    emailNode.path("text").asText().equals(emailToDelete.getMessage()) &&
                                    emailNode.path("dateTime").asText().equals(emailToDelete.getDate())) {

                                // Rimuove l'email se tutti i campi corrispondono
                                contentNode.remove(i);
                                break; // Esce dal ciclo una volta trovata e rimossa l'email corrispondente
                            }
                        }
                        break; // Esce dal ciclo una volta trovato l'account corrispondente
                    }
                }

                // Sovrascrive il file JSON con le modifiche
                objectMapper.writeValue(new File(jsonFilePath), accounts);
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura/scrittura del file JSON: " + e.getMessage());

        }
    }
}


