/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.common;

import java.io.Serializable;

/**
 *
 * @author Mattia
 */
public class Message implements Serializable{
    
    /**
     * Code that represents the type of message. 
     * Use it to define the protocol for the Client-Server communication.
     */
    private int code;
    /**
     * Objects to pass with the message, i.e. sealed keks and deks.
     */
    private Object[] body;
    /**
     * How many objects in the body.
     */
    private int lenght;
    
    
    public Message(int code, Object[] body, int lenght){
        this.code = code;
        this.body = body;
        this.lenght = lenght;
    }
    
    public Message(int code, Object body){
        this.code = code;
        this.lenght = 1;
        this.body = new Object[lenght];
        this.body[0] = body;
    }
    
    public Message(int code){
        this.code = code;
    }
    

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object[] getBody() {
        return body;
    }

    public void setBody(Object[] body) {
        this.body = body;
    }

    public int getLenght() {
        return body.length;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }
    
    
    
    
}
