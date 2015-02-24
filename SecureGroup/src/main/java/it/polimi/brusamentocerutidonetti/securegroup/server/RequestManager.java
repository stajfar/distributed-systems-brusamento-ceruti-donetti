/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.SealedObject;


/**
 *
 * @author Mattia
 */
public class RequestManager implements Runnable{
    
    private SyncQueue<Request> requests;
    private SyncList<Integer> ackList;
    private List<Connection> members;
    private FlatTable ft;

    public RequestManager(SyncQueue<Request> requests, FlatTable ft) {
        this.requests = requests;
        this.ft = ft;
        members = new ArrayList<>();
        ackList = new SyncList<>();
    }
    
    
    
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
            case (Parameters.REQUEST_JOIN):  handleJoin(req); break;
            case (Parameters.REQUEST_LEAVE): handleLeave(req); break;
        }
    }
    
    
    
    /**
     * Manage the operations to do when a new member joins.
     * @param req 
     */
    private synchronized void handleJoin(Request req) {
        Connection c = req.getConnection();
        int id = findAvailableID();
        if(id < 0){
            c.send(new Message(Parameters.REFUSED_JOIN));
            c.close();
        }else{
            /**
             * Send encrypted keys to the other members.
             */
            c.setID(id);
            ft.updateKeys(id);
            for(Connection m: members){
                SealedObject[] keys = ft.getEncryptedKeysJoin(m.getID());
                Message msg = new Message(Parameters.SOMEONE_JOINING, keys, keys.length);
                m.send(msg);
            }
            /**
             * Wait for all the acks.
             */
             ackList.waitForCapacity(members.size());
            /**
             * Confirm the update to all the members and ack the new one.
             */
            for (Connection m : members) {
                m.send(new Message(Parameters.UPDATE_COMPLETE));
            }
            Object[] body = req.getMessage().getBody();
            SealedObject[] keys = ft.getKeysNewMember((Key) body[0], id);
            c.send(new Message(Parameters.ACCEPTED_JOIN, keys, keys.length));
            members.add(c);
            ackList.free();
        }
    }
    
    
 
    /**
     * Manage the operations to do when a new member leaves.
     * @param req 
     */
    private synchronized void handleLeave(Request req) {
        Connection c = req.getConnection();
        int leaveID = c.getID();
        members.remove(c);
        
        ft.updateKeys(leaveID);
        SealedObject[] deks = ft.getDEKsAfterLeave(leaveID);
        SealedObject[] keys = new SealedObject[2*Parameters.FLAT_TABLE];
        for(int i=0; i<deks.length; i++){
            keys[i+Parameters.FLAT_TABLE] = deks[i];
        }
        /**
         * Send the keys to each member.
         */
        for (Connection m : members) {
            SealedObject[] keks = ft.getKEKsLeave(m.getID());
            for(int i=0; i<keks.length; i++){
                keys[i] = keks[i];
            }
            m.send(new Message(Parameters.SOMEONE_LEAVING, keys, keys.length));
        }
        /**
         * Wait for the acks.
         */
        ackList.waitForCapacity(members.size());
        /**
         * Confirm the update
         */
        for (Connection m : members) {
            m.send(new Message(Parameters.UPDATE_COMPLETE));
        }
        c.send(new Message(Parameters.LEAVE_COMPLETE));
        ackList.free();
        c.close();
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

    public SyncList<Integer> getAckList() {
        return ackList;
    }
    
    
    
    
}
