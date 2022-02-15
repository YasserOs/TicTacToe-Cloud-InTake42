/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;
import java.net.Socket;
import java.util.Vector;
import models.*;
/**
 *
 * @author YasserOsama
 */
public class SessionManager {
    static Vector<Session> sessions ;
    Person loggedPlayer;
    public SessionManager(Socket player){
        
    }
    public static Session createSinglePlayerSession(Person p1)
    {
        return new Session(p1);
    }
    public static Session createMultiPlayerSession(Person p1 , Player p2) 
    {
        return new Session(p1,p2);
    }
    public static void saveSession(Session session){
        
    }
}
