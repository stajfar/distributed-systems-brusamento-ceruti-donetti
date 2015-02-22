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
    
    /**
     * Codes for the protocol
    */
    //JOIN REQUEST
    public static final int REQUEST_JOIN = 1;
    public static final int REFUSED_JOIN = 2;
    public static final int ACCEPTED_JOIN = 3;
    //SOMEONE JOINS/LEAVES
    public static final int SOMEONE_JOINING = 4;
    public static final int SOMEONE_LEAVING = 5;
    public static final int ACK_UPDATE = 6;
    public static final int UPDATE_COMPLETE = 7;    
    //LEAVE REQUEST
    public static final int REQUEST_LEAVE = 8;
    public static final int LEAVE_COMPLETE = 9;
    
}
