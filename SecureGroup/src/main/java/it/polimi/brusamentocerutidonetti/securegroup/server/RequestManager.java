/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.util.List;


/**
 *
 * @author Mattia
 */
public class RequestManager implements Runnable, RequestHandler{
    
    private SyncQueue<Request> requests;
    private List<Long> ackList;
    private List<Connection> members;
    
    @Override
    public void run() {
        while(true){
            Request newReq = (Request) requests.poll();
            handleRequest(newReq);
        }
    }
    
    private void handleRequest(Request req){
        int code = req.getMessage().getCode();
        switch(code){
            case (Parameters.REQUEST_JOIN): handleJoin(req); break;
        }
    }

    @Override
    public synchronized void updateAcked(long ID) {
        
    }

    private synchronized void handleJoin(Request req) {
        Connection c = req.getConnection();
        int id = findAvailableID();
        if(id < 0){
            c.send(new Message(Parameters.REFUSED_JOIN));
            c.close();
        }else{
            
        }
    }
    
    
    
    private int findAvailableID(){
        if(members.size() < Parameters.MAX_MEMBERS) {
            boolean[] ids = new boolean[Parameters.MAX_MEMBERS];
            for (Connection member : members) {
                ids[member.getID()] = true;
            }
            for(int i=0; i<Parameters.MAX_MEMBERS; i++){
                if(ids[i] == false){
                    return i;
                }
            }
        }
        return -1;
    }
    
    
    
}
