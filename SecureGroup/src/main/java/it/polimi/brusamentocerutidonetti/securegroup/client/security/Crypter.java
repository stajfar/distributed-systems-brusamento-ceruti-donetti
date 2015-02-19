/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.security;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import java.io.Serializable;

/**
 *
 * @author Mattia
 */
public class Crypter implements MessageSender{
    
    private DEKManager dekm;
    
    @Override
    public void sendMessage(Serializable msg, Class msgClass) {
        if(msgClass.equals(String.class)){
            
            
        }
        
    }
    
    
}


