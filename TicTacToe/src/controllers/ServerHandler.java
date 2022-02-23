/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import static controllers.Server.db;
import models.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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
                processMessage(msg);            
            } catch (IOException ex) {
                
                closeConnection();
                break;
            } catch (SQLException ex){
                System.out.println("FromSQL");
            }
          
        }
    }
    
    public void closeConnection() 
    {
        System.out.println(loggedPlayer.getUsername() + " Closed connection !");
        Server.db.updatePlayerStatus(loggedPlayer.getUsername(), "offline");
        Server.updateplayer(loggedPlayer.getUsername(), "offline");
        System.out.println("close connsection ");
        JSONObject msg=new JSONObject();
        msg.put("Action", "playersignout");
        msg.put("username",loggedPlayer.getUsername());
        sendMsgToAll(msg);
        
        handlers.remove(this);
        try {
            this.inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.printStream.close();
        this.stop();

    }
    
    public void processMessage(JSONObject msg) throws IOException, SQLException {
        String Action = msg.getString("Action");
        switch (Action) {
            case "SignUp":
                SignUp(msg);
                break;
            case "SignIn":
                SignIn(msg);
                break;
            case "BroadcastChat":
                sendMsgToAll(msg);
                break;
            case "In game":
                //sendMsgToAll(msg);
            case "getallplayers":
                getAllPlayers();
                break;
            case "playerStartedMatch" :
                changeplayerstatus("playerStartedMatch","in-game");
                break;
            case "playerFinishMatch":
                changeplayerstatus("playerFinishMatch","online");
                break;
            default:
                sendMsgToReceiver(msg);
                break;
        }
    }
    
    public void changeplayerstatus(String action,String status)
    {
        Server.updateplayer(loggedPlayer.getUsername(),status);
        JSONObject msg= new JSONObject();
        msg.put("Action",action);
        msg.put("username",loggedPlayer.getUsername());
        sendMsgToAll(msg);
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
   
    public void SignIn(JSONObject msg) throws SQLException, JSONException {
        int flag= Server.SignIn(msg);
        if( flag == 1)
        {  
            loggedPlayer = db.getPlayer(msg.getString("username"));
            System.out.println("From Signin Server handler" + loggedPlayer.getUsername());
            JSONObject reply = new JSONObject();
            reply.put("Action", "playersignin");
            reply.put("username", loggedPlayer.getUsername());
            sendMsgToAll(reply);
        }
        sendResponse("SignIn", flag);
    }
 
    public void SignUp(JSONObject msg) throws SQLException, JSONException{
        int flag = Server.SignUp(msg);
        if (flag == 1) 
        {
            loggedPlayer = db.getPlayer(msg.getString("username"));
            System.out.println("From Signin Server handler" + loggedPlayer.getUsername());
            JSONObject reply = new JSONObject();
            reply.put("Action", "playersignup");
            reply.put("username", loggedPlayer.getUsername());
            sendMsgToAll(reply);
        }
        sendResponse("SignUp", flag);

    }
    public void  sendResponse(String Action, int flagDB) throws JSONException{
        JSONObject response = new JSONObject();  
        
        if(loggedPlayer!=null){
            System.out.println("From sendResponse Server handler" + loggedPlayer.getUsername());
            response.put("Action",Action);
            //response.put("Response", true);
            response.put("Response", flagDB);
            convertPlayerToJSON(loggedPlayer,response);
            
        }else{
            response.put("Action", Action);
            //response.put("Response", false);
            response.put("Response", flagDB);
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
    public void sendMsgToReceiver(JSONObject msg) {
        
        for (ServerHandler sh : handlers) {
            if (msg.getString("Receiver").equals(sh.loggedPlayer.getUsername())) {
                sh.printStream.println(msg.toString());
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
