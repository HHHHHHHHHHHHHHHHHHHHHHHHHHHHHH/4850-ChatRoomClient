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

                    } catch (IOException e) {
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
                        String message = (String) messages.take();
                        // Do some handling here...
                        //System.out.println("Client Received: " + message);
                        System.out.println(message);
                        if (null != message) switch (message) {
                            case "Login success!":
                                login = true;
                                break;
                        //login success
                            case "Login failed, please check you name and password.":
                                name = "";
                                password = "";
                                break;
                        //other
                            case "You should login first!":
                                name = "";
                                password = "";
                                break;
                            case "You send message to all in succeed.":
                                break;
                        //sendall message
                            case "You failed to send message to all.":
                                break;
                        //send someone message
                            case "You send message in succeed.":
                                break;
                        //someone send message
                            case "There is no this person or it is not in the room.":
                                break;
                        //who
                            case "You asked who is in the room.":
                                break;
                        //logout
                            case "You log out!":
                                name = "";
                                password = "";
                                login = false;
                                System.exit(0);
                                break;
                            case "You cannot login because the room can only contain 3 people.":
                                name = "";
                                password = "";
                                login = false;
                                break;
                            default:
                                break;
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
            
            System.out.println("You should log in or sign up first!");
            return;
        }

        //System.out.println("Client send: " + obj);
        server.write(obj);
    }
}
