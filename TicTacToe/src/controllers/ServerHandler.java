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
    Session s;
    static Vector<ServerHandler> handlers = new Vector<ServerHandler>();
    static Vector<Person> allPlayers = new Vector<Person>();
    static Vector<Person> onlinePlayers = new Vector<Person>();
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

    public void closeConnection() throws IOException {
        System.out.println(loggedPlayer.getUsername() + " Closed connection !");
        Server.onlinePlayers.remove(loggedPlayer);
        onlinePlayers.remove(loggedPlayer);
        handlers.remove(this);
        this.objectInputStream.close();
        this.objectOutputStream.close();
        this.stop();
    }

    public void processMessage(Message msg) {
        String Action = msg.getAction();
        switch (Action) {
            case "LoggedIn":
                loginPlayer(msg.getSender());
            case "Chat":
                sendMsgToAll(msg);
                break;
            case "Invite":
                handleInvitation(msg);
                break;
            case "play":
                s = new Session(loggedPlayer, playertwo);
                break;
            case "move":
                break;

        }
    }

    // send to receiver
    // send response back to sender
    public void handleInvitation(Message msg) {
        String content = msg.getContent();
        for (ServerHandler sh : handlers) {
            try {
                switch (content) {
                    case "Pending":
                        if (msg.getReceiver().equals(sh.loggedPlayer)) {
                            sh.objectOutputStream.writeObject(msg);
                        }
                        break;

                }

            } catch (IOException ex) {
                Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void loginPlayer(String userName) {
        for (Person p : allPlayers) {
            if (p.getUsername().equals(userName)) {
                p.setStatus("Online");
                onlinePlayers.add(p);
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

    public void SessionManager(Socket player) {

    }

    public static Session createSinglePlayerSession(Person p1) {
        return new Session(p1);
    }

    public static Session createMultiPlayerSession(Person p1, Player p2) {
        return new Session(p1, p2);
    }

    public static void saveSession(Session session) {

    }

}
