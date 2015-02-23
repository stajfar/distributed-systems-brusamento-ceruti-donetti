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
public class Chat extends JPanel implements UserInterface, Logger{
    
    private String username;
    private SendButton sendButton;
    private JoinButton joinButton;
    private TextArea screenArea, writingArea;
    private MessageSender sender;
    
    
    
    @Override
    public void receiveMessage(String msg) {
        this.print(msg);
    }

    
    private synchronized void print(String msg) {
        screenArea.append(msg + "\n");
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

    @Override
    public void error(String e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void log(String l) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void joinAccepeted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void requestJoin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void leaveAccepetd() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockJoin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlockJoin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockSend() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlockSend() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
