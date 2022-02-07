/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Vector;

public class ChatHandler extends Thread{
 
    DataInputStream dis ;
    PrintStream ps;
    static Vector<ChatHandler> clients = new Vector<ChatHandler>();
    public ChatHandler(Socket clientSocket) throws IOException{              
        dis = new DataInputStream(clientSocket.getInputStream());
        ps = new PrintStream(clientSocket.getOutputStream());
        clients.add(this);
        sendMsgToAll("Client Id : "+this.getId()+" Entered Chat Room ");        
        start();
    }
    public void run(){        
        while(true){            
            try {
                if(dis.read()==-1){
                    sendMsgToAll("Client : "+this.getId()+" Has left the chat .");
                    clients.remove(this);
                    this.stop();
                }else{
                    String msg = dis.readLine();
                    sendMsgToAll(this.getId()+": "+msg);                             
                }
            } catch (IOException ex) {
                System.out.println("Client connection is dropped");
                break;
            }
        }
    }
    
    void sendMsgToAll(String msg){
        for(ChatHandler ch : clients){
                ch.ps.println(msg);                  
        }
    }
    
}
