/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;


/**
 *
 * @author Mattia
 */
public class RequestManager implements Runnable{
    
    private SyncQueue<Request> requests;
    
    @Override
    public void run() {
        while(true){
            Request newReq = (Request) requests.poll();
            handleRequest(newReq);
        }
    }
    
    private void handleRequest(Request req){
        
    }
    
}
