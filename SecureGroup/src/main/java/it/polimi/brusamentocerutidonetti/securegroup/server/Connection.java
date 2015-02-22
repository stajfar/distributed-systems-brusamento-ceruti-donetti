/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Message;

/**
 *
 * @author Mattia
 */
public interface Connection {
    public void send (Message m);
    public int getID();
    public void setID(int ID);
    public void close();
}
