/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dh
 */
public class Client {

    private final ConnectionToServer server;
    private LinkedBlockingQueue<Object> messages;
    private final Socket socket;
    private String name;
    private String password;
    private boolean login;

    public Client(String IPAddress, int port) throws IOException {
        this.password = "";
        this.name = "";
        this.login = false;
        socket = new Socket(IPAddress, port);
        messages = new LinkedBlockingQueue<>();
        server = new ConnectionToServer(socket);

        System.out.println("Dear client, input you shell, please.");
        Thread askThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        InputStreamReader convert = new InputStreamReader(System.in);
                        BufferedReader stdin = new BufferedReader(convert);

                        String line = stdin.readLine();

                        send(line);

                    } catch (Exception e) {
                    }
                }

            }
        };
        askThread.start();

        Thread messageHandling = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String message = (String)messages.take();
                        // Do some handling here...
                        System.out.println("Client Received: " + message);
                        System.out.println(message);
                        if ("Login success!".equals(message)) {
                            login = true;

                        } else if ("Login failed, please check you name and password.".equals(message)) {
                            name = "";
                            password = "";
                        } //login success
                        else if ("You should login first!".equals(message)) {
                            name = "";
                            password = "";
                        } //other
                        else if ("You send message to all in succeed.".equals(message)) {

                        } else if ("You failed to send message to all.".equals(message)) {

                        } //sendall message
                        else if ("You send message in succeed.".equals(message)) {

                        } //send someone message
                        else if ("There is no this person or it is not in the room.".equals(message)) {

                        } //someone send message
                        else if ("You asked who is in the room.".equals(message)) {

                        } //who
                        else if ("You log out!".equals(message)) {
                            name = "";
                            password = "";
                            login = false;
                        } //logout
                        else if ("You cannot login because the room can only contain 3 people.".equals(message)) {
                            name = "";
                            password = "";
                            login = false;
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        };

        //messageHandling.setDaemon(true);
        messageHandling.start();
    }

    private class ConnectionToServer {

        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Thread read = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Object obj = in.readObject();
                            messages.put(obj);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException | InterruptedException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };

            read.setDaemon(true);
            read.start();
        }

        private void write(Object obj) {
            try {
                out.writeObject(obj);
            } catch (IOException e) {
            }
        }

    }

    public void send(Object obj) {
        String[] buff = ((String) obj).split(" ");
        //login user pwd 
        if ("login".equals(buff[0]) || "newuser".equals(buff[0])) {

            if (login == true) {
                System.out.println("You cannot do this when you have log in.");
                return;
            } else {
                name = buff[1];
                password = buff[2];
            }
        } //other + user pwd
        else if (login == true) {
            obj = (String) obj + " " + this.name + " " + this.password;

        } else {
            System.out.println("login = " + login);
            System.out.println("You should log in first!");
            return;
        }

        System.out.println("Client send: " + obj);
        server.write(obj);
    }
}
