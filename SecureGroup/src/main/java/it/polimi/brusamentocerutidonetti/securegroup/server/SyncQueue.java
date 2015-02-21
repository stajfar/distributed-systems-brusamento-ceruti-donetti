/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Mattia
 * @param <T>
 */
public class SyncQueue<T> extends LinkedBlockingQueue<Object>{
    
    @Override
    public synchronized Object poll(){
        while(super.poll() == null){
            try {
                wait();
            } catch (Exception e) {
            }
        }
        Object element = super.poll();
        notifyAll();
        return element;
    }
    
    @Override
    public synchronized boolean offer(Object o){
        while(!(super.offer(o))){
            try {
                wait();
            } catch (Exception e) {
            }
        }
        notifyAll();
        return true;
    }
}
