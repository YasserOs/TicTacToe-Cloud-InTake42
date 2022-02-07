/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abdur
 */
public class Database {
    Connection connectionToDB;
    private  String database_connection_string = "jdbc:postgresql://localhost:5432/tic-tac-toe";
    private String user = "postgres";
    private String password = "123456";

    public Database() {
        try {
            connectionToDB =  DriverManager.getConnection(database_connection_string, user,password);
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public int connect(){
        try {
            connectionToDB =  DriverManager.getConnection(database_connection_string, user,password);
            return 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }
    public int runQuery(String query){
        
      
        try {
            PreparedStatement statement = connectionToDB.prepareStatement(query);
            statement.executeUpdate();
            statement.close();
            return 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    
    }
    public int disconnect(){
        try {
            connectionToDB.close();
             return 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    
    }
    
    public static void main(String[] args){
        Database db = new Database();
        
        
        
        String query = new String(" insert into players values('Baha Galal', 4, 9 )");
        int x= db.runQuery(query);
        System.out.println(x);
        x = db.disconnect();
        System.err.println(x);
    }
}