/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mattia
 */
public class GroupReceiver implements Runnable{
    
    private MessageHandler mh;
    private MulticastSocket ms;
    
    public GroupReceiver(MulticastSocket ms, MessageHandler mh){
        this.ms = ms;
        this.mh = mh;
    }

    @Override
    public void run() {
        while(true){
            try {
                byte[] buffer = new byte[5000];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                ms.receive(recv);
                ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
                ObjectInputStream os = new ObjectInputStream(new BufferedInputStream(bs));
                Object msg = new Object();
                try {
                    msg = os.readObject();
                } catch (ClassNotFoundException | IOException ex){
                    Logger.getLogger(GroupReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                mh.handleMessage(msg);
            } catch (IOException ex) {
                Logger.getLogger(GroupReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
