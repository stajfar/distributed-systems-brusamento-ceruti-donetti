/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import it.polimi.brusamentocerutidonetti.securegroup.client.security.KeysManager;
import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 *
 * @author Mattia
 */
public class ServerDispatcher {
    
    public static final String serverIP = "localhost";
    public static final int port = 1234;
    
    private KeysManager km;
    private MessageSender ms;
    private UserInterface logger;
    
    public void requestJoin(){
        try {
            Socket s = new Socket(serverIP, port);
            ms = new ServerSender(s);
            new Thread(new ServerReceiver(s)).start();
            Key publicKey = km.getPublicKey();
            ms.sendMessage(publicKey, Key.class);
        } catch (IOException ex) {
            logger.receiveMessage(getClass() + ": Server not found.");
        }
    }
    
    
    
}
