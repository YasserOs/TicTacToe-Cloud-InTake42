/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import models.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerHandler extends Thread{
 
    InputStream inputStream;
    OutputStream outputStream ;
    ObjectOutputStream objectOutputStream ;
    ObjectInputStream objectInputStream;
    String loggedPlayer;
    static Vector<ServerHandler> handlers = new Vector<ServerHandler>();
    static Vector<Person> allPlayers = new Vector<Person>();
    static Vector<Person> onlinePlayers = new Vector<Person>();
    public ServerHandler(Socket clientSocket) throws IOException{
        allPlayers = Server.players;
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        objectInputStream = new ObjectInputStream(inputStream);
        handlers.add(this);
        start();
    }
    public void run(){
        while(true){
                    try {
                        Message msg = (Message)objectInputStream.readObject();
                        processMessage(msg);
                    } catch (IOException ex) {
                        System.out.println("IO");
                    } catch (ClassNotFoundException ex) {
                        System.out.println("");;
                    }
                } 
    }
    public void processMessage(Message msg){
        String Action =msg.getAction(); 
        switch(Action){
            case "LoggedIn":
                loginPlayer(msg.getSender());
            case "Chat":
                sendMsgToAll(msg);
                break;
            case "Invite":
                handleInvitation(msg);

        }
    }
    
    // send to receiver
    // send response back to sender
    public void handleInvitation(Message msg){
        String content = msg.getContent();
        for(ServerHandler sh : handlers){
            try {
                switch(content){
                    case "Pending":
                        if(msg.getReceiver().equals(sh.loggedPlayer)){
                            sh.objectOutputStream.writeObject(msg);
                        }
                        break;
                    
                }
                             
            } catch (IOException ex) {
                Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void loginPlayer(String userName){
        loggedPlayer = userName;
        for(Person p : allPlayers){
            if(p.getUsername().equals(loggedPlayer)){
                p.setStatus("Online");
                onlinePlayers.add(p);
            }
        }
        
    }
    void sendMsgToAll(Message msg){
        for(ServerHandler sh : handlers){
            try {                  
                sh.objectOutputStream.writeObject(msg);
            } catch (IOException ex) {
                Logger.getLogger(ChatHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
