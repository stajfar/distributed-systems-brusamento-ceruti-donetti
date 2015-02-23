/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.brusamentocerutidonetti.securegroup.server;

import it.polimi.brusamentocerutidonetti.securegroup.common.Parameters;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Mattia
 */
public class Server {
    
    private static final int port = Parameters.serverPort;
    private FlatTable flatTable;
    private RequestManager requestManager;
    private SyncQueue<Request> requests;
    
    
    public Server(){
        this.flatTable = new FlatTable();
        this.requests = new SyncQueue<>();
        this.requestManager = new RequestManager(requests, flatTable);
    }
    
    public void startServer(){
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // porta non disponibile
            return;
        }
        System.out.println("Server ready on port: " + port);
        while (true) {
            try {
                System.out.println("Waiting for a new member.");
                Socket socket = serverSocket.accept();
                System.out.println("New client arrived.");
                executor.submit(new SocketConnection(socket, this.requests));
            } catch(IOException e) {
                break; // entrerei qui se serverSocket venisse chiuso
            }
        }
        executor.shutdown();
    }
    
    
    
    public static void main(String[] args) {
        Server s = new Server();
        s.startServer();
    }
    
}
