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
import java.io.IOException;

/**
 * The port is 14727
 * @author dh
 */

public class ChatRoomClient {

    public static void main(String[] args) throws IOException {
        //String address = Inet4Address.getLocalHost().getHostAddress();
        if(args.length!=1)
        {
            System.out.println("There should be one parameter.");
            return;
        }
        String address = args[0];
        int port = 14727;
        try {
            Client client = new Client(address, port);
        } catch (IOException e) {
            System.out.println(e);
        }
        

    }
}
