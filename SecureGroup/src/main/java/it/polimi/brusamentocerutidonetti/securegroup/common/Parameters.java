/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.common;

/**
 *
 * @author Mattia
 */
public class Parameters {
    public static final int MAX_MEMBERS = 8;
    public static final int FLAT_TABLE = (int) Math.ceil(Math.log(MAX_MEMBERS)/Math.log(2));
    
    public static final String SYMM_ALGORITHM = "AES";
    public static final String ASYMM_ALGORITHM = "RSA";
    
    public static final int serverPort = 12345;
    
    /**
     * Codes for the protocol
    */
    
    /**
     * A client is requesting to join.
     */
    public static final int REQUEST_JOIN = 1;
    
    /**
     * The server refuses the join request.
     */
    public static final int REFUSED_JOIN = 2;
    
    /**
     * The server accepts the join and sends back the keys.
     */
    public static final int ACCEPTED_JOIN = 3;
    
    /**
     * The server advises that someone is joining and sends the new keys.
     */
    public static final int SOMEONE_JOINING = 4;
    
    /**
     * The server advises that someone is leaving and sends the new keys.
     */
    public static final int SOMEONE_LEAVING = 5;
    
    /**
     * The client acks the server.
     */
    public static final int ACK_UPDATE = 6;
    
    /**
     * The server acks the clients.
     */
    public static final int UPDATE_COMPLETE = 7;    
    
    /**
     * A client requests to leave.
     */
    public static final int REQUEST_LEAVE = 8;
    
    /**
     * The server confirms the leaving operation to the client.
     */
    public static final int LEAVE_COMPLETE = 9;
    
}
