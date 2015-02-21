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
    public void leaveAccepetd();
    public void lockJoin();
    public void unlockJoin();
    public void lockSend();
    public void unlockSend();
    public void send();
}
