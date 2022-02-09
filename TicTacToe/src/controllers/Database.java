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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    //In database
    public ArrayList<String> getLeaderBoard () throws SQLException{
        String leaderBoard = new String();
        ArrayList<String> leaderList = new ArrayList<String>();
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement selectstatement = conn.createStatement();
            String leaderBoardQuery = new String("select id,username, status, last_seen,total_score, games_played, games_won, games_lost from players where username not in('Computer') order by total_score desc limit 10;");
            ResultSet rs = selectstatement.executeQuery(leaderBoardQuery);
            while(rs.next()){
                Date dateSql = rs.getDate("last_seen");
                String date = dateSql.toString();
                 leaderBoard=String.valueOf(rs.getInt("id")) + ":" + rs.getString("username") + ":" + rs.getString("status") + ":" + date + ":" + String.valueOf(rs.getInt("total_score")) + ":" + String.valueOf(rs.getInt("games_played")) + ":" + String.valueOf(rs.getInt("games_won")) + ":" + String.valueOf(rs.getInt("games_lost"));
                leaderList.add(leaderBoard);
            }
            selectstatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            conn.close();
        }
        conn.close();
        return leaderList;
        
    }
    public ArrayList<String> allPlayers() throws SQLException {

        String allPlayers = new String();
        ArrayList<String> playerList = new ArrayList<String>();
        playerList.add(allPlayers);
        try {
            conn = DriverManager.getConnection(url, user, password);
            Statement selectstatement = conn.createStatement();
            String leaderBoardQuery = new String("select id,username, status, last_seen,total_score, games_played, games_won, games_lost from players where username not in('Computer') order by total_score desc;");
            ResultSet rs = selectstatement.executeQuery(leaderBoardQuery);
            while (rs.next()) {
                Date dateSql = rs.getDate("last_seen");
                String date = dateSql.toString();
                allPlayers =  String.valueOf(rs.getInt("id")) + ":" + rs.getString("username") + ":" + rs.getString("status") + ":" + date + ":" + String.valueOf(rs.getInt("total_score")) + ":" + String.valueOf(rs.getInt("games_played")) + ":" + String.valueOf(rs.getInt("games_won")) + ":" + String.valueOf(rs.getInt("games_lost"));
                playerList.add(allPlayers);
            }
            selectstatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            conn.close();
        }
        conn.close();
        return playerList;

    }
    public boolean createGame(String playerOneName, String playerTwoName){
        String mode;
        if(playerTwoName == "Computer"){
            mode = new String("Single player");
        }else{
            mode = new String("MultiPlayer");
        }
        String query = new String("insert into games(player_one_name, player_two_name, game_state, game_mode, game_result, winner) values(?, ?, 'ongoing', ?, 'TBD','TBD')");
        
        try {
            conn = DriverManager.getConnection(url, user, password);
            PreparedStatement createGameStatement = conn.prepareCall(query);
            createGameStatement.setString(1, playerOneName);
            createGameStatement.setString(2, playerTwoName);
            createGameStatement.setString(3, mode);
            createGameStatement.executeUpdate();
            createGameStatement.close();
           
        } catch (SQLException ex) {
           
             System.err.println(ex.getMessage());
            return false;
            
        }
        try {
            Statement selectUserStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs= selectUserStatement.executeQuery("select * from players where username='" +playerOneName +"'");
            rs.next();
            int games = rs.getInt("games_played")+1;
            rs.updateInt("games_played", games);
            rs.updateRow();
            selectUserStatement.close();
          conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }
    public boolean updateGame(String winner, String loser, int gameID){
        String query = new String("update games set game_state= ?,game_result=?, winner= ? where game_id =?");
        try {
            conn = DriverManager.getConnection(url, user, password);
            PreparedStatement updateGameStatement = conn.prepareStatement(query);
            updateGameStatement.setString(1, "Finished");
            updateGameStatement.setString(2, "Resolved");
            updateGameStatement.setString(3, winner);
            updateGameStatement.setInt(4, gameID);
            updateGameStatement.executeUpdate();
            updateGameStatement.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
       try{
           // updating the winner
           Statement selectPlayerStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
           ResultSet rs =selectPlayerStatement.executeQuery("select * from players where username='" + winner + "'");
           rs.next();
           int gamesWon = rs.getInt("games_won")+1;
           int score = rs.getInt("total_score")+3;
           rs.updateInt("total_score", score);
           rs.updateInt("games_won", gamesWon);
           rs.updateRow();
           // updating the loser
           if((!loser.equals("Computer"))){
           rs = selectPlayerStatement.executeQuery("select * from players where username='" + loser + "'");
           rs.next();
           int gamesLost = rs.getInt("games_Lost")+1;
           rs.updateInt("games_Lost", gamesLost);
           rs.updateRow();
           }
           
           selectPlayerStatement.close();
           conn.close();
       }catch(SQLException ex){
           ex.printStackTrace();
            System.err.println(ex.getMessage());
       }
       
        return true;
    }
    public boolean draw(String playerOne, String PlayerTwo, int gameID){
        try {
            conn = DriverManager.getConnection(url, user, password);
             String query = new String("update games set game_state= ?,game_result=?, winner= ? where game_id =?");
            PreparedStatement drawGameStatement = conn.prepareStatement(query);
            drawGameStatement.setString(1, "Finished");
            drawGameStatement.setString(2, "Draw");
            drawGameStatement.setString(3, "None");
            drawGameStatement.setInt(4, gameID);
            drawGameStatement.executeUpdate();
            drawGameStatement.close();
            query = "select * from players where username='" + playerOne + "'";
            Statement updatePlayersStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = updatePlayersStatement.executeQuery(query);
            rs.next();
            int draws=rs.getInt("draws") +1 ;
            int score = rs.getInt("total_score")+1;
            rs.updateInt("total_score", score);
            rs.updateInt("draws", draws);
            rs.updateRow();
            if(!(PlayerTwo.equals("Computer"))){
            rs = updatePlayersStatement.executeQuery("select * from players where username='" + PlayerTwo + "'");
            rs.next();
            int drawsSec = rs.getInt("draws")+1;
            int scoreSec = rs.getInt("total_score")+1;
            rs.updateInt("total_score", scoreSec);
            rs.updateInt("draws", drawsSec);
            rs.updateRow();
            }
            updatePlayersStatement.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return true;
    }

    
}
