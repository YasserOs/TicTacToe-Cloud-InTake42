/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import static controllers.ServerHandler.allPlayers;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Person;
import org.json.JSONObject;
public class Server 
{
    
    ServerSocket myServerSocket;
    public static Database db ;
    public static Vector<Person> players ;
    
    static{
        try {
            players = new Vector<Person>();
            db = new Database();
            players = db.getPlayers();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Server() throws SQLException{ 
        /*try {
            myServerSocket = new ServerSocket(9000);
            while(true){
                Socket s = myServerSocket.accept();
                new ServerHandler(s);
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public static Vector<Person> getPlayers(){
        return players;
    }
    public static Database getDatabase(){

        return db;
    }

    
    public static void updateAllPlayersVector(Person p) throws SQLException{
        players.add(p);
    }
    
    public static Person SignUp(JSONObject msg) throws SQLException{
        String userName = msg.getString("username");
        String email = msg.getString("email");
        String password = msg.getString("password");
        if(db.checkRegister(userName, email)){
            return null;
        } else {
            Person p = db.signUp(userName, password, email);
            updateAllPlayersVector(p);
            return p;
        }
    }
    public static Person SignIn(JSONObject msg) throws SQLException{
        String userName = msg.getString("username");
        String password = msg.getString("password");
    
        if(!db.logIn(userName, password)){
            return null;
        } else{  
            db.updatePlayerStatus(userName, "online");
            updateplayer(userName, "online");
           return db.getPlayer(userName);
                 
        }
    }
    public static void main(String[] args) throws SQLException {
        Server serverMulti = new Server();
        
        
    }
    
    public static void updateplayer(String userName, String status) 
    {       
        for (Person p : players) {
            if (p.getUsername().equals(userName)) 
            {
                p.setStatus(status);
                break;
            }
        }
        
    } 
}
