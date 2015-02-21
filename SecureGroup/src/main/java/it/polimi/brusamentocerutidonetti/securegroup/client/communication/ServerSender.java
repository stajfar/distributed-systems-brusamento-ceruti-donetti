/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Mattia
 */
public class ServerSender implements MessageSender{
    
    Socket serverSocket;

    ServerSender(Socket s) {
        this.serverSocket = s;
    }
    
    @Override
    public void sendMessage(Serializable msg, Class msgClass) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            oos.writeObject(msg);
            oos.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerSender.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerSender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
