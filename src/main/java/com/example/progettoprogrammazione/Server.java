package com.example.progettoprogrammazione;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;



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

public class Server {
    private String[] accounts;
    //private static AtomicInteger uniqueId = new AtomicInteger(0);
    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";

    ServerSocket serverSocket;
    ThreadPoolExecutor executor;

    private ListProperty<String> logList; /*binding in sever controller*/
    private ObservableList<String> logListContent;

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
                emailList = jSonReader("Uscita",account);
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
                    if(email.getReceiver().equals(accounts[i]))
                        flag=true;
                }
                if(flag){
                    jSonWriter(email,account);
                    Platform.runLater(() -> logList.add("L'utente: " + account + " ha mandato una mail a " + email.getReceiver()));
                }
                else {
                    Platform.runLater(() -> logList.add("L'utente: " + account + " cerca di mandare un email all'utente " + email.getReceiver() + " che non esiste!"));
                }

            }catch(IOException | ClassNotFoundException e){throw new RuntimeException(e);}

        }
    }
    class ThreadSendAll implements Runnable{
        ObjectInputStream in;
        String account;
        Email email;
        boolean flag=false;
        public ThreadSendAll(ObjectInputStream in,String account)
        {
            this.in = in;
            this.account = account;
        }
        @Override
        public void run(){
            try{
                email = (Email) in.readObject();
                //stringa tipo denis@example.com,gaia@example.com
            }catch (IOException | ClassNotFoundException e){throw new RuntimeException(e);}

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
                            executor.execute(new ThreadSend(inputStream, outputStream, account));
                            break;
                        case "sendAll":
                            //executor.execute(new ThreadSendAll(outputStream));
                            break;
                        case "delete":
                            executor.execute(new ThreadDelete(inputStream, outputStream, account));
                            break;
                        case "deleteAll":
                            //executor.execute(new ThreadDeleteAll(outputStream));
                            break;
                        case "getForwardAccounts":
                            executor.execute(new ThreadForwardAccounts(outputStream,account));
                            break;
                        case "exit":
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

    class ThreadForwardAccounts implements Runnable {
        ObjectOutputStream outputStream;
        String account;

        public ThreadForwardAccounts(ObjectOutputStream out, String a) {
            this.outputStream = out;
            this.account = a;
        }

        @Override
        public void run() {
            ArrayList<String> forwardAccounts = new ArrayList<>();
            try {
                forwardAccounts.clear();
                ObjectMapper readObjectMapper = new ObjectMapper();
                JsonNode rootNode = readObjectMapper.readTree(new File(jsonFilePath));

                // Itera sui nodi del file JSON
                for (JsonNode emailNode : rootNode) {
                    forwardAccounts.add(emailNode.get("email").asText());
                }
                outputStream.writeObject(forwardAccounts);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /* -------------------------- GESTIONE JSON --------------------- ----- */


    // Metodo che conta quanti account ci sono nel file json
    public int jsonCount() {
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
                            Email emailObj = new Email(sender, receiver, object, message, dateTime);
                            if(Objects.equals(account, receiver)) inMail.add(emailObj);
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
                     //den Questo e' un controllo che nella realta' non ha senso: perche' qui controlla che --account-- dato da client sia uguale a uno dei nodi degli account("email:")
                     //ma account viene dato da un metodo account picker che lavora gia' su quel json per assegnare un nodo degli account("email:") ad --account--

                     //qua dobbiamo fare un controllo che se dest e mit diversi tra di loro  fa i due if sotto sennò fa altro
                     if(!Objects.equals(newEmail.getSender(), newEmail.getReceiver())){
                         if (registeredAccount.equals(account)) {
                         // Trovato l'account, aggiungi la nuova email al nodo "content"
                         newEmailNode = writeObjectMapper.createObjectNode();
                         newEmailNode.put("from", newEmail.getSender());

                         // TODO inverte oggetto e destinatario
                         newEmailNode.put("to", newEmail.getReceiver());
                         newEmailNode.put("object", newEmail.getObject());
                         newEmailNode.put("text", newEmail.getMessage());
                         newEmailNode.put("dateTime", newEmail.getDate());

                         ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                         contentNode.add(newEmailNode);

                         // Salva le modifiche nel file JSON
                         writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                         //System.out.println("Email aggiunta con successo per l'account: " + account);
                             System.out.println("Sono nel nodo if1" );
                             System.out.println("Valore di Mittente " + newEmail.getSender());
                             System.out.println("Valore di Dest " + newEmail.getReceiver());
                             System.out.println("Valore di uguaglianza" + newEmail.getSender().equals(newEmail.getReceiver()));
                     }
                         if (registeredAccount.equals(newEmail.getReceiver())){
                             newEmailNode = writeObjectMapper.createObjectNode();
                             newEmailNode.put("from", newEmail.getSender());

                             // TODO inverte oggetto e destinatario
                             newEmailNode.put("to", newEmail.getReceiver());
                             newEmailNode.put("object", newEmail.getObject());
                             newEmailNode.put("text", newEmail.getMessage());
                             newEmailNode.put("dateTime", newEmail.getDate());

                             ArrayNode contentNode = (ArrayNode) accountNode.path("content");
                             contentNode.add(newEmailNode);

                             // Salva le modifiche nel file JSON
                             writeObjectMapper.writeValue(new File(jsonFilePath), accounts);
                             //System.out.println("Email aggiunta con successo per l'account: " + newEmail.getReceiver());
                             System.out.println("Sono nel nodo if2" );
                             System.out.println("Valore di Mittente " + newEmail.getSender());
                             System.out.println("Valore di Dest " + newEmail.getReceiver());
                             System.out.println("Valore di uguaglianza" + newEmail.getSender().equals(newEmail.getReceiver()));
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
                             System.out.println("Sono nel nodo else del Writer");
                         }
                     }

                 }
             }

         } catch (IOException e) {
             System.err.println("Errore durante la lettura/scrittura del file JSON: " + e.getMessage());
         }
     }

     public synchronized void jSonDeleter(Email newEmail, String account) throws JSONException, IOException {
     String jsonString = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
     JSONArray jsonArray = new JSONArray(jsonString);

     // Scorrere l'array JSON
     for (int i = 0; i < jsonArray.length(); i++) {
         JSONObject jsonObject = jsonArray.getJSONObject(i);
         String email = jsonObject.getString("email");
         // Verificare se l'email corrente corrisponde all'account specificato
         if (email.equals(account)) {
             JSONArray contentArray = jsonObject.getJSONArray("content");
             // Scorrere il contenuto (content) dell'email
             for (int j = 0; j < contentArray.length(); j++) {
                 JSONObject contentObject = contentArray.getJSONObject(j);
                 System.out.println("Contenuto di contentObject : " + contentObject);
                 // Verificare se i campi dell'oggetto Email corrispondono ai campi del JSON
                 if (contentObject.getString("from").equals(newEmail.getSender()) &&
                         contentObject.getString("to").equals(newEmail.getReceiver()) &&
                         contentObject.getString("object").equals(newEmail.getObject()) &&
                         contentObject.getString("text").equals(newEmail.getMessage()) &&
                         contentObject.getString("dateTime").equals(newEmail.getDate())) {

                     // Rimuovere il nodo content se i campi corrispondono
                     contentArray.remove(j);
                     j--; // Aggiornare l'indice dopo la rimozione
                 }
             }
         }
     }
     Files.write(Paths.get(jsonFilePath), jsonArray.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
 }


}


