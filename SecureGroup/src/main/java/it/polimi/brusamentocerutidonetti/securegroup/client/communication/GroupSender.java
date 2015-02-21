/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Mattia
 */
public class GroupSender implements MessageSender{
    
    public static final String groupIP = "224.0.0.1";
    public static final int portMulticast = 6789;
    
    private MulticastSocket ms;
    private InetAddress group;
    
    public GroupSender(MulticastSocket ms, InetAddress group){
        this.ms = ms;
        this.group = group;
    }
    
    @Override
    public void sendMessage(Serializable obj, Class msgClass) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(obj);
            byte[] msg = b.toByteArray();
            DatagramPacket pckt = new DatagramPacket(msg, msg.length, group, 6789);
            ms.send(pckt);
        } catch (IOException ex) {
            Logger.getLogger(GroupSender.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
}
