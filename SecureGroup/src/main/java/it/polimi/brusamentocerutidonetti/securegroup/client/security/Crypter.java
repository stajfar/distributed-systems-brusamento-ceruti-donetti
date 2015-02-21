/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageHandler;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.Logger;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import java.io.IOException;
import java.io.Serializable;
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
    
    private DEKManager dekm;
    private UserInterface ui;
    private Logger logger;
    private MessageSender ms;
    
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


