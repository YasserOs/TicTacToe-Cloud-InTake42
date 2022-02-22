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

public class ServerHandler extends Thread {

    InputStream inputStream;
    OutputStream outputStream;
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    Person loggedPlayer;
    Player playertwo;
    static Vector<ServerHandler> handlers = new Vector<ServerHandler>();
    static Vector<Person> allPlayers = new Vector<Person>();
    static Vector<Session> allsession = new Vector<Session>();

    public ServerHandler(Socket clientSocket) throws IOException {
        allPlayers = Server.players;
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        objectOutputStream = new ObjectOutputStream(outputStream);
        objectInputStream = new ObjectInputStream(inputStream);
        handlers.add(this);
        start();

    }

    public void run() {
        while (true) {
            try {
                Message msg = (Message) objectInputStream.readObject();
                processMessage(msg);
            } catch (IOException ex) {
                try {
                    closeConnection();
                } catch (IOException ex1) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (ClassNotFoundException ex) {
                System.out.println("");;
            }
        }
    }
    
    public void closeConnection() throws IOException 
    {
        System.out.println(loggedPlayer.getUsername() + " Closed connection !");
        Server.db.updatePlayerStatus(loggedPlayer.getUsername(), "offline");
        handlers.remove(this);
        Message msg = new Message("LogOut","","","");
        sendMsgToAll(msg);
        this.objectInputStream.close();
        this.objectOutputStream.close();
        this.stop();
    }
    
    public void processMessage(Message msg) throws IOException {
        String Action = msg.getAction();
        switch (Action) {
            case "LoggedIn":
                loginPlayer(msg.getSender());
                sendMsgToAll(msg);
                break;
            case "Chat":
                sendMsgToAll(msg);
                break;
            case "In game":
                sendMsgToAll(msg);
            default:
                sendMsgToReceiver(msg);
                break;
        }
    }

    // send to receiver
    // send response back to sender
    public void sendMsgToReceiver(Message msg) {
        
        for (ServerHandler sh : handlers) {
            try {
                if (msg.getReceiver().equals(sh.loggedPlayer.getUsername())) {
                            sh.objectOutputStream.writeObject(msg);
                            break;
                    }
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
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

    void sendMsgToAll(Message msg) {
        for (ServerHandler sh : handlers) {
            try {
                sh.objectOutputStream.writeObject(msg);
            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void saveSession(Session session) {

    }

}
