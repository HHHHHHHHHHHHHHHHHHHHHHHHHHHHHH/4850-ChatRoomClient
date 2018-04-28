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

    
    public Client(String IPAddress, int port) throws IOException {
        this.password = "";
        this.name = "";
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
                        Object message = messages.take();
                        // Do some handling here...
                        System.out.println("Client Received: " + message);

                        if(message=="Login success!")
                        {
                            
                        }
                        else if(message=="Login failed, please check you name and password.")
                        {
                            name="";
                            password="";
                        }
                        //login success
                        else if(message=="You should login first!")
                        {
                            name="";
                            password="";
                        }
                        //other
                        else if(message=="You send message to all in succeed.")
                        {
                            
                        }
                        else if(message=="You failed to send message to all.")
                        {
                            
                        }
                        //sendall message
                        else if(message=="You send message in succeed.")
                        {
                            
                        }
                        //send someone message
                        else if(message=="There is no this person or it is not in the room.")
                        {
                            
                        }
                        //someone send message
                        else if(message=="You asked who is in the room.")
                        {
                            
                        }
                        //who
                        else if(message=="You log out!")
                        {
                            name="";
                            password="";
                        }
                        //logout
                        else
                        {
                            
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
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InterruptedException ex) {
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
        if ("login".equals(buff[0])) {
            name = buff[1];
            password = buff[2];

        } //other + user pwd
        else if (!"".equals(this.name) && !"".equals(this.password)) {
            obj = (String) obj + " " + this.name + " " + this.password;

        } else {
            System.out.println("You should loggin first!");
            return;
        }

        System.out.println("Client send: " + obj);
        server.write(obj);
    }
}
