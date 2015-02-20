/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageHandler;
import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import java.io.Serializable;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public class Crypter implements MessageSender, MessageHandler{
    
    private DEKManager dekm;
    private UserInterface ui;
    
    @Override
    public void sendMessage(Serializable msg, Class msgClass) {
        if(msgClass.equals(String.class)){
            
            
        }
        
    }

    @Override
    public synchronized void handleMessage(Object o) {
       
    }
    
    
}


