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
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ServerHandler extends Thread {

    DataInputStream inputStream;
    PrintStream printStream;
    Person loggedPlayer;
    Player playertwo;
    static Vector<ServerHandler> handlers = new Vector<ServerHandler>();
    static Vector<Person> allPlayers = new Vector<Person>();
    static Vector<Session> allsession = new Vector<Session>();

    public ServerHandler(Socket clientSocket) throws IOException {
        allPlayers = Server.players;
        inputStream = new DataInputStream(clientSocket.getInputStream());
        printStream = new PrintStream(clientSocket.getOutputStream());

        handlers.add(this);
        start();

    }

    public void run() {
        while (true) {
            try {
                JSONObject msg = new JSONObject(inputStream.readLine()) ;
                try {
                    processMessage(msg);
                } catch (SQLException ex) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          
        }
    }
    
    public void closeConnection() throws IOException 
    {
        System.out.println("");
        System.out.println(loggedPlayer.getUsername() + " Closed connection !");
        Server.db.updatePlayerStatus(loggedPlayer.getUsername(), "offline");
        JSONObject msg=new JSONObject();
        msg.put("Action", "playersignout");
        msg.put("username",loggedPlayer.getUsername());
        sendMsgToAll(msg);
        handlers.remove(this);
        this.inputStream.close();
        this.printStream.close();
        this.stop();

    }
    
    public void processMessage(JSONObject msg) throws IOException, SQLException {
        String Action = msg.get("Action").toString();
        switch (Action) {
            case "SignUp":
                SignUp(msg);
                break;
            case "SignIn":
                SignIn(msg);
                //sendMsgToAll(msg);
                break;
            case "Chat":
                //sendMsgToAll(msg);
                break;
            case "In game":
                //sendMsgToAll(msg);
            case "getallplayers":
                getAllPlayers();
                break;
            default:
                //sendMsgToReceiver(msg);
                break;
        }
    }
    
    
    public void getAllPlayers()
    {
       JSONArray names = new JSONArray();
       JSONArray status =new JSONArray();
       JSONObject msg = new JSONObject();
       
       for(Person p: Server.players)
       {   
           if(!p.getUsername().equals(loggedPlayer.getUsername()))
           {
                names.put(p.getUsername());
                status.put(p.getStatus());
                
           }        
       
       }
       
       msg.put("names", names);
       msg.put("status", status);
       msg.put("Action","Playerslist");
       this.printStream.println(msg.toString());
    }

    public void SignIn(JSONObject msg) throws SQLException {
        loggedPlayer = Server.SignIn(msg);
        
        if(loggedPlayer!=null)
        {
            JSONObject reply =new JSONObject();
            reply.put("Action", "playersignin");
            reply.put("username",loggedPlayer.getUsername());
            sendMsgToAll(reply);
        }
        sendResponse("SignIn");
    }
 
    public void SignUp(JSONObject msg) throws SQLException{
        loggedPlayer = Server.SignUp(msg);
         if(loggedPlayer!=null)
        {
            JSONObject reply =new JSONObject();
            reply.put("Action", "playersignup");
            reply.put("username",loggedPlayer.getUsername());
            sendMsgToAll(reply);
        }
        sendResponse("SignUp");

    }
    public void  sendResponse(String Action){
        JSONObject response = new JSONObject();        
        if(loggedPlayer!=null){
            response.put("Action",Action);
            response.put("Response", true);
            convertPlayerToJSON(loggedPlayer,response);
            
        }else{
            response.put("Action", Action);
            response.put("Response", false);
        }
        printStream.println(response.toString());
    }
    public void convertPlayerToJSON(Person p , JSONObject json){
        json.put("username", p.getUsername());
        json.put("score", p.getScore());
        json.put("status", p.getStatus());
        json.put("wins", p.getGames_won());
        json.put("games", p.getGames_played());
        json.put("draws", p.getDraws());
        json.put("losses", p.getGames_lost());
    }
    // send to receiver
    // send response back to sender
    public void sendMsgToReceiver(Message msg) {
        
        for (ServerHandler sh : handlers) {
            if (msg.getReceiver().equals(sh.loggedPlayer.getUsername())) {
                sh.printStream.println();
                break;
            }
        }
    }

    public void loginPlayer(String userName) throws IOException {
        for (Person p : allPlayers) {
            if (p.getUsername().equals(userName)) {
                p.setStatus("online");
                loggedPlayer = p;
                break;
            }
        }

    }

    void sendMsgToAll(JSONObject msg) 
    {
        for (ServerHandler sh : handlers) {
            if(!sh.loggedPlayer.getUsername().equals(loggedPlayer.getUsername()))
               sh.printStream.println(msg.toString());
        }
    }

    public static void saveSession(Session session) {

    }

}
