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
    Socket socket = null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;
    private String jsonFilePath= "src/main/java/com/example/progettoprogrammazione/accounts/account.json";
    String account = "Si è connesso ";
    ArrayList<Email> inMail = new ArrayList<>();
    ArrayList<Email> outMail = new ArrayList<>();
    ArrayList<ArrayList<Email>> contenuto = new ArrayList<>();

    private final SimpleStringProperty accountLog = new SimpleStringProperty();

    //Conessione---------------------------------------------------------------------------------
    public void listen(int port) {

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            // a Marius puzza la roba di v= false, sostituire con true
            while (true) {
                System.out.println("qua");
                serveClient(serverSocket);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
 //           System.out.println("entrato nel finally");
            if (socket!=null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
 //       System.out.println("Fine di listen");
    }
    public void serveClient(ServerSocket serverSocket){
        try {

            openStreams(serverSocket);

            account = (String) inputStream.readObject();
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
    private void openStreams(ServerSocket serverSocket) throws IOException {
        socket = serverSocket.accept();
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
    //Conessione---------------------------------------------------------------------------------

    public void jSonReader (){
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
    }

    public String getAccountLog() {
        return accountLog.get();
    }

    public SimpleStringProperty getAccountLogProperty() {
        return accountLog;
    }


}

