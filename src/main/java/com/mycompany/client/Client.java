/**
 * Name: Ding Hao
 * Date: 5/1/2018
 * Description:
 *     It is the client part of the ChatRoom Program. 
 * Help: 
 *     newuser username userpassword: create a new account
 * 
 *     login username userpassword: login an account with username and password
 * 
 *     send someone message: send message to someone
 * 
 *     send all: send everyone a message
 * 
 *     who: show you who are in this chatroom
 * 
 *     logout: logout from the account and close the client
 * 
 */
package com.mycompany.client;

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;



/**
 *
 * @author dh
 */
public class Client {

    private ConnectionToServer server;
    private LinkedBlockingQueue<Object> messages;
    private Socket socket;
    private String name;
    private String password;
    private boolean login;

    public Client(String IPAddress, int port) throws IOException {
        this.password = "";
        this.name = "";
        this.login = false;
        socket = null;
        server = null;
        try{
        socket = new Socket(IPAddress, port);
        messages = new LinkedBlockingQueue<>();
        server = new ConnectionToServer(socket);
        }
        catch(IOException e){
        }
        
       
        
        System.out.println("Input your shell, please.");
        /**
         * send shell to server
         */
        Thread askThread = new Thread() {
            @Override
            public void run() {
                String line="";
                while (true) {
                    try {
                        InputStreamReader convert = new InputStreamReader(System.in);
                        BufferedReader stdin = new BufferedReader(convert);
                        line = stdin.readLine();
                        

                    } catch (IOException e) {
                    }
                    try {
                        send(line);
                    } catch (Exception e) {
                        System.out.println("The server looks unconnected.");
                        System.exit(0);
                    }
                    
                }

            }
        };
        askThread.start();

        /**
         * different operitions after recieve results from server
         */
        Thread messageHandling = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {String message="";
                        try{
                        message = (String) messages.take();
                        }catch(InterruptedException e){
                            continue;
                        }
                        System.out.println(message);
                        if (null != message) {
                            switch (message) {
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
                                    break;
                                case "You cannot login because the room can only contain 3 people.":
                                    name = "";
                                    password = "";
                                    login = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        
                    }
                }
            }
        };

        messageHandling.start();
    }
/**
 * connect to server
 */
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
/**
 * send shell to server
 * if it is login or newuser, user should be logout, or he/she should be login.
 * @param obj 
 */
    public void send(Object obj) {
        String[] buff = stringGuide((String)obj);
        if(buff==null)
        {
            System.out.println("Wrong input.");
            return;
        }
        //login or newuser
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

        server.write(obj);
    }
    private String[] stringGuide(String str) {
        String[] buff = str.split(" ");
        switch (buff[0]) {
            case "login":
                if (buff.length != 3) {
                    return null;
                }
                break;
            case "sendall":
                if (buff.length != 2) {
                    return null;
                }
                break;
            case "send":
                if (buff.length != 3) {
                    return null;
                }
                break;
            case "who":
                if (buff.length != 1) {
                    return null;
                }
                break;
            case "logout":
                if (buff.length != 1) {
                    return null;
                }
                break;
            case "newuser":
                if (buff.length != 3) {
                    return null;
                }
                break;
            default:
                return null;

        }

        return buff;
        }
    
}
