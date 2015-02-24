/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.client.communication;

import it.polimi.brusamentocerutidonetti.securegroup.client.gui.Logger;
import it.polimi.brusamentocerutidonetti.securegroup.client.gui.UserInterface;
import it.polimi.brusamentocerutidonetti.securegroup.client.security.KeysManager;
import it.polimi.brusamentocerutidonetti.securegroup.common.Message;
import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.security.Key;
import javax.crypto.SealedObject;

/**
 *
 * @author Mattia
 */
public class ServerDispatcher implements MessageHandler{
    
    public static final String serverIP = "localhost";
    public static final int port = Parameters.serverPort;
    
    private KeysManager km;
    private MessageSender ms;
    private Logger logger;
    private UserInterface ui;
    
    public ServerDispatcher(UserInterface ui, Logger log, KeysManager km){
        this.ui = ui;
        this.logger = log;
        this.km = km;
    }
    
    public void requestJoin(){
        logger.log("Waiting to join...");
        try {
            Socket s = new Socket(serverIP, port);
            ms = new ServerSender(s);
            new Thread(new ServerReceiver(s, this)).start();
            Key publicKey = km.getPublicKey();
            Message msg = new Message(Parameters.REQUEST_JOIN, publicKey);
            ms.sendMessage(msg, Message.class);
        } catch (IOException ex) {
            logger.error(getClass().toString() + ": Server not found.");
            ui.refusedJoin();
        }
    }
    
    public void requestLeave(){
        logger.log("Waiting to leave...");
        Message msg = new Message(Parameters.REQUEST_LEAVE);
        ms.sendMessage(msg, Message.class);
    }
    
    /**
     * Handles the messages from the Server depending on the code.
     * @param o the object received by the Receiver.
     */
    @Override
    public void handleMessage(Object o) {
        if(!(o instanceof Message)){
            logger.error(getClass() + ": Don't know what received from server. Fuck!");
        }
        Message msg = (Message) o;
        int code = msg.getCode();
        switch(code){
            //Me joining
            case(Parameters.REFUSED_JOIN): refusedJoin(); break;
            case(Parameters.ACCEPTED_JOIN): acceptedJoin(msg); break;
            //Someone else is joining/leaving
            case(Parameters.SOMEONE_JOINING): someoneJoining(msg); break;
            case(Parameters.SOMEONE_LEAVING): someoneLeaving(msg); break;
            case(Parameters.UPDATE_COMPLETE): updateComplete(); break;
            //I have left
            case(Parameters.LEAVE_COMPLETE): leaveComplete(); break;
        }
    }
    
    
    
    private void refusedJoin(){
        logger.error(getClass() + ": You join request has been refused. Try again later.");
        ui.refusedJoin();
    }
    
    /**
     * The request has been accepted and I receive my new beautiful keys. 
     * @param msg the message containing my keys in the body.
     */
    private void acceptedJoin(Message msg){
        int lenght = msg.getLenght();
        if(lenght < (Parameters.FLAT_TABLE + 1)){
            logger.error(getClass() + ": Error receiving keys.");
            ui.joinAccepeted();
            return;
        }
        Object[] body = msg.getBody();
        SealedObject[] keks = new SealedObject[Parameters.FLAT_TABLE];
        int i=0;
        for (; i<Parameters.FLAT_TABLE; i++){
            keks[i] = (SealedObject) body[i];
        }
        SealedObject dek = (SealedObject) body[i];
        km.initialise(keks, dek);
        logger.log("YOU HAVE JOINED THE GROUP!");
        ui.joinAccepeted();
    }
    
    /**
     * Someone is joining, the server gives me the new keys, I update them and wait for the other members.
     * @param msg the message with the new keys.
     */
    private void someoneJoining(Message msg) {
        ui.lockForUpdate();
        logger.log(getClass() + ": Someone is joining the group!");
        int lenght = msg.getLenght();
        if(lenght < (Parameters.FLAT_TABLE + 1)){
            logger.error(getClass() + ": Error receiving keys.");
            return;
        }
        Object[] body = msg.getBody();
        SealedObject[] keks = new SealedObject[Parameters.FLAT_TABLE];
        int i=0;
        for (; i<Parameters.FLAT_TABLE; i++){
            keks[i] = (SealedObject) body[i];
        }
        SealedObject dek = (SealedObject) body[i];
        km.updateOnJoin(keks, dek);
        Message ack = new Message(Parameters.ACK_UPDATE);
        ms.sendMessage(ack, Message.class);
    }
    
    /**
     * Someone is leaving, the server gives me the new keys, I update them and wait for the other members.
     * @param msg the message with the new keys.
     */
    private void someoneLeaving(Message msg) {
        ui.lockForUpdate();
        logger.log(getClass() + ": Someone is leaving the group!");
        int lenght = msg.getLenght();
        if(lenght < (2*Parameters.FLAT_TABLE)){
            logger.error(getClass() + ": Error receiving keys.");
            return;
        }
        Object[] body = msg.getBody();
        SealedObject[] keks = new SealedObject[Parameters.FLAT_TABLE];
        SealedObject[] deks = new SealedObject[Parameters.FLAT_TABLE];
        int i=0;
        for (; i<Parameters.FLAT_TABLE; i++){
            keks[i] = (SealedObject) body[i];
        }
        for (int j=0; j<Parameters.FLAT_TABLE; j++){
            deks[j] = (SealedObject) body[i+j];
        }
        km.updateOnLeave(keks, deks);
        Message ack = new Message(Parameters.ACK_UPDATE);
        ms.sendMessage(ack, Message.class);
    }
    
    /**
     * All members have update their keys.
     */
    private void updateComplete() {
        logger.log("OPERATION COMPLETE!");
        km.confirmUpdate();
        ui.updateComplete();
    }

    private void leaveComplete() {
        logger.log("YOU HAVE LEFT THE GROUP!");
        ui.leaveAccepetd();
    }

    
    
}
