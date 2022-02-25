/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;


import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.DisplayPlayers;
import models.Person;
import org.json.JSONException;
import org.json.JSONObject;

public class Server 
{
    
    public static ServerSocket myServerSocket;
    public static Thread th;
    public static Database db ;
    public static Vector<Person> players ;
    public static ObservableList<DisplayPlayers> Playerslist=FXCollections.observableArrayList();
  //public static Vector<JSONObject> savedSessions = new Vector<JSONObject>();

    static{
        try {
            players = new Vector<Person>();
            db = new Database();
            players = db.getPlayers();
            Playerslist=db.displayPlayers();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Server() {
        
    }
    
    
    public static Vector<Person> getPlayers(){
        return players;
    }
    public static Database getDatabase(){

        return db;
    }

    
    public static void updateAllPlayersVector(Person p) throws SQLException{
        players.add(p);
        Playerslist.add(new DisplayPlayers(p.getUsername(),"online"));
    }
    
  public static int SignUp(JSONObject msg) throws SQLException, JSONException{
        String userName = msg.getString("username");
        String email = msg.getString("email");
        String password = msg.getString("password");
        if(!playerExists(userName)){
            int flagFromDB = db.checkRegister(userName, email);
                    switch(flagFromDB){
                        case 1: // Both username and email not used 
                             Person p = db.signUp(userName, password, email);
                              updateAllPlayersVector(p);
                            return flagFromDB;
                        case 2:  //email already used
                            return flagFromDB;
                        case 3:   // error occured while connecting to the datebase
                            return flagFromDB;
                    }
            }
        return 0;// username already used   
    }
    public static int SignIn(JSONObject msg) throws SQLException, JSONException{
        String userName = msg.getString("username");
        String password = msg.getString("password");
        if(playerExists(userName)){
           int flagFromDB = db.logIn(userName, password);
                switch(flagFromDB){
                    case 1: //the username and pw are correct success 
                        db.updatePlayerStatus(userName, "online");
                        updateplayer(userName, "online");
                        return flagFromDB;
                    case 2:  // the password is incorrect
                        return flagFromDB;
                    case 3:  //the user is already logged/ online
                        return flagFromDB;
                    case 4: // error occured while connecting to the datebase
                        return flagFromDB;
                }
        }
        return 0;// player Dosen't exist or username is incorrect   
    }
    
      public static boolean playerExists(String username){
        for (Person player : players) {
            if(player.getUsername().equals(username)){
                return true;
            }
        }
    
       return false; 
    }
      
    public static void updateplayer(String userName, String status) 
    {       
        for (Person p : players)
        {
            if (p.getUsername().equals(userName)) 
            {
                p.setStatus(status);
                updateObservablePlayerslist(userName,status);
                break;
            }
        }
        
    }
    public static void updateplayerScore(Person player) 
    {       
        for (Person p : players)
        {
            if (p.getUsername().equals(player.getUsername())) 
            {
                players.set(players.indexOf(p), player);
                break;
            }
        }
        
    }
    public static void updateObservablePlayerslist(String userName , String status)
    {
        for(int i=0 ; i <Playerslist.size();i++){
            if(Playerslist.get(i).getName().equals(userName)){
                Playerslist.set(i, new DisplayPlayers(userName,status));
                break;
            }
        }
    }
    public static void getPlayer(String username){
    
    }
}
