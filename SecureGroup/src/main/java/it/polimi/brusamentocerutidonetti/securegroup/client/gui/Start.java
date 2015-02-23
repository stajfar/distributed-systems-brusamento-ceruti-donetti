/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.ServerDispatcher;
import it.polimi.brusamentocerutidonetti.securegroup.client.security.Crypter;
import it.polimi.brusamentocerutidonetti.securegroup.client.security.KeyManager;
import javax.swing.SwingUtilities;

/**
 *
 * @author Mattia
 */
public class Start {
    
    
               
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runnable chatRun = new Runnable() {
            @Override
            public void run() {
               Chat chat = Chat.get();
               /**
                * Initialise multicast side
                */
               KeyManager keyManager = new KeyManager(chat);
               Crypter crypt = new Crypter(chat, chat, keyManager);
               chat.setSender(crypt);
               /**
                * Initialise server side;
                */
               ServerDispatcher dispatcher = new ServerDispatcher(chat, chat, keyManager);
               chat.setServerDispatcher(dispatcher);
               chat.setVisible(true);
            }
        };
        
        SwingUtilities.invokeLater(chatRun);
    }
    
}
