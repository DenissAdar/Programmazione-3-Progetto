package com.example.progettoprogrammazione;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    Socket socket = null;
    ObjectInputStream inputStream = null;
    ObjectOutputStream outputStream = null;
    public void listen(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            String account;
          /* while (true) {

            }*/

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket!=null)
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

}
