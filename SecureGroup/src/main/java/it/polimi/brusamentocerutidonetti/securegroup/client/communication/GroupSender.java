/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import it.polimi.brusamentocerutidonetti.securegroup.client.security.Crypter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Mattia
 */
public class GroupSender implements MessageSender{
    
    private MulticastSocket ms;
    private InetAddress group;
    
    public GroupSender(MulticastSocket ms, InetAddress group){
        this.ms = ms;
        this.group = group;
        try {
            ms.setBroadcast(true);
        } catch (SocketException ex) {
            Logger.getLogger(GroupSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void sendMessage(Serializable obj, Class msgClass) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.flush();
            o.writeObject(obj);
            o.flush();
            byte[] msg = b.toByteArray();
            DatagramPacket pckt = new DatagramPacket(msg, msg.length, group, Crypter.portMulticast);
            ms.send(pckt);
            o.close();
        } catch (IOException ex) {
            Logger.getLogger(GroupSender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
}
