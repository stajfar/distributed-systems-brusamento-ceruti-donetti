/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.GroupReceiver;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.GroupSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageHandler;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.Logger;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public class Crypter implements MessageSender, MessageHandler{
    
    public static final String groupIP = "239.255.1.2";
    public static final int portMulticast = 5353;
    
    private DEKManager dekm;
    private UserInterface ui;
    private Logger logger;
    private MessageSender ms;
    
    public Crypter(UserInterface ui, Logger log, DEKManager dek){
        try {
            this.ui = ui;
            this.logger = log;
            this.dekm = dek;
            InetAddress group = InetAddress.getByName(groupIP);
            MulticastSocket s = new MulticastSocket(portMulticast);
            
            s.setInterface(InetAddress.getByName("10.0.0.1"));    
            
            s.joinGroup(group);
            this.ms = new GroupSender(s, group);
            new Thread(new GroupReceiver(s, this)).start();
        } catch (UnknownHostException ex) {
            logger.error(getClass() + ": Unkown host exception.");
        } catch (IOException ex) {
            logger.error(getClass() + ": IOexception.");
        }
        
    }
    
    @Override
    public void sendMessage(Serializable msg, Class msgClass) {
        try {
            Cipher encrypter = dekm.getEncrypter();
            SealedObject encMsg = new SealedObject(msg, encrypter);
            ms.sendMessage(encMsg, SealedObject.class);
        } catch (IOException | IllegalBlockSizeException ex) {
            logger.error(getClass() + ": Encrypt error.");
        }
    }

    @Override
    public synchronized void handleMessage(Object o) {
        SealedObject msg = (SealedObject) o;
        Cipher decrypter = dekm.getDecrypter();
        try {
            Object decryptedMsg = msg.getObject(decrypter);
            ui.receiveMessage((String) decryptedMsg);
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | NullPointerException ex) {
            decrypter = dekm.getSecondaryDecrypter();
            try {
                Object decryptedMsg = msg.getObject(decrypter);
                ui.receiveMessage((String) decryptedMsg);
            } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | NullPointerException ex1) {
                ui.receiveMessage(msg.toString());
            }
        }
    }
    
    
}


