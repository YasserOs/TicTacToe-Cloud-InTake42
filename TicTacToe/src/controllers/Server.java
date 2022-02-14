/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Person;
public class Server {
    
    static Database db ;
    ChatHandler ch;
    ServerSocket myServerSocket;
    static Vector<Person> players ;
    static Vector<Person> onlinePlayers = new Vector<Person>();
    
    public Server() throws SQLException{
     
        try {
            myServerSocket = new ServerSocket(9000);
            db = new Database();
            players = new Vector<Person>();
            players=db.getPlayers();
            while(true){
                Socket s = myServerSocket.accept();
                new ServerHandler(s);
                new ChatHandler(s);                
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static public Vector<Person> getPlayers(){
        return players;
    }
    static public void updateOnlinePlayersVector(Person p) throws SQLException{
         System.out.println("from Server"+p.getUsername());
         onlinePlayers.add(p);
       
    }
    
      static public void updateAllPlayersVector(Person p) throws SQLException{
        players.add(p);
    }
    
    public static void main(String[] args) throws SQLException {
        Server serverMulti = new Server();
        
        
    }
}
