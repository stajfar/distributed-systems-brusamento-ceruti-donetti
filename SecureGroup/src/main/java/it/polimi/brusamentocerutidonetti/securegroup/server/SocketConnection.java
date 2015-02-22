/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mattia
 */
public class SocketConnection implements Connection, Runnable{
    
    private int ID;
    private Socket s;
    private ObjectInputStream i;
    private ObjectOutputStream o;
    private Queue<Request> q;
    private boolean open;

    @Override
    public void send(Message m){
        try {
            o.writeObject(m);
            o.flush();
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void run() {
        setOpen(true);
        while(isOpen()){
            try {
                Message m = (Message) i.readObject();
                q.offer(new Request(this, m));
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void close() {
        setOpen(false);
    }
    
    @Override
    public void setID(int ID) {
        this.ID = ID;
    }
    
    private synchronized boolean isOpen(){
        return open;
    }
    
    private synchronized void setOpen(boolean open){
        this.open = open;
    }
}
