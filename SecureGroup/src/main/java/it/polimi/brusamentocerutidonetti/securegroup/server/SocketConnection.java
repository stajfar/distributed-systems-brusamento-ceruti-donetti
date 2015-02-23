/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
    private SyncQueue<Request> q;
    private RequestHandler h;
    private boolean open;
    
    public SocketConnection(Socket s, SyncQueue<Request> q){
        try {
            this.q = q;
            this.s = s;
            i = new ObjectInputStream(s.getInputStream());
            o = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
                if(m.getCode() == Parameters.ACK_UPDATE){
                    h.updateAcked(ID);
                }else{
                    q.offer(new Request(this, m));
                }
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
