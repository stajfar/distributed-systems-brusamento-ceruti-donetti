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
    public void lockInterface();
    public void unlockInterface();
    public void send();
}
