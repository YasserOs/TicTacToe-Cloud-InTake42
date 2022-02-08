package controllers;

import models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

public class Database {

    ResultSet rs;
    Connection conn;
    private String url = "jdbc:postgresql://localhost:5432/tic-tac-toe";
    private String user = "postgres";
    private String password = "123456";

    public Database() throws SQLException {
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
        conn = DriverManager.getConnection(url, user, password);
        Statement stmt = conn.createStatement();
        String queryString = new String("select * from players");
        rs = stmt.executeQuery(queryString);
        while (rs.next()) {
            Person p = createPerson(rs);
            players.add(p);

        }
        return players;
    }

    public Person createPerson(ResultSet rs) throws SQLException {

        Person p = new Person(rs.getString(1), rs.getString(4), rs.getInt(3), rs.getString(5), rs.getDate(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11));
        return p;
    }

    // Person p = new Person();
    public void removeRecord(int ID) throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            String queryString = new String("DELETE FROM players WHERE id=" + ID + ";");
            stmt.executeUpdate(queryString);
            System.out.println("Deleted successfully. ");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        conn.close();

    }

    //Hashed and Encrypted Password
    public String passwordEnc(String password) {

        String encryptedpassword = null;
        try {
            /* MessageDigest instance for MD5. */
            MessageDigest m = MessageDigest.getInstance("MD5");
            /* Add plain-text password bytes to digest using MD5 update() method. */
            m.update(password.getBytes());
            /* Convert the hash value into bytes */
            byte[] bytes = m.digest();
            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                s.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            /* Complete hashed password in hexadecimal format */
            encryptedpassword = s.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptedpassword;

    }

    // Sign Up Function
    public boolean signUp(String username, String pswd, String email) throws SQLException {
        try {
            conn = DriverManager.getConnection(url, user, password);
            String queryString = new String("insert into players"
                    + " (username,password,email,status ,last_seen , total_score, games_played, games_won, games_lost , draws)"
                    + "  values(?,?,?,?,NOW(),?,?,?,?,?)");
            PreparedStatement stmt = conn.prepareStatement(queryString);
            stmt.setString(1, username);
            stmt.setString(2, passwordEnc(pswd));
            stmt.setString(3, email);
            stmt.setString(4,"offline");
            stmt.setInt(5,  0);
            stmt.setInt(6, 0);
            stmt.setInt(7, 0);
            stmt.setInt(8, 0);
            stmt.setInt(9, 0);

            stmt.executeUpdate();
            System.out.println("Record successfully Inserted. ");
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
            return false;
        }

        conn.close();

        return true;
    }
    // login With username
    public boolean logIn(String username, String pswd) throws SQLException {
            String passwordEncrypted = passwordEnc(pswd);
         try {
             conn = DriverManager.getConnection(url, user, password);
            Statement selectStatement = conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE);
            String query = new String("select username, password,status from players where username='" + username +"'");
            ResultSet rs = selectStatement.executeQuery(query);
            rs.next();
            String retrievedPassword=rs.getString("password");
            if(retrievedPassword.equals(passwordEncrypted ) ){
                System.out.println("Logged in successfully");
                String updatingStatus=rs.getString("status");
                updatingStatus = "Online";
                rs.updateString("status", updatingStatus);
                selectStatement.close();
                conn.close();
            }else{
                System.out.println("The password you entered is incorrect");
                selectStatement.close();
                conn.close();
                return false;
            }
            
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
             System.out.println("The username you entered is not correct");
             conn.close();
            return false;
        }
         conn.close();
        return true;
    }
    // login With email
    public boolean logInUsingEmail(String email, String pswd) throws SQLException {
            String passwordEncrypted = passwordEnc(pswd);
            ResultSet rs;
         try {
             conn = DriverManager.getConnection(url, user, password);
            Statement selectStatement = conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE);
            String query = new String("select * from players where email='" + email +"'");
            rs = selectStatement.executeQuery(query);
            rs.next();
            String retrievedPassword=rs.getString("password");
            if(retrievedPassword.equals(passwordEncrypted ) ){
                System.out.println("Logged in successfully");
                String updatingStatus=rs.getString("status");
                updatingStatus = "Online";
                rs.updateString("status", updatingStatus);
                
                selectStatement.close();
                conn.close();
            }else{
                System.out.println("The password you entered is incorrect");
                selectStatement.close();
                conn.close();
                return false;
            }
            
        } catch (SQLException ex) {
            //can use it to print duplicate key error
            System.err.println(ex.getMessage());
             System.out.println("The email you entered is not correct");
             conn.close();
            return false;
        }
         conn.close();
        return true;
    }
    

    
}
