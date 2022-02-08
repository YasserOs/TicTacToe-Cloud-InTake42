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
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Person;
public class Server {
    
    Database db ;
     ResultSet rs;
    ChatHandler ch;
    ServerSocket myServerSocket;
    public Server() throws SQLException{
        try {
            myServerSocket = new ServerSocket(5000);
            db = new Database();
            db.connect();
           // db.createPerson(rs);
            db.signUp("ahmed","00456456d","koko@meo.com");
            /*while(true){
                Socket s = myServerSocket.accept();
                new ChatHandler(s);                
            }*/
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
//Check SignUp
    
    
    public static void main(String[] args) throws SQLException {
        Server serverMulti = new Server();
        
        
    }
}
