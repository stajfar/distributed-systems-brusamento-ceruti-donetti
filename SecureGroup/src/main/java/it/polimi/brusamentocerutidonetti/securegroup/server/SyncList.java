/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mattia
 * @param <T>
 */
public class SyncList<T>{
    
    private List<Object> list;
    
    public SyncList(){
        this.list = new ArrayList<>();
    }
    
    
    public boolean contains(Object o){
        return list.contains(o);
    }
    
    public synchronized void add(Object element){
        if(list.contains(element)){
            return;
        }else{
            list.add(element);
            notifyAll();
        }
    }
    
    public int size(){
        return list.size();
    }
    
    public synchronized void free(){
        list = new ArrayList<>();
    }
    
    
    public synchronized void waitForCapacity(int size){
        while(list.size() < size){
            try {
                wait();
            } catch (Exception e) {
            }
        }
    }
}
