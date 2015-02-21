/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mattia
 */
public class ServerReceiver implements Runnable{
    
    private Socket s;
    private ObjectInputStream ois;
    private MessageHandler mh;
    
    public ServerReceiver(Socket s) throws IOException{
        this.s = s;
        ois = new ObjectInputStream(s.getInputStream());        
    }
    
    @Override
    public void run() {
        while(true){
            try {
                mh.handleMessage(ois.readObject());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(ServerReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
