/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.client;

import java.io.IOException;
import java.net.Inet4Address;

/**
 *
 * @author dh
 */
public class ChatRoomClient {
    public static void main(String[] args) throws IOException {
        String address=Inet4Address.getLocalHost().getHostAddress();
        int port=14727;
        Client client = new Client(address,port);
        
        
    }
}
