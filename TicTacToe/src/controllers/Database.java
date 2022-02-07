package controllers;

import models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

public class Database {

    ResultSet rs;
    Connection conn;
    private  String url = "jdbc:postgresql://localhost:5432/tic-tac-toe";
    private String user = "postgres";
    private String password = "123456";

    public Database() throws SQLException{
        connect();
    }
    
    public void connect() throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            
            System.out.println("Connected to the PostgreSQL server successfully. ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public Vector<Person> getPlayers() throws SQLException {
        Vector<Person> players = new Vector<Person>();
        Statement stmt = conn.createStatement();
        String queryString = new String("select * from players");
        rs = stmt.executeQuery(queryString);
        do {
            Person p = createPerson(rs);
            players.add(p);

        } while (rs.next() != false);
        return players;
        }
    

    public Person createPerson(ResultSet rs) throws SQLException {
        Person p = new Person(rs.getString(1),rs.getString(4),rs.getInt(3),rs.getString(5),rs.getString(6),rs.getInt(7),rs.getInt(8),rs.getInt(9),rs.getInt(10),rs.getInt(11));      
        return p;
    }

    public void removeRecord(int ID) throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String queryString = new String("DELETE FROM contact WHERE id=" + ID + ";");
            stmt.executeUpdate(queryString);
            System.out.println("Deleted successfully. ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        conn.close();
    }

    public boolean insertNewContact(Person p) throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String queryString = new String("insert into players (username,email,password,status,last_seen,total_score,games_played,games_won,games_lost,draws)  values (" + p.getUsername() + ",'" + p.getEmail() + "','" + p.getPassword() + "','" + p.getStatus() + "','" + p.getLast_seen() + "','" + p.getTotal_score()+ "');");
            int numberOfUpdatedCols = stmt.executeUpdate(queryString);
            System.out.println("Record successfully Inserted. ");
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
        }

        conn.close();

        return true;
    }

}
