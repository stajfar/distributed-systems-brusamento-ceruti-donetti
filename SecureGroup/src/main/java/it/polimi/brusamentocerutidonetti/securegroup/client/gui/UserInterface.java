/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.gui;

/**
 *
 * @author Mattia
 */
public interface UserInterface {
    public void receiveMessage(String msg);
    
    public void joinAccepeted();
    public void requestJoin();
    public void refusedJoin();
    
    public void requestLeave();
    public void leaveAccepetd();
    
    public void lockForUpdate();
    public void updateComplete();
    
    public void send();
}
