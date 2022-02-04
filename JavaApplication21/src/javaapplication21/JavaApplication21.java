/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import java.sql.*;
import java.sql.Driver;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author abdur
 */
public class Database {
    Connection con;
    private  String database_connection_string = "jdbc:postgresql://localhost:5432/addressbook";

    private  String database_user_name = "postgres";

    private  String database_user_password = "123456";    
    public Database() {
        try {
            con = DriverManager.getConnection(database_connection_string,
                    database_user_name,database_user_password);
            System.out.println("Success connected");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            Statement stmt= con.createStatement();
             String query =new String("select * from contact");
             ResultSet rs= stmt.executeQuery(query);
             while(rs.next()){
                 System.out.println(rs.getString(1));
                 System.out.println(rs.getString(2));
                 System.out.println(rs.getString(3));
                 System.out.println(rs.getString(4));
                 System.out.println(rs.getString(5));
                 System.out.println(rs.getString(6));
                 System.out.println(rs.getString(7));
             }
             stmt.close();
             con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
       
         
    }
    public static void main(String[] args){
        Database db =new Database();
        
    }
    
}
