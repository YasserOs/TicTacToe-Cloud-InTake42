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
public class Server 
{
    
    ChatHandler ch;
    ServerSocket myServerSocket;
    public static Database db ;
    public static Vector<Person> players ;
    public static Vector<Person> onlinePlayers = new Vector<Person>();
    
    static{
        try {
            players = new Vector<Person>();
            onlinePlayers = new Vector<Person>();
            db = new Database();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Server() throws SQLException{
        
        try {
            myServerSocket = new ServerSocket(9000);
            while(true){
                Socket s = myServerSocket.accept();
                new ServerHandler(s);
                new ChatHandler(s);                
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Vector<Person> getPlayers(){
        return players;
    }
    public static Database getDatabase(){

        return db;
    }
    public static void updateOnlinePlayersVector(Person p) throws SQLException{
         onlinePlayers.add(p);
       
    }

    public static void updateAllPlayersVector(Person p) throws SQLException{
        players.add(p);
    }
    
    public static void main(String[] args) throws SQLException {
        Server serverMulti = new Server();
        
        
    }
}
