/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;

import it.polimi.brusamentocerutidonetti.securegroup.client.communication.MessageSender;
import java.awt.TextArea;
import javax.swing.JPanel;


/**
 *
 * @author Mattia
 */
public class Chat extends JPanel implements UserInterface{
    
    private String username;
    private SendButton sendButton;
    private JoinButton joinButton;
    private TextArea screenArea, writingArea;
    private MessageSender sender;
    
    @Override
    public void receiveMessage(String msg) {
        this.print(msg);
    }

    @Override
    public void lockInterface() {
        sendButton.setEnabled(false);
        joinButton.setEnabled(false);
    }

    @Override
    public void unlockInterface() {
        sendButton.setEnabled(true);
        joinButton.setEnabled(true);
    }

    
    private synchronized void print(String msg) {
        screenArea.append(msg);
    }

    @Override
    public void send() {
        String msg = writingArea.getText();
        if(msg.length()>0){
           msg = username + ": <<" + msg + ">>";
           writingArea.setText(null);
           this.print(msg);
           sender.sendMessage(msg, String.class);
        }
    }


}
