package com.example.progettoprogrammazione;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;


public class Server {
    int cambiamento;
    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";

    ArrayList<Email> inMail = new ArrayList<>();
    ArrayList<Email> outMail = new ArrayList<>();
    ArrayList<ArrayList<Email>> contenuto = new ArrayList<>();


    //Conessione---------------------------------------------------------------------------------
    //
    public void listen(int port) {

        try {
            int id = 1;
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("ascolto");
                // TODO Rimango in attesa finchè non trovo qualcosa da accettare
                Socket incoming = serverSocket.accept();
                //Classe dei runnable thread---
                Runnable r = new ThreadHandler(incoming,id);
                new Thread(r).start();
                id++;
                //serveClient(serverSocket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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
                    if(Objects.equals(this.account, sender))        outMail.add(emailObj);
                    else if(Objects.equals(this.account, receiver)) inMail.add(emailObj);
                }

            }
            System.out.println("Grandezza di outMail "+outMail.size());
            System.out.println("Grandezza di inMail "+ inMail.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

} //Fine classe Server-----------------------------------

class ThreadHandler implements Runnable{
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private ObjectOutputStream outputStream = null;
    String account = "Si è connesso ";
    private final SimpleStringProperty accountLog = new SimpleStringProperty();

    private Socket incoming;
    private int id;

    public ThreadHandler(Socket in, int id){
        this.incoming = in;
        this.id = id;
    }
    @Override
    public void run() {
        serveClient();
    }

    public void serveClient(){
        try {

            openStreams();

            account += (String) inputStream.readObject();
            System.out.println(account);
            accountLog.set(account);

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
    private void openStreams() throws IOException {

        System.out.println("Server Connesso");

        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        outputStream.flush();

    }

    private void closeStreams() {
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

